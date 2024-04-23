const { ObjectId } = require('mongodb');
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

            usersRepository.findUser(filter, options).then(user => {
                if (user == null) {
                    res.status(401); // Unauthorized
                    res.json({
                        message: "usuario no autorizado",
                        authenticated: false
                    })
                } else {
                    let token = app.get('jwt').sign(
                        { user: user.email, time: Date.now() / 1000 }, "secreto"
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

    app.get('/api/v1.0/users/friendships', function (req, res) {
        try {

            usersRepository.findUser({ email: res.user }, {}).then(user => {

                let filter = {$or: [{user1: user}, {user2: user}]};
                
                friendshipRepository.findFriendships(filter, {}).then(friendships => {
                    res.status(200);

                    let parsedFriendships = friendships.map(friendship => {
                        if (friendship.user1.email === user.email) {
                            return friendship.user2;
                        } else {
                            return friendship.user1;
                        }
                    }).map(friend => {
                        return {
                            _id: friend._id,
                            email: friend.email,
                            firstName: friend.firstName,
                            lastName: friend.lastName,
                        }
                    }
                    );

                    res.json({ friendships: parsedFriendships });
                });
            
            }).catch(error => {
                res.status(404);
                res.json({ error: "Error while finding user: " + error });
            });

        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({ error: "Se ha producido un error :" + e })
        }
    });

    app.get('/api/v1.0/conversation/:user', function (req, res) {
        try {
            let user1ID = new ObjectId(req.params.user1);
            let user2 = res.user;
            user2 = new ObjectId(user2._id);

            usersRepository.findUser({ _id: user1ID }, {}).then(user1 => {

                let filter = { user1: user1, user2: user2 };
                let options = {}
                conversationsRepository.findConversation(filter, options).then(conversation => {

                    if (conversation === null || typeof conversation === "undefined") {
                        let filter = { user1: user2, user2: user1 };
                        let options = {}
                        conversationsRepository.findConversation(filter, options).then(conversation => {

                            if (conversation === null || typeof conversation === "undefined") {
                                res.status(404);
                                res.json({ error: "No exite conversación con el usuario especificado" })
                            }
                            else {
                                res.status(200);
                                res.json({ conversation: conversation });
                            }
                        });
                    }
                    else {
                        res.status(200);
                        res.json({ conversation: conversation });
                    }
                });
            });
        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({ error: "Se ha producido un error :" + e })
        }
    });

    app.get('/api/v1.0/conversations/', function (req, res) {
        try {
            let user1 = res.user;
            let filter = {user1: user1};
            let options= {};
            conversationsRepository.getConversations(filter, options).then(conversations => {

                if (conversations === null || typeof conversations === "undefined" || conversations.length === 0) {
                    let filter = { user2: user1 };
                    let options = {}
                    conversationsRepository.findConversation(filter, options).then(conversations => {

                        res.status(200);
                        res.json({ conversations: conversations });
                    });
                }
                else {
                    res.status(200);
                    res.json({ conversations: conversations });
                }
            });
        } catch (e) {
            console.log(e);
            res.status(500);
            res.json({ error: "Se ha producido un error :" + e })
        }
    });

    app.post('/api/v1.0/conversation', function (req, res) {
        try {
            if (typeof req.body.friendId === "undefined" || req.body.friendId === null) {
                res.status(409);
                res.json({ error: "Cannot create conversation. Incorrect friend id" });
                return;
            }
            if (typeof req.body.writerEmail === "undefined" || req.body.friendId === null) {
                res.status(409);
                res.json({ error: "Cannot create conversation. Incorrect friend id" });
                return;
            }
            //let user1 = await usersRepository.findUser({email:req.body.sender},{});
            console.log(res);
            let convers = {
                user1: req.body.friend,
                user2: res.token
            }
            //Should look for users and check if they are friends
            usersRepository.findUser({ email: req.body.senderEmail }, {}).then(user1 => {
                let filter = { user1: user1, user2: user2 };
                let options = {}
                conversationsRepository.findConversation(filter, options).then(conversation => {
                    if (conversation === null || typeof conversation === "undefined") {
                        let filter = { user1: user2, user2: user1 };
                        let options = {}
                        conversationsRepository.findConversation(filter, options).then(conversation => {
                            // There is no conversation between this users
                            if (conversation === null || typeof conversation === "undefined") {
                                //A new conversation is created
                                let message = {
                                    author: user1,
                                    date: new Date(),
                                    text: req.body.message,
                                    read: false
                                }
                                let convers = {
                                    user1: user1,
                                    user2: user2,
                                    messages: [message]
                                }
                                conversationsRepository.insertConversation(convers, function (conversationId) {
                                    if (conversationId === null) {
                                        res.status(409);
                                        res.json({ error: "Could not create song" });
                                        return;

                                    } else {
                                        res.status(201);
                                        res.json({
                                            message: "Conversation created successfully",
                                            _id: conversationId
                                        })
                                    }
                                })
                            }
                            else {

                                res.status(200);
                                res.json({ conversation: conversation });
                            }
                        });
                    }
                    else {
                        res.status(200);
                        res.json({ conversation: conversation });
                    }
                });
            });
        } catch (e) {
            res.status(500);
            res.json({ error: "Error while creating conversation: " + e })
        }
    });

    app.delete('/api/v1.0/conversation/:id', async function (req, res) {

        /*
        token ok
        usuario es emisor o receptior
        borrar tos los mensajes
        */

        // Request param
        const id = req.params.id;
        if (!id)
            res.status(400).json({ error: "Conversation id is required" });

        // Find conversation
        try {
            const conversation = await conversationsRepository.findConversation({ _id: new ObjectId(id) });
            if (!conversation)
                res.status(404).json({ error: "Conversation not found" });
        } catch (e) {
            res.status(500).json({ error: "Error while finding conversation: " + e });
        }
        
        // TODO check if user is sender or receiver
        //TODO delete conver 



    });
}