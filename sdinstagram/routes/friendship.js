const {ObjectId} = require("mongodb");
module.exports = function (app, friendshipRepository, friendshipRequestRepository) {

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

}