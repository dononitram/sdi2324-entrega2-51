/**
 * Defines the routes and handlers for user-related operations.
 * @param {Object} app - The Express application object.
 * @param {Object} usersRepository - The repository object for user data access.
 */
const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository) {


	/**
	 * GET /users/system
	 * Returns a list of users.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users/system', function (req, res) {
		res.send('lista de usuarios sistema');
	})

	/**
	 * GET /users/social
	 * Returns a list of users except admins and the user logged in.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users/social', function (req, res) {
		let connectedUser = req.session.user;
		if(!connectedUser || !connectedUser._id){
			res.send("Error you have to be logged to see users");
			return;
		}
		let userId = new ObjectId(connectedUser._id);
		let filter =
			{
				role: { $ne:"admin"},
				_id:{ $ne: userId},
			};
		//Búsqueda
		let busquedaStr = "";//Vacía por defecto
		if(req.query.search != null && typeof(req.query.search) != "undefined" && req.query.search != ""){
			busquedaStr = req.query.search;
			filter.$or = [
				{"email": {$regex: ".*" + req.query.search + ".*"}},
				{"firstName": {$regex: ".*" + req.query.search + ".*"}},
				{"lastName": {$regex: ".*" + req.query.search + ".*"}}
			];
		}

		let page = parseInt(req.query.page); // Es String !!!
		if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { //
			//Puede no venir el param
			page = 1;
		}
		usersRepository.getUsersPg(filter, {}, page).then(result => {
			let lastPage = result.total / 5;
			if (result.total % 5 > 0) { // Sobran decimales
				lastPage = lastPage + 1;
			}
			let pages = []; // paginas mostrar
			for (let i = page - 2; i <= page + 2; i++) {
				if (i > 0 && i <= lastPage) {
					pages.push(i);
				}
			}
			let response = {
				users: result.users,
				pages: pages,
				currentPage: page,
				busquedaStr: busquedaStr
			}
			res.render("users/users-social.twig", response);
		}).catch(error => {
			res.send("Error when searching social users: "+error);
		});
	})

	/**
	 * GET /users/signup
	 * Renders the signup page.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users/signup', function (req, res) {
		res.render("signup.twig");
	})

	/**
	 * POST /users/signup
	 * Handles user signup.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.post('/users/signup', function (req, res) {

		// Validate fields

		validateSignup(req,res).then(errors => {

			if (errors.length > 0) {
				errors.splice(0, 0, "<b>Validation errors:</b>");
				res.redirect("/users/signup" + "?message=" + errors.join("<br>") + "&messageType=alert-danger");
				return;
			}

			let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
				.update(req.body.password).digest('hex');

			let user = {
				email: req.body.email,
				firstName: req.body.firstName,
				lastName: req.body.lastName,
				birthdate: req.body.birthdate,
				role: "user",
				password: securePassword
			}

			// Check if user already exists

			usersRepository.findUser({ email: user.email }, {}).then(existingUser => {

				if (existingUser !== null) {
					res.redirect("/users/signup" + "?message=Email is already registered" + "&messageType=alert-danger");
					return;
				}

				// Register user

				usersRepository.insertUser(user).then(userId => {
					res.redirect("/users/login" + "?message=User registered correctly" + "&messageType=alert-info");

				}).catch(error => {
					console.log(error);
					res.redirect("/users/signup" + "?message=Error registering user" + "&messageType=alert-danger");
				});

			}).catch(error => {
				console.log(error);
				res.redirect("/users/signup" + "?message=Error validating user" + "&messageType=alert-danger");
			});

		}).catch(error => {
			console.log(error);
			res.redirect("/users/signup" + "?message=Error validating fields" + "&messageType=alert-danger");
		});

	})

	/**
	 * GET /users/login
	 * Renders the login page.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users/login', function (req, res) {
		res.render("login.twig");
	})

	/**
	 * POST /users/login
	 * Handles user login.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.post('/users/login', function (req, res) {

		validateLogin(req,res).then(errors => {
			
			/*if (errors.length > 0) {
				errors.splice(0, 0, "<b>Validation errors:</b>");
				res.redirect("/users/login" + "?message=" + errors.join("<br>") + "&messageType=alert-danger");
				return;
			}*/

			let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
			.update(req.body.password).digest('hex');

			let filter = {
				email: req.body.email,
				password: securePassword
			}
			let options = {};

			usersRepository.findUser(filter, options).then(user => {

				if (user == null) {
					req.session.user = null;
					res.redirect("/users/login" + "?message=Email o Contraseña incorrectos" + "&messageType=alert-danger");
				} else {
					req.session.user = user;
					res.redirect("/publications");
				}
				
			}).catch(error => {
				req.session.user = null;
				res.redirect("/users/login" + "?message=Error al buscar el usuario" + "&messageType=alert-danger");
			});

		}).catch(error => {
			console.log(error);
			res.redirect("/users/login" + "?message=Error validating fields" + "&messageType=alert-danger");
		});

	})

	/**
	 * GET /users/logout
	 * Logs out the user.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users/logout', function (req, res) {
		req.session.user = null;
		res.send("El usuario se ha desconectado correctamente");
	})

}

function validate(errors, condition, message) {
	if (!condition) {
		errors.push("- " + message);
	}
}

async function validateSignup(req, res) {

	let errors = [];

	validate(errors, req.body.email !== null && req.body.email !== undefined && req.body.email.trim() !== '', "Email cannot be blank");
	validate(errors, req.body.firstName !== null && req.body.firstName !== undefined && req.body.firstName.trim() !== '', "First name cannot be blank");
	validate(errors, req.body.lastName !== null && req.body.lastName !== undefined && req.body.lastName.trim() !== '', "Last name cannot be blank");
	validate(errors, req.body.birthdate !== null && req.body.birthdate !== undefined && req.body.birthdate.trim() !== '', "Birthdate cannot be blank");
	validate(errors, req.body.password !== null && req.body.password !== undefined && req.body.password.trim() !== '', "Password cannot be blank");
	validate(errors, req.body.passwordConfirmation !== null && req.body.passwordConfirmation !== undefined && req.body.passwordConfirmation.trim() !== '', "Password confirmation cannot be blank");
	validate(errors, req.body.password === req.body.passwordConfirmation, "Passwords do not match");

	return errors;

}

async function validateLogin(req, res) {

	let errors = [];

	validate(errors, req.body.email !== null && req.body.email !== undefined && req.body.email.trim() !== '', "Email cannot be blank");
	validate(errors, req.body.password !== null && req.body.password !== undefined && req.body.password.trim() !== '', "Password cannot be blank");

}

	