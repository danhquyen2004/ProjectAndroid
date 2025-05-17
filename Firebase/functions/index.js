/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */


const functions = require("firebase-functions");
const {onRequest} = require("firebase-functions/v2/https");

exports.helloWorld = functions.https.onRequest((request, response) => {
    response.send("Xin chào từ Firebase!");
});
exports.apiTest = onRequest((req, res) => {
    switch (req.method) {
        case "GET":
            return res.send("📥 Đây là GET request");

        case "POST":
            return res.send(`📦 POST nhận được: ${JSON.stringify(req.body)}`);

        case "PUT":
            return res.send(`✏️ PUT cập nhật với: ${JSON.stringify(req.body)}`);

        case "DELETE":
            return res.send("🗑️ Đã xóa thành công");

        default:
            return res.status(405).send("🚫 Method Not Allowed");
    }
});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
