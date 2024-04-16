const express = require('express');
const path = require("path");
const {ObjectId} = require("mongodb");
const publicationsRepository = require("../repositories/publicationsRepository");
const userFriendRouter = express.Router();

userFriendRouter.use(function (req, res, next) {
    console.log("userFriendRouter");
    let publicationId = path.basename(req.originalUrl);
    let filter = {_id: new ObjectId(publicationId)};
    publicationsRepository.findPublication(filter, {}).then(publication => {
        if(req.session.user && publication.author === req.session.user) {
            next();
        } else {
            res.redirect("/publications");
        }
    }).catch(error => {
        res.redirect("/publications");
    })
});

module.exports = userFriendRouter;