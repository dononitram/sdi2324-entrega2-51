const express = require('express');
const path = require("path");
const {ObjectId} = require("mongodb");
const friendshipRequestRepository = require("../repositories/friendshipRequestRepository");
const friendshipRequestsRouter = express.Router();
friendshipRequestsRouter.use(function (req,
                                       res, next) {
    receiver = new ObjectId(path.basename(req.originalUrl));
    requester = req.session.user;
    // Si se envía una invitación de amistad a un usuario amigo o hay una solicitud de amistad en curso
    // entre ambos usuarios, el sistema validará del lado del servidor y mostrará un mensaje de error
    // indicando que no se puede realizar dicha solicitud, indicando cuál es la causa
});
module.exports = friendshipRequestsRouter;
