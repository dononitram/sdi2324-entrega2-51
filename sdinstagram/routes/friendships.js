const {ObjectId} = require("mongodb");
module.exports = function (app, friendshipRepository, friendshipRequestRepository, usersRepository, publicationsRepository) {

    app.post('/request/send/:id', function (req, res) {
        let receiver = new ObjectId(req.params.id);
        let requester = req.session.user;
        let friendshipRequest = {
            receiver: receiver,
            requester: requester,
            date: new Date()
        }
        friendshipRequestRepository.insertFriendshipRequest(friendshipRequest).then(result =>{
            if (result.insertedId === null || typeof (result.insertedId) === undefined){
                res.redirect("" + '?message=There was an error sending the friend request.'+
                    "&messageType=alert-danger"); //completar con ruta al listado de usuarios
            }
            else {
                res.redirect("" + '?message=Invitation successfully sent.'+
                    "&messageType=alert-info"); //completar con ruta al listado de usuarios
            }
        });
    });

    app.get('/requests', function (req, res){
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }

        let filter = {receiver: req.session.user};
        let options = {projection: {_id: 0, receiver:0, requester: 1, date: 1}};
        friendshipRequestRepository.getFriendshipRequestsPg(filter, options, page).then(result => {
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

            const requesters = result.requests.map(request => request.requester);
            const dates = result.requests.map(request => request.date);
            let filter = {"_id": {$in: requesters}}
            let options = {};
            usersRepository.getUsers(filter, options).then(requesters => {
                res.render("requests.twig", {requesters: requesters, dates:dates, pages:pages, currentPage: page});
            }).catch(error => {
                res.redirect("" + '?message=There has been an error listing the senders of the friendship requests.'+
                    "&messageType=alert-danger"); //completar con ruta a una página home
            });
        }).catch(error => {
            res.redirect("" + '?message=There has been an error listing the friendship requests.'+
                "&messageType=alert-danger"); //completar con ruta a una página home
        });
    });

    app.post('/request/accept/:id', function (req, res) {
        let filter = {receiver: new ObjectId(req.params.id)};
        let options = {};
        friendshipRequestRepository.deleteFriendshipRequest(filter, options).then(result => {
            if (result === null || result.deletedCount === 0) {
                res.redirect("" + '?message=There has been an error accepting the friendship invitation.'+
                    "&messageType=alert-danger");
            } else {
                res.redirect("/requests" + '?message=Invitation correctly accepted.'+
                    "&messageType=alert-info");
            }
        }).catch(error => {
            res.send("There has been an error accepting the friendship invitation: " + error)
        });

        let requester = new ObjectId(req.params.id);
        let receiver = req.session.user;
        let friendship = {
            user1: receiver,
            user2: requester,
            date: new Date()
        }
        friendshipRepository.insertFriendship(friendship).then(result =>{
            if (result.insertedId === null || typeof (result.insertedId) === undefined){
                res.redirect("/requests" + '?message=There was an error accepting the friendship request.'+
                    "&messageType=alert-danger"); //completar con ruta al listado de usuarios
            }
        })

    });

    app.get('/friendships/:id', function (req, res) {
        let filter = {_id: new ObjectId(req.params.id)};
        let options = {};
        friendshipRepository.findFriend(filter, options).then(friend => {
            let filter = {_id: new ObjectId(req.params.id)};
            let options = {};
            const publications = publicationsRepository.findPublications(filter, options)
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