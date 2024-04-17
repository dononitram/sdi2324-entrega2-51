let createError = require('http-errors');
let express = require('express');
let path = require('path');
let cookieParser = require('cookie-parser');
let logger = require('morgan');

let indexRouter = require('./routes/index');

let app = express();

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

// Repositories. They need to be placed here.
let publicationsRepository = require("./repositories/publicationsRepository");
publicationsRepository.init(app, dbClient);

let friendshipRepository = require("./repositories/friendshipsRepository");
friendshipRepository.init(app, dbClient);

let friendshipRequestRepository = require("./repositories/friendshipRequestsRepository");
friendshipRequestRepository.init(app, dbClient);

let usersRepository = require("./repositories/usersRepository");
usersRepository.init(app, dbClient, publicationsRepository, friendshipRepository, friendshipRequestRepository);

let loggerRepository = require("./repositories/logsRepository");
loggerRepository.init(app, dbClient);

// MiddleWares. They need to be placed here.
const petLogger = require('./middlewares/petLogger');
app.use(petLogger(loggerRepository));

const userSessionRouter = require('./middlewares/userSessionRouter');
app.use("/friendships", userSessionRouter);
app.use("/publications/add", userSessionRouter);
app.use("/publications", userSessionRouter);

const adminSessionRouter = require('./middlewares/adminSessionRouter');
app.use("/log/list", adminSessionRouter);
app.use("/users/system", adminSessionRouter);
app.use("/users/delete", adminSessionRouter);
app.use("/users/edit", adminSessionRouter)

const notAdminSessionRouter = require('./middlewares/notAdminSessionRouter');
// TODO: fill with the routes an administrator can't access

const userFriendRouter = require('./middlewares/userFriendRouter');
//app.use("/friendships", userFriendRouter);

const friendshipRequestsRouter = require("./middlewares/friendshipRequestsRouter");
app.use("/friendships/request/send", friendshipRequestsRouter);

// BBDD MongoDB Cloud
const { MongoClient } = require("mongodb");
const connectionStrings = 'mongodb://sdi51:g1PqYBrJug94nHRNBV9k@158.179.219.219:27017/'; // Connection to cloud virtual machine
const dbClient = new MongoClient(connectionStrings);

//Routes
require("./routes/friendships")(app, friendshipRepository, friendshipRequestRepository, usersRepository, publicationsRepository);
require("./routes/users")(app, usersRepository);
require("./routes/publications")(app, publicationsRepository);

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
