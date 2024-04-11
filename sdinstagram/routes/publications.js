const {ObjectId} = require("mongodb");
module.exports = function (app, publicationsRepository) {
    app.get("/publications", function(req, res) {
        let filter = {};
        let options = {sort: {title:1}};
        if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != ""){
            filter = {"title": {$regex: ".*" + req.query.search + ".*"}};
        }
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            //Puede no venir el param
            page = 1;
        }

        publicationsRepository.getPublicationsPg(filter, options, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let response = {
                publications: result.publications,
                pages: pages,
                currentPage: page
            }
            res.render("publications.twig", response);
        }).catch(error => {
            res.render("Error when listing publications "+ error)
        });
    });

    app.get('/publications/add', function (req, res) {
        res.render("publications/add.twig");
    });

    app.post('/publications/add',function (req,res) {
        console.log(req);
        if(req.session.user == null || typeof(req.session.user) == "undefined") {
            res.send("Error when inserting new publication: Invalid user");
            return;
        }
        if(req.body.title == null || typeof(req.body.title) == "undefined"
            || req.body.title.length() <= 4) {
            res.send("Error when inserting new publication: Invalid title " + req.body.title);
            return;
        }
        if(req.body.description == null || typeof(req.body.description) == "undefined"
            || req.body.description.length() <= 4) {
            res.send("Error when inserting new publication: Invalid description");
            return;
        }
        let publication = {
            title: req.body.title,
            description: req.body.description,
            author: req.session.user,
            date: new Date()
        }
        publicationsRepository.insertPublication(publication, function (result) {
            if (result.toString() !== null && result.toString() !== undefined) {
                //res.send("Agregada la canción ID: " + result.songId);
                res.redirect("/publications")
            } else {
                res.send("Error when inserting new publication " + result.error);
            }
        });
    });

}