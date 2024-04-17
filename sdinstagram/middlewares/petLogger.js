const express = require('express');
loggerRouter = function (logsRepository) {
    
    const innerRouter = express.Router();
    innerRouter.use(function (req, res, next) {

        let log = {
            date: Date.now(),
            action: req.method,
            url: req.originalUrl,
            type: "PET",
        }
        
        logsRepository.insertLog(log).catch(error => {
            console.log("No se ha podido registrar la peticion " + req.method)
        });
        
        next();
    });

    return innerRouter;
}

module.exports = loggerRouter;