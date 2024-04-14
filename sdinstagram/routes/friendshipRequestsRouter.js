const express = require('express');
const path = require("path");
const {ObjectId} = require("mongodb");
const friendshipRequestRepository = require("../repositories/friendshipRequestsRepository");
const friendshipRepository = require("../repositories/friendshipsRepository");
const friendshipRequestsRouter = express.Router();
friendshipRequestsRouter.use(function (req,
                                       res, next) {
    receiver = new ObjectId(path.basename(req.originalUrl));
    requester = req.session.user
    let filter = {'receiver._id': receiver, requester: requester};
    let options = {};
    friendshipRequestRepository.findFriendshipRequest(filter, options).then(result => {
        if (result === null || typeof (result) === undefined){
            let filter = {'user1._id': receiver, user2: requester}
            let options = {};
            friendshipRepository.findFriend(filter, options).then(result => {
                if (result === null || typeof (result) === undefined) {
                    let filter = {user1: requester, 'user2._id': receiver}
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
module.exports = friendshipRequestsRouter;
