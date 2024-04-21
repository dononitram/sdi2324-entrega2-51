const {ObjectId} = require('mongodb');
const conversationsRepository = require("../../repositories/conversationsRepository");
module.exports = function (app, usersRepository, friendshipRepository, friendshipRequestRepository, publicationsRepository
                           , conversationsRepository) {

    app.post('/api/v1.0/users/login', function (req, res) {

        try {
            let securePassword = app.get("crypto").createHmac("sha256", app.get('clave'))
                .update(req.body.password).digest("hex");

            let filter = {
                email: req.body.email,
                password: securePassword
            }

            let options = {};

            usersRepository.findUser(filter,options).then(user => {
                if (user == null) {
                    res.status(401); // Unauthorized
                    res.json({
                      message: "usuario no autorizado",
                      authenticated: false
                    })
                } else {
                    let token = app.get('jwt').sign(
                        {user: user.email, time: Date.now() / 1000}, "secreto"
                    );

                    const jwt = require('jsonwebtoken');
                    try {
                        let decoded = jwt.verify(token, 'secreto'); // replace 'secreto' with your secret key
                        req.session.user = decoded.user;
                    } catch (err) {
                        res.status(401).json({ error: 'Invalid token' });
                    }

                    res.status(200);
                    res.json({
                        message: "usuario autorizado",
                        authenticated: true,
                        token: token
                    })

                }
            }).catch(error => {
                res.status(401);
                console.log(error);
                res.json({
                    message: "Se ha producido un error al verificar las credenciales",
                    authenticated: false
                })
            })
        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({
                message: "Se ha producido un error al verificar credenciales",
                authenticated: false
            })
        }

    });

    app.get('/api/v1.0/conversation/:user', function (req, res) {
        try {
            let user1ID = new ObjectId(req.params.user1);
            let user2 = req.session.user;
            user2 = new ObjectId(user2._id);

            usersRepository.findUser({_id:user1ID},{}).then(user1 => {

                let filter = {user1: user1, user2: user2};
                let options = {}
                conversationsRepository.findConversation(filter, options).then(conversation => {

                    if (conversation === null || typeof conversation === "undefined") {
                        let filter = {user1: user2, user2: user1};
                        let options = {}
                        conversationsRepository.findConversation(filter, options).then(conversation => {

                            if (conversation === null || typeof conversation === "undefined") {
                                res.status(404);
                                res.json({error: "No exite conversación con el usuario especificado"})
                            }
                            else{
                                res.status(200);
                                res.json({conversation: conversation});
                            }
                        });
                    }
                    else {
                        res.status(200);
                        res.json({conversation: conversation});
                    }
                });
            });
        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({error: "Se ha producido un error :" + e})
        }
    });

    app.get('/api/v1.0/conversations/', function (req, res) {
        try {
            let user1 = req.session.user;
            let filter = {user1: user1};
            let options= {};
            conversationsRepository.getConversations(filter, options).then(conversations => {

                    if (conversations === null || typeof conversations === "undefined" || conversations.length === 0) {
                        let filter = {user2: user1};
                        let options = {}
                        conversationsRepository.findConversation(filter, options).then(conversations => {

                            res.status(200);
                            res.json({conversations: conversations});
                        });
                    }
                    else {
                        res.status(200);
                        res.json({conversations: conversations});
                    }
                });
        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({error: "Se ha producido un error :" + e})
        }
    });

}