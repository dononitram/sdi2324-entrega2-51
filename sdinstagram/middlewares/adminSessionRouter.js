const express = require('express');
const userSessionRouter = express.Router();
userSessionRouter.use(function(req, res, next) {
    console.log("adminSessionRouter");
    console.log(req.body);
    if ( req.session.user.role === "admin") {
        // dejamos correr la petición
        next();
    } else {
        console.log("va a: " + req.originalUrl);
        res.redirect("/index" + "?message=You are not an administrator.");
    }
});
module.exports = userSessionRouter;