const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitAccount = async (req, res) => {
  try {
    const uid = await verifyToken(req, res);
    if (!uid) return;

    const { email, password } = req.body;

    console.log("✅ Received body:", req.body);
    console.log("👤 UID:", uid);

    if (!email || !password) {
      return res.status(400).send("Missing email or password");
    }

    await admin.firestore().collection("users").doc(uid).set({
      email,
      password,
      role: "member",
      approved: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    res.send("Account has been saved");
  } catch (e) {
    console.error("❌ Error while processing submitAccount:", e);
    res.status(500).send("Internal server error");
  }
};

module.exports = { submitAccount };
