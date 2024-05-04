const {ObjectId} = require("mongodb");
module.exports = function (app, logsRepository) {

    /**
     * GET /logs
     * Retrieves and displays logs filtered by type, with pagination and sorting by date in descending order.
     * If no specific type is provided or if 'ALL' is selected, it fetches all logs.
     * Renders the logs using the 'logs.twig' template with appropriate messaging.
     */
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

    /**
     * GET /logs/delete/:type
     * Deletes logs of a specified type. If the type is 'ALL' or not specified, it deletes all logs.
     * Redirects to the logs page with an appropriate message indicating the outcome.
     */
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