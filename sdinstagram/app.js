let createError = require('http-errors');
let express = require('express');
let path = require('path');
let cookieParser = require('cookie-parser');
let logger = require('morgan');

let indexRouter = require('./routes/index');

let app = express();

let rest = require('request');
app.set('rest', rest);

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Credentials", "true");
  res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
// Debemos especificar todas las headers que se aceptan. Content-Type , token
  next();
});

let jwt = require('jsonwebtoken');
app.set('jwt', jwt);

let crypto = require('crypto');
app.set('clave', 'abcdefg');
app.set('crypto', crypto);

const expressSession = require('express-session');
app.use(expressSession({
  secret: 'abcdefg',
  resave: true,
  saveUninitialized: true
}));

let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// BBDD MongoDB Cloud
const { MongoClient } = require("mongodb");
const connectionStrings = "mongodb://localhost:27017/"
const dbClient = new MongoClient(connectionStrings);

// Repositories
let publicationsRepository = require("./repositories/publicationsRepository");
publicationsRepository.init(app, dbClient);

let friendshipRepository = require("./repositories/friendshipsRepository");
friendshipRepository.init(app, dbClient);

let friendshipRequestRepository = require("./repositories/friendshipRequestsRepository");
friendshipRequestRepository.init(app, dbClient);

let logsRepository = require("./repositories/logsRepository");
logsRepository.init(app, dbClient);

let conversationsRepository = require("./repositories/conversationsRepository");
conversationsRepository.init(app, dbClient);

let usersRepository = require("./repositories/usersRepository");
usersRepository.init(app, dbClient, publicationsRepository, friendshipRepository, friendshipRequestRepository, conversationsRepository);

// MiddleWares
const petLogger = require('./middlewares/petLogger');
app.use("*", petLogger(logsRepository));

const userSessionRouter = require('./middlewares/userSessionRouter');
app.use("/friendships", userSessionRouter);
app.use("/publications", userSessionRouter);
app.use("/users/logout", userSessionRouter)
app.use("/logs", userSessionRouter);

const adminSessionRouter = require('./middlewares/adminSessionRouter');
app.use("/logs", adminSessionRouter);
app.use("/users/system", adminSessionRouter);
app.use("/users/delete", adminSessionRouter);
app.use("/users/edit", adminSessionRouter)

const userTokenRouter = require('./middlewares/userTokenRouter');
app.use("/api/v1.0/users/friendships", userTokenRouter);
app.use("/api/v1.0/users/conversations", userTokenRouter);
app.use("/api/v1.0/conversation", userTokenRouter);
app.use("/api/v1.0/conversations", userTokenRouter);
app.use("/api/v1.0/messages", userTokenRouter);

const notAdminSessionRouter = require('./middlewares/notAdminSessionRouter');
app.use("/friendships", notAdminSessionRouter);
app.use("/publications", notAdminSessionRouter);
app.use("/users/social", notAdminSessionRouter);

const friendshipRequestsRouter = require("./middlewares/friendshipRequestsRouter");
app.use("/friendships/request/send", friendshipRequestsRouter);

//Routes
require("./routes/logs")(app, logsRepository);
require("./routes/friendships")(app, friendshipRepository, friendshipRequestRepository, usersRepository, publicationsRepository);
require("./routes/users")(app, usersRepository, logsRepository);
require("./routes/publications")(app, publicationsRepository);

require("./routes/api/APIv1.0")(app, usersRepository, friendshipRepository, friendshipRequestRepository, publicationsRepository, conversationsRepository);

// View engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);

// Catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// Error Handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error', { user: req.session.user });
});

module.exports = app;
