const {ObjectId} = require("mongodb");
module.exports = function (app, publicationsRepository) {
    app.get("/publications", function(req, res) {
        let filter = {};
        let options = {sort: {title: 1}};

    });

    app.get('/publications/add', function (req, res) {
        res.render("publications/add.twig");
    });

    app.post('/publications/add',function (req,res) {
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
                if (req.files != null) {
                    let image = req.files.cover;
                    image.mv(app.get("uploadPath") + '/public/covers/' + result.toString() + '.png')
                        .then(() => {
                            if (req.files.audio != null) {
                                let audio = req.files.audio;
                                audio.mv(app.get("uploadPath") + '/public/audios/' + result.toString() + '.mp3')
                                    .then(res.redirect("/publications"))
                                    .catch(error => res.send("Error al subir el audio de la canción"))
                            }else {
                                //res.send("Agregada la canción ID: " + result.toString())
                                res.redirect("/publications");
                            }
                        })
                        .catch(error => res.send("Error al subir la portada de la canción") )
                } else {
                    res.send("Agregada la canción ID: " + result.toString())
                }
            } else {
                res.send("Error when inserting new publication " + result.error);
            }
        });
    });

}