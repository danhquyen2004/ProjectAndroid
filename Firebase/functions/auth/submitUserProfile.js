const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitUserProfile = onRequest(async (req, res) => {
  const uid = await verifyToken(req, res);
  if (!uid) return;

  const { name, dob, gender } = req.body;
  if (!name || !dob || !gender) {
    return res.status(400).send("Thiếu thông tin");
  }

  await admin.firestore().collection("users").doc(uid).set({
    name,
    dob,
    gender,
    phoneNumber: req.body.phoneNumber || "",
    role: "member",
    approved: false,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  });

  res.send("Đã ghi thông tin người dùng");
});

module.exports = { submitUserProfile };
