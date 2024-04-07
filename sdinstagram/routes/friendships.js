const {ObjectId} = require("mongodb");
module.exports = function (app, friendshipRepository, friendshipRequestRepository, usersRepository) {

    app.post('/request/send/:id', function (req, res) {
        receiver = new ObjectId(req.params.id);
        requester = req.session.user;

        let friendshipRequest = {
            receiver: receiver,
            requester: requester,
            date: new Date()
        }

        friendshipRequestRepository.insertFriendshipRequest(friendshipRequest).then(ressult =>{
            if (result.insertedId === null || typeof (result.insertedId) === undefined){
                res.redirect("" + '?message=Se ha producido un error al enviar la petición de amistad.'+
                    "&messageType=alert-danger") //completar con ruta al listado de usuarios
            }
        })

    });

    app.get('/requests', function (req, res){
        let filter = {receiver: req.session.user};
        let options = {projection: {_id: 0, receiver:0, requester: 1, date: 1}};
        friendshipRequestRepository.getFriendshipRequests(filter, options).then(requests => {
            const requesters = requests.map(request => request.requester);
            let filter = {"_id": {$in: requesters}}
            let options = {};
            usersRepository.getUsers(filter, options).then(requesters => {
                res.render("requests.twig", {requesters: requesters});
            }).catch(error => {
                res.redirect("" + '?message=Se ha producido un error al listar los emisores de las peticiones de amistad.'+
                    "&messageType=alert-danger") //completar con ruta a una página home
            });
        }).catch(error => {
            res.redirect("" + '?message=Se ha producido un error al listar las peticiones de amistad.'+
                "&messageType=alert-danger") //completar con ruta a una página home
        });
    });

}