const functions = require("firebase-functions");
const admin = require("firebase-admin");

console.log("🔥 Firebase Admin SDK initialized");
admin.initializeApp();

const { submitAccount } = require("./auth/submitAccount");
const { submitProfile } = require("./auth/submitProfile");

exports.submitAccount = functions.https.onRequest(submitAccount);
exports.submitProfile = functions.https.onRequest(submitProfile);
