var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');

var app = express();

// BBDD MongoDB Cloud
const { MongoClient } = require("mongodb");
const connectionStrings = 'mongodb://sdi51:g1PqYBrJug94nHRNBV9k@158.179.219.219:27017/sdinstagram'; // Connection to cloud virtual machine
const dbClient = new MongoClient(connectionStrings);

// Repositories
let usersRepository = require("./repositories/usersRepository");
usersRepository.init(app, dbClient);

let publicationsRepository = require("./repositories/publicationsRepository");
publicationsRepository.init(app, dbClient);

let friendshipRepository = require("./repositories/friendshipsRepository");
friendshipRepository.init(app, dbClient);

let friendshipRequestRepository = require("./repositories/friendshipRequestsRepository");
friendshipRequestRepository.init(app, dbClient);


//Routes
require("./routes/friendships")(app, friendshipRepository, friendshipRequestRepository, usersRepository);
require("./routes/users")(app, usersRepository);
require("./routes/publications")(app, publicationsRepository);

// MiddleWares
const userSessionRouter = require('./routes/userSessionRouter');
const usersRouter = require('./routes/users');
const friendshipRequestsRouter = require("./routes/friendshipRequestsRouter")
const publicationsRouter = require("./routes/publications");
app.use("/publications/add", userSessionRouter);
app.use("/request/send", friendshipRequestsRouter);

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
  res.render('error');
});

module.exports = app;
