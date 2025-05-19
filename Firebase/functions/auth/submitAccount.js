const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitAccount = onRequest(async (req, res) => {
  const uid = await verifyToken(req, res);
  if (!uid) return;

  const { phoneNumber, password } = req.body;
  if (!phoneNumber || !password) {
    return res.status(400).send("Thiếu số điện thoại hoặc mật khẩu");
  }

  await admin.firestore().collection("users").doc(uid).set({
    phoneNumber,
    password,
    role: "member",
    approved: false,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  }, { merge: true });

  res.send("Tài khoản đã được lưu");
});

module.exports = { submitAccount };
