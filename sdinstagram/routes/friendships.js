const {ObjectId} = require("mongodb");
module.exports = function (app, friendshipRepository, friendshipRequestRepository, usersRepository, publicationsRepository) {

    app.post('/friendships/request/send/:id', function (req, res) {
        let receiverID = new ObjectId(req.params.id);
        let requester = req.session.user;
        usersRepository.findUser({_id:receiverID},{}).then(receiver => {

            let friendshipRequest = {
                receiver: receiver,
                requester: requester,
                date: new Date()
            }
            friendshipRequestRepository.insertFriendshipRequest(friendshipRequest).then(result =>{
                if (result.insertedId === null || typeof (result.insertedId) === undefined){
                    res.redirect("/users/social" + '?message=There was an error sending the friend request.'+
                        "&messageType=alert-danger");
                }
                else {
                    res.redirect("/users/social" + '?message=Invitation successfully sent.'+
                        "&messageType=alert-info");
                }
            });
        });
    });

    app.get('/friendships/requests', function (req, res){
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }

        let filter = {'receiver._id': new ObjectId(req.session.user._id)};
        let options = {};
        friendshipRequestRepository.getFriendshipRequestsPg(filter, options).then(result => {

            let lastPage = result.total / 5;
            if (result.total % 5 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }

            res.render("friendships/requests.twig", {requests: result.requests, pages:pages, currentPage: page});
        }).catch(error => {
            res.redirect("/publications" + '?message=There has been an error listing the friendship requests.' +
                "&messageType=alert-danger");
        });
    });

    app.post('/friendships/request/accept/:id/:requester', function (req, res) {
        let filter = {_id: new ObjectId(req.params.id)};
        let options = {};
        friendshipRequestRepository.deleteFriendshipRequest(filter, options).then(result => {
            if (result === null || result.deletedCount === 0) {
                res.redirect("/friendships/requests" + '?message=There has been an error accepting the friendship invitation.'+
                    "&messageType=alert-danger");
            }
        }).catch(error => {
            res.redirect("/friendships/requests" + '?message=There has been an error accepting the friendship invitation.'+ error +
                "&messageType=alert-danger");
        });

        let requesterId = new ObjectId(req.params.requester);
        usersRepository.findUser({_id:requesterId},{}).then(requester => {
            let receiver = req.session.user;
            let friendship = {
                user1: receiver,
                user2: requester,
                date: new Date()
            }
            friendshipRepository.insertFriendship(friendship).then(result =>{
                if (result.insertedId === null || typeof (result.insertedId) === undefined){
                    res.redirect("/friendships/requests" + '?message=There was an error accepting the friendship request.'+
                        "&messageType=alert-danger"); //completar con ruta al listado de usuarios
                } else {
                    res.redirect("/friendships/requests" + '?message=Invitation correctly accepted.'+
                        "&messageType=alert-info");
                }
            });
        });
    });

    /**
     * Shows all the friends of the user in session
     */
    app.get('/friendships/', async function (req, res) {
        //TO-DO PEDRO FILTRO
        try {
            let connectedUser = req.session.user;
            if (!connectedUser || !connectedUser._id) {
                res.send("Error you have to be logged to see users");
                return;
            }
        let userId = new ObjectId(connectedUser._id);
        let filter =
            {
                _id: { $ne: userId },
            };
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { //
            //Puede no venir el param
            page = 1;
        }
        const result = await friendshipRepository.getFriendshipsPg(filter, {}, page);

        let lastPage = result.total / 5;
        if (result.total % 5 > 0) { // Sobran decimales
            lastPage = lastPage + 1;
        }
        let pages = []; // paginas mostrar
        for (let i = page - 2; i <= page + 2; i++) {
            if (i > 0 && i <= lastPage) {
                pages.push(i);
            }
        }

        let response = {
            friends: result.friends,
            pages: pages,
            currentPage: page
        };

        //Buscar su última publicación

        res.render("friendships/friends.twig", response);

        }catch (error) {
            res.send("Error when making the user friend's list")
        }
    });

    app.get('/friendships/:id', function (req, res) {
        let filter = {_id: new ObjectId(req.params.id)};
        let options = {};
        friendshipRepository.findFriend(filter, options).then(friend => {
            let filter = {_id: new ObjectId(req.params.id)};
            let options = {};
            publicationsRepository.findPublications(filter, options)
                .then(publications => {
                    res.render("friendships/friend.twig", {friend:friend, publications:publications});
                })
                .catch(error => {
                    res.render("friendships/friend.twig", {friend: friend});
                })
        }).catch(error => {
            res.send("Se ha producido un error al buscar la canción " + error)
        });
    });
}