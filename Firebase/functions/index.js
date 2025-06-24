const functions = require("firebase-functions");
const app = require("./api");

exports.api = functions.https.onRequest(app);
