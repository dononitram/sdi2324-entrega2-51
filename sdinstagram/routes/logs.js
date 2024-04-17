const {ObjectId} = require("mongodb");
module.exports = function (app, logsRepository) {

    app.get("/logs/list", function(req, res) {
        let filter = {};
        let options = {sort: {date:-1}};
        if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != ""){
            filter = {"action": {$regex: ".*" + req.query.search + ".*"}};
        }
        logsRepository.getLogs(filter, options).then(result => {
            let response = {
                logs: result,
                user: req.session.user
            }
            res.render("logs.twig", response);
        }).catch(error => {
            res.render("index.twig?message=Error getting logs&messageType=alert-danger");
        });
    });

}