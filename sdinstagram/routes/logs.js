const {ObjectId} = require("mongodb");
module.exports = function (app, logsRepository) {

    app.get("/logs", function(req, res) {
        
        let filter = {type: req.query.type};
        let options = {sort: {date:-1}};

        if(req.query.type == null || req.query.type == undefined || req.query.type == "ALL")
            filter = {};

        logsRepository.getLogs(filter, options).then(result => {
            let response = {
                logs: result,
                user: req.session.user,
                selectedType: req.query.type
            }
            res.render("logs.twig", response);
        }).catch(error => {
            res.render("logs.twig?message=Error getting logs&messageType=alert-danger", {user: req.session.user});
        });
    });

    app.get("/logs/delete/:type", function(req, res) {

        let filter = {type: req.params.type};
        let options = {};

        if (req.params.type == null || req.params.type == undefined || req.params.type == "ALL")
            filter = {};

        logsRepository.deleteLogs(filter, options).then(result => {
            res.redirect("/logs");
        }).catch(error => {
            console.log(error);
            res.redirect("/logs.twig?message=Error deleting log&messageType=alert-danger");
        });
    });

}