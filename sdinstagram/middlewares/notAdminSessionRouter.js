const express = require('express');
const notAdminSessionRouter = express.Router();
notAdminSessionRouter.use(function(req, res, next) {
    console.log("notAdminSessionRouter");
    if ( req.session.user.role === "admin") {
        // dejamos correr la petición
        next();
    } else {
        console.log("va a: " + req.originalUrl);
        res.redirect("/index" + "?message=You couldn't access as an administrator.");
    }
});
module.exports = notAdminSessionRouter;