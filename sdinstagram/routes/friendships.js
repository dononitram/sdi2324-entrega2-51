const { ObjectId } = require("mongodb");

module.exports = function(app, friendshipRepository, friendshipRequestRepository, usersRepository, publicationsRepository) {

    /**
     * POST /friendships/request/send/:id
     * Sends a friendship request to the user specified by :id.
     * Redirects with a message indicating success or failure of the request initiation.
     */
    app.post('/friendships/request/send/:id', function(req, res) {
        let receiverID = new ObjectId(req.params.id);
        let requester = req.session.user;
        usersRepository.findUser({ _id: receiverID }, {}).then(receiver => {

            let friendshipRequest = {
                receiver: receiver,
                requester: requester,
                date: new Date()
            }
            friendshipRequestRepository.insertFriendshipRequest(friendshipRequest).then(result => {
                if (result.insertedId === null || typeof(result.insertedId) === undefined) {
                    res.redirect("/users/social" + '?message=There was an error sending the friend request.' +
                        "&messageType=alert-danger");
                } else {
                    res.redirect("/users/social" + '?message=Invitation successfully sent.' +
                        "&messageType=alert-info");
                }
            });
        });
    });

    /**
     * GET /friendships/requests
     * Lists the friendship requests for the logged-in user, with pagination support.
     * Displays the list using the 'friendships/requests.twig' template.
     */
    app.get('/friendships/requests', function(req, res) {
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }

        let filter = { 'receiver._id': new ObjectId(req.session.user._id) };
        let options = {};
        friendshipRequestRepository.getFriendshipRequestsPg(filter, options).then(result => {

            let lastPage = Math.ceil(result.total / 5);
            let pages = [];
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }

            res.render("friendships/requests.twig", { requests: result.requests, pages: pages, currentPage: page, user: req.session.user });
        }).catch(error => {
            res.redirect("/publications" + '?message=There has been an error listing the friendship requests.' +
                "&messageType=alert-danger");
        });
    });

    /**
     * POST /friendships/request/accept/:id/:requester
     * Accepts a friendship request identified by :id and :requester.
     * Redirects with a message indicating whether the acceptance was successful or failed.
     */
    app.post('/friendships/request/accept/:id/:requester', function(req, res) {
        let filter = { _id: new ObjectId(req.params.id) };
        let options = {};
        friendshipRequestRepository.deleteFriendshipRequest(filter, options).then(result => {
            if (result === null || result.deletedCount === 0) {
                res.redirect("/friendships/requests" + '?message=There has been an error accepting the friendship invitation.' +
                    "&messageType=alert-danger");
            }
        }).catch(error => {
            res.redirect("/friendships/requests" + '?message=There has been an error accepting the friendship invitation.' + error +
                "&messageType=alert-danger");
        });

        let requesterId = new ObjectId(req.params.requester);
        usersRepository.findUser({ _id: requesterId }, {}).then(requester => {
            let receiver = req.session.user;
            receiver._id = new ObjectId(receiver._id);
            let friendship = {
                user1: receiver,
                user2: requester,
                date: new Date()
            }
            friendshipRepository.insertFriendship(friendship).then(result => {
                if (result.insertedId === null || typeof(result.insertedId) === undefined) {
                    res.redirect("/friendships/requests" + '?message=There was an error accepting the friendship request.' +
                        "&messageType=alert-danger");
                } else {
                    res.redirect("/friendships/requests" + '?message=Invitation correctly accepted.' +
                        "&messageType=alert-info");
                }
            });
        });
    });

    /**
     * GET /friendships/
     * Shows all the friends of the logged-in user, along with the start date of the friendship and the latest publication by each friend.
     * Uses pagination and displays the list using the 'friendships/friends.twig' template.
     */
    app.get('/friendships/', async function(req, res) {
        try {
            let connectedUser = req.session.user;
            if (!connectedUser || !connectedUser._id) {
                res.send("Error you have to be logged to see users");
                return;
            }
            let userId = new ObjectId(connectedUser._id);
            let filter = {
                $or: [
                    { "user1._id": userId },
                    { "user2._id": userId }
                ]
            };

            let page = parseInt(req.query.page);
            if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
                page = 1;
            }
            const result = await friendshipRepository.getFriendshipsPg(filter, {}, page);

            let lastPage = Math.ceil(result.total / 5);
            let pages = [];
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }

            let filteredFriendship = [];
            result.friendships.forEach(friendRelation => {
                let str = friendRelation.user1._id.toString();
                if (str === connectedUser._id) {
                    let day = friendRelation.date.getDate();
                    let month = friendRelation.date.toLocaleDateString('en-GB', { month: 'short' });
                    let year = friendRelation.date.getFullYear();

                    friendRelation.user2.initFriendship = `${day} ${month} ${year}`;
                    filteredFriendship.push(friendRelation.user2)
                } else {
                    let day = friendRelation.date.getDate();
                    let month = friendRelation.date.toLocaleDateString('en-GB', { month: 'short' });
                    let year = friendRelation.date.getFullYear();

                    friendRelation.user1.initFriendship = `${day} ${month} ${year}`;
                    filteredFriendship.push(friendRelation.user1)
                }
            })
            for (const friend of filteredFriendship) {
                let filter2 = {
                    "author._id": friend._id
                };
                const publication = await publicationsRepository.getLastPublicationOf(filter2, {});
                if (publication.length === 0) {
                    friend.lastPublicationTitle = "Nothing published";
                } else {
                    friend.lastPublicationTitle = publication[0].title;
                }
            }

            let response = {
                friends: filteredFriendship,
                pages: pages,
                currentPage: page,
                user: req.session.user
            };

            res.render("friendships/friends.twig", response);

        } catch (error) {
            res.send("Error when making the user friend's list")
        }
    });

    /**
     * GET /friendships/:email
     * Retrieves the friendship information and the publications of the user identified by :email.
     * Displays the details using the 'friendships/friend.twig' template or redirects with an error message if no friendship is found.
     */
    app.get('/friendships/:email', function (req, res) {
        let connectedUser = req.session.user;
        if (!connectedUser || !connectedUser._id) {
            res.send("Error you have to be logged to see users");
            return;
        }
        let filter = {email: req.params.email};
        let options = {};
        usersRepository.findUser(filter, options).then(friend => {
            if(typeof friend !== "undefined" &&  friend !== null) {
                let filter =
                    {
                        $or: [
                            {"user1._id": new ObjectId(friend._id)},
                            {"user2._id": new ObjectId(friend._id)}
                        ]
                    };
                let options = {};

                friendshipRepository.findFriendship(filter, options).then(friendship => {
                    if(typeof friendship !== "undefined" &&  friendship !== null) {
                        let filter;
                        let friend;
                        if(friendship.user1._id.toString() !== req.session.user._id) {
                            filter = {"author._id": friendship.user1._id.toString()};
                            friend = friendship.user1;
                        } else {
                            filter = {"author._id": friendship.user2._id.toString()};
                            friend = friendship.user2;
                        }
                        let options = {sort: {title:1}};
                        let page = parseInt(req.query.page); // Es String !!!
                        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
                            //Param can be inexistent
                            page = 1;
                        }
                        publicationsRepository.getPublicationsPg(filter, options, page)
                            .then(result => {
                                let lastPage = result.total / 4;
                                if (result.total % 4 > 0) { // Sobran decimales
                                    lastPage = lastPage + 1;
                                }
                                let pages = []; // paginas mostrar
                                for (let i = page - 2; i <= page + 2; i++) {
                                    if (i > 0 && i <= lastPage) {
                                        pages.push(i);
                                    }
                                }

                                console.log(result.publications)
                                res.render("friendships/friend.twig",
                                    { friendship:friendship, friend:friend, publications:result.publications,
                                        pages: pages, currentPage: page, user: req.session.user});
                            })
                            .catch(error => {
                                res.render("friendships/friend.twig", {friend: friend});
                            })
                    } else {
                        //res.send("Error: you are not friends with this user");
                        res.redirect("/friendships/" + "?message=You are not friends with this user: "+friend.email + "&messageType=alert-danger");
                        return;
                    }

                }).catch(error => {
                    res.send("Error while looking for friendship " + error)
                })
            } else {
                res.redirect("/friendships/" + "?message=Friend does not exist" + "&messageType=alert-danger");
                return;
            }

        });
    });
}