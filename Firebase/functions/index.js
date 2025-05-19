/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */


const { submitAccount } = require("./auth/submitAccount");
const { submitProfile } = require("./auth/submitProfile");

exports.submitAccount = submitAccount;
exports.submitProfile = submitProfile;


