const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitAccount = async (req, res) => {
  try {
    const uid = await verifyToken(req, res);
    if (!uid) return;

    const { phoneNumber, password } = req.body;

    console.log("✅ Body nhận được:", req.body);
    console.log("👤 UID:", uid);

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
  } catch (e) {
    console.error("❌ Lỗi khi xử lý submitAccount:", e);
    res.status(500).send("Lỗi hệ thống");
  }
};

module.exports = { submitAccount };
