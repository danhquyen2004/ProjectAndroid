const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitProfile = async (req, res) => {
  const uid = await verifyToken(req, res);
  if (!uid) return;

  const { name, dob, gender } = req.body;
  if (!name || !dob || !gender) {
    return res.status(400).send("Thiếu thông tin hồ sơ");
  }

  await admin.firestore().collection("users").doc(uid).set({
    name,
    dob,
    gender
  }, { merge: true });

  res.send("Hồ sơ cá nhân đã được cập nhật");
};

module.exports = { submitProfile };
