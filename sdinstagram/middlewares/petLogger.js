const express = require('express');
loggerRouter = function (logsRepository) {
    
    const innerRouter = express.Router();
    innerRouter.use(function (req, res, next) {

        let log = {
            date: Date.now(),
            type: "PET",
            description: req.method + " " + req.originalUrl,
        }
        
        logsRepository.insertLog(log).then(result => {
            next();
        })
        .catch(error => {
            console.log(error);
            console.log("Petition could not be registered " + req.originalUrl)
            next();
        });
        
    });

    return innerRouter;
}

module.exports = loggerRouter;