const { ObjectId } = require("mongodb");
const friendshipRepository = require("../repositories/friendshipsRepository");

module.exports = function (app, usersRepository, logsRepository) {

  // Route to retrieve system users
  app.get('/users/system', async function (req, res) {
    let page = parseInt(req.query.page);
    if (!page || page < 1)
      page = 1;
    const usersResponse = await usersRepository.getUsersPg({}, {}, page);
    //Calculate pages to show
    let lastPage = usersResponse.total / 5;
    if (usersResponse.total % 5 > 0)
      lastPage = lastPage + 1;
    let pages = [];
    for (let i = page - 2; i <= page + 2; i++)
      if (i > 0 && i <= lastPage)
        pages.push(i);

    res.render('users/users-system.twig', { users: usersResponse.users, pages: pages, currentPage: page, user: req.session.user });
  })

  // Route to retrieve social users
  app.get('/users/social', async function (req, res) {
    try {
      let connectedUser = req.session.user;
      if (!connectedUser || !connectedUser._id) {
        res.send("Error you have to be logged to see users");
        return;
      }
      let userId = new ObjectId(connectedUser._id);
      let filter =
      {
        role: { $ne: "admin" },
        _id: { $ne: userId },
      };
      //Búsqueda
      let busquedaStr = "";//Vacía por defecto
      if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
        busquedaStr = req.query.search;
        filter.$or = [
          { "email": { $regex: ".*" + req.query.search + ".*" } },
          { "firstName": { $regex: ".*" + req.query.search + ".*" } },
          { "lastName": { $regex: ".*" + req.query.search + ".*" } }
        ];
      }

      let page = parseInt(req.query.page); // Es String !!!
      if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { //
        //Puede no venir el param
        page = 1;
      }
      const result = await usersRepository.getUsersPg(filter, {}, page);

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
        busquedaStr: busquedaStr,
        user: req.session.user
      };

      for (const user of result.users) {
        user2 = req.session.user;
        user2._id = new ObjectId(user2._id);
        const filter = { user1: user, user2: user2 };
        const options = {};
        const result = await friendshipRepository.findFriend(filter, options);
        console.log(result)
        if (result === null || typeof result === "undefined") {
          user1 = req.session.user;
          user1._id = new ObjectId(user1._id);
          const filter = { user1: user1, user2: user };
          const options = {};
          const result = await friendshipRepository.findFriend(filter, options);
          console.log(result)
          if (result === null || typeof result === "undefined") {
            user.areFriends = false;
          }
          else {
            user.areFriends = true;
          }
        } else {
          user.areFriends = true;
        }
      }

      res.render("users/users-social.twig", response);
    } catch (error) {
      res.send("Error when searching social users: " + error);
    }
  });

  // Route to render user edit form
  app.get('/users/edit/:id', async function (req, res) {
    const user = await usersRepository.findUser({ _id: new ObjectId(req.params.id) }, {});
    res.render('users/users-edit.twig', { user: user, admin: admin(req) }); // TODO: change user to userEdit
  });


  // Route to handle user edit form submission
  app.post('/users/edit/:id', async function (req, res) {

    // Validate fields
    const errors = validateUserEdit(req, res)

    if (errors && errors.length > 0) {
      errors.splice(0, 0, "<b>Validation errors:</b>");
      res.redirect("/users/edit/" + req.params.id + "?message=" + errors.join("<br>") + "&messageType=alert-danger");
      return;
    }

    // Check if email is already in use
    const emailUser = await usersRepository.findUser({ email: req.body.email }, {});

    if (emailUser) {
      res.redirect('/users/edit/' + req.params.id + `?message=Email already in use&messageType=alert-danger`);
      return;
    }

    // Check role
    if (admin(req)) {
      await usersRepository.updateUser({ _id: new ObjectId(req.params.id) }, req.body);
      res.redirect('/users/edit/' + req.params.id + '?message=User updated successfully&messageType=alert-info');
    } else
      res.redirect('/users/edit/' + req.params.id + `?message=You don't have permission to edit users&messageType=alert-danger`);
  });


  // Route to retrieve available user roles
  app.get('/users/roles', async function (req, res) {
    res.json({ roles: ["admin", "user"] });
  });

  // Route to delete user
  app.post('/users/delete', async function (req, res) {

    if (!admin(req)) {
      res.json({ message: 'You don\'t have permission to delete users', messageType: 'alert-danger' });
      return;
    }

    // Users to delete
    const ids = req.body.ids;
    //Check current user is not included
    const currentId = req.session.user._id;
    if (currentId && ids && ids.includes(currentId)) {
      res.json({ message: 'You cannot delete yourself', messageType: 'alert-danger' });
      return;
    }

    //Convert to ObjectIds
    const objectIds = ids.map(id => new ObjectId(id));

    //Perform delete
    try {
      const result = await usersRepository.deleteUsersData(objectIds);
      res.json({ message: `${result.deletedCount} users deleted successfully`, messageType: 'alert-info' });
      return;
    } catch (error) {
      res.json({ message: 'Error deleting users', messageType: 'alert-danger' });
    }

  });

  // Route to render signup form
  app.get('/users/signup', function (req, res) {
    res.render("signup.twig", { user: req.session.user });
  });

  // Route to handle user signup form submission
  app.post('/users/signup', function (req, res) {

    // Validate fields
    validateSignup(req, res).then(errors => {

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
          logsRepository.insertLog({ date: Date.now(), type: "ALTA", description: "User " + user.email + " registered" }).catch(error => {
            console.log(error);
          });

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

  });

  // Route to render login form
  app.get('/users/login', function (req, res) {
    res.render("login.twig", { user: req.session.user });
  })

  // Route to handle user login form submission
  app.post('/users/login', function (req, res) {

    validateLogin(req, res).then(errors => {

      if (errors.length > 0) {
        errors.splice(0, 0, "<b>Validation errors:</b>");
        res.redirect("/users/login" + "?message=" + errors.join("<br>") + "&messageType=alert-danger");
        return;
      }

      let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
        .update(req.body.password).digest('hex');

      let filter = {
        email: req.body.email,
        password: securePassword
      }
      let options = {};

      usersRepository.findUser(filter, options).then(user => {

        req.session.user = user;

        logsRepository.insertLog({ date: Date.now(), type: "LOGIN-EX", description: "User " + user.email + " logged in" }).catch(error => {
          console.log(error);
        });

        (user.role === "admin") ? res.redirect("/users/system") : res.redirect("/users/social");

      }).catch(error => {
        req.session.user = null;

        logsRepository.insertLog({ date: Date.now(), type: "LOGIN-ERR", description: "User " + req.body.email + " tried to log in" }).catch(error => {
          console.log(error);
        });

        res.redirect("/users/login" + "?message=Incorrect user or password" + "&messageType=alert-danger");
      });

    }).catch(error => {
      res.redirect("/users/login" + "?message=Error validating fields" + "&messageType=alert-danger");
    });

  })

  // Route to handle user logout
  app.get('/users/logout', function (req, res) {
    logsRepository.insertLog({ date: Date.now(), type: "LOGOUT", description: "User " + req.session.user.email + " logged out" }).catch(error => {
      console.log(error);
    });
    req.session.user = null;
    res.redirect("/users/login" + "?message=Logged out successfully" + "&messageType=alert-info");
  })

}

// validate
function validate(errors, condition, message) {
  if (!condition) {
    errors.push("- " + message);
  }
}


// Function to validate form fields for signup
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

// Function to validate form fields for login
async function validateLogin(req, res) {

  let errors = [];

  validate(errors, req.body.email !== null && req.body.email !== undefined && req.body.email.trim() !== '', "Email cannot be blank");
  validate(errors, req.body.password !== null && req.body.password !== undefined && req.body.password.trim() !== '', "Password cannot be blank");

  return errors;
}

// Function to validate form fields for user edit
function validateUserEdit(req, res) {
  let errors = [];

  validate(errors, req.body.email !== null && req.body.email !== undefined && req.body.email.trim() !== '', "Email cannot be blank");
  validate(errors, req.body.firstName !== null && req.body.firstName !== undefined && req.body.firstName.trim() !== '', "First name cannot be blank");
  validate(errors, req.body.lastName !== null && req.body.lastName !== undefined && req.body.lastName.trim() !== '', "Last name cannot be blank");
  validate(errors, req.body.birthdate !== null && req.body.birthdate !== undefined && req.body.birthdate.trim() !== '', "Birthdate cannot be blank");

  return errors;
}

// Function to check if user is admin
function admin(req) {
  try {
    const role = req.session.user.role;
    return role === 'admin' ? true : false;
  } catch (e) {
    return false;
  }
}
