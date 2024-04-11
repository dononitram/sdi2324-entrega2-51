/**
 * Defines the routes and handlers for user-related operations.
 * @param {Object} app - The Express application object.
 * @param {Object} usersRepository - The repository object for user data access.
 */
module.exports = function (app, usersRepository) {

	/**
	 * GET /users
	 * Returns a list of users.
	 * @param {Object} req - The request object.
	 * @param {Object} res - The response object.
	 */
	app.get('/users', function (req, res) {
		res.send('lista de usuarios');
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

			/*
			if (errors.length > 0) {
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

	