const express = require('express');
const path = require("path");
const {ObjectId} = require("mongodb");
const friendshipRequestRepository = require("../repositories/friendshipRequestsRepository");
const friendshipRepository = require("../repositories/friendshipsRepository");
const usersRepository = require("../repositories/usersRepository")
const friendshipRequestsRouter = express.Router();
friendshipRequestsRouter.use(function (req,
                                       res, next) {
    receiverId = new ObjectId(path.basename(req.originalUrl));
    requester = req.session.user
    requester._id = new ObjectId(requester._id)
    usersRepository.findUser({_id:receiverId}, {}).then(receiver => {
        let filter = {receiver: receiver, requester: requester};
        let options = {};
        friendshipRequestRepository.findFriendshipRequest(filter, options).then(result => {
            if (result === null || typeof (result) === undefined){
                let filter = {user1: receiver, user2: requester}
                let options = {};
                friendshipRepository.findFriend(filter, options).then(result => {
                    if (result === null || typeof (result) === undefined) {
                        let filter = {user1: requester, user2: receiver}
                        let options = {};
                        friendshipRepository.findFriend(filter, options).then(result => {
                            if (result === null || typeof (result) === undefined) {
                                next();
                            }
                            else {
                                res.redirect("/users/social" + '?message=You are already friends.'+
                                    "&messageType=alert-danger");
                            }
                        });
                    }
                    else {
                        res.redirect("/users/social" + '?message=You are already friends.'+
                            "&messageType=alert-danger");
                    }
                });
            }
            else {
                res.redirect("/users/social" + '?message=You have already sent a friend request to this user.'+
                    "&messageType=alert-danger");
            }
        });
    });
});
module.exports = friendshipRequestsRouter;
