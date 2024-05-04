const { ObjectId } = require("mongodb");

module.exports = function (app, publicationsRepository) {

    // Route to retrieve publications
    app.get("/publications", function(req, res) {
        let filter = {};
        let options = { sort: { title: 1 } };

        // Check if there's a search query parameter
        if (req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != "") {
            filter = { "title": { $regex: ".*" + req.query.search + ".*" } };
        }

        // Parse page number (string to integer)
        let page = parseInt(req.query.page); // It's a String !!!

        // Handle undefined or null page parameter
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            // Parameter can be inexistent
            page = 1;
        }

        // Retrieve publications with pagination
        publicationsRepository.getPublicationsPg(filter, options, page)
            .then(result => {
                let lastPage = result.total / 4;

                // Check for remaining decimals
                if (result.total % 4 > 0) { // Decimals remain
                    lastPage = lastPage + 1;
                }

                let pages = []; // Pages to display
                for (let i = page - 2; i <= page + 2; i++) {
                    if (i > 0 && i <= lastPage) {
                        pages.push(i);
                    }
                }
                let response = {
                    publications: result.publications,
                    pages: pages,
                    currentPage: page,
                    user: req.session.user
                }
                res.render("publications.twig", response);
            })
            .catch(error => {
                // Render error template
                res.render("Error when listing publications " + error, { user: req.session.user }) // TODO: Are you sure about this?
            });
    });

    // Route to render add publication form
    app.get('/publications/add', function (req, res) {
        res.render("publications/add.twig");
    });

    // Route to handle form submission for adding new publication
    app.post('/publications/add', function (req, res) {
        // Check if user is logged in
        if (req.session.user == null || typeof(req.session.user) == "undefined") {
            let error = "Error when inserting new publication: Invalid user";
            res.redirect("/publications/add" + "?message=" + error + "&messageType=alert-danger");
            return;
        }
        // Validate title
        if (req.body.title == null || typeof(req.body.title) == "undefined"
            || req.body.title.length <= 4) {
            let error = "Error when inserting new publication: Title must be at least 4 characters long.";
            res.redirect("/publications/add" + "?message=" + error + "&messageType=alert-danger");
            return;
        }
        // Validate description
        if (req.body.description == null || typeof(req.body.description) == "undefined"
            || req.body.description.length <= 4) {
            let error = "Error when inserting new publication: Description must be at least 4 characters long.";
            res.redirect("/publications/add" + "?message=" + error + "&messageType=alert-danger");
            return;
        }
        // Create new publication object
        let publication = {
            title: req.body.title,
            description: req.body.description,
            author: req.session.user,
            date: getFormattedDate()
        }

        console.log(publication);
        // Insert new publication
        publicationsRepository.insertPublication(publication, function (result) {
            if (result.toString() !== null && result.toString() !== undefined) {
                // Redirect to publications page after successful insertion
                res.redirect("/publications")
            } else {
                // Render error if insertion fails
                res.send("Error when inserting new publication " + result.error);
            }
        });
    });

    // Function to get formatted date
    function getFormattedDate() {
        // Current Date
        const currentDate = new Date();

        // Get date components
        const day = currentDate.getDate();
        const month = currentDate.getMonth() + 1; // Months are zero-based, so add 1
        const year = currentDate.getFullYear();

        // Get time components
        const hours = currentDate.getHours();
        const minutes = currentDate.getMinutes();
        const seconds = currentDate.getSeconds();

        return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`;
    }
}