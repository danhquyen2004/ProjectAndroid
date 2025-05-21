const admin = require("firebase-admin");
const { verifyToken } = require("../utils/verifyToken");

const submitProfile = async (req, res) => {
  try {
    const uid = await verifyToken(req, res);
    if (!uid) return;

    const { name, dob, gender } = req.body;

    if (!name || !dob || !gender) {
      return res.status(400).send("Missing profile information (name, date of birth, or gender)");
    }

    const userRecord = await admin.auth().getUser(uid);
    const email = userRecord.email || null;

    const userRef = admin.firestore().collection("users").doc(uid);
    const doc = await userRef.get();

    if (!doc.exists) {
      await userRef.set({
        name,
        dob,
        gender,
        email,
        role: "member",
        approved: false,
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
    }
    res.send("Profile has been updated successfully");
  } catch (e) {
    console.error("❌ Error in submitProfile:", e);
    res.status(500).send("Internal server error");
  }
};

module.exports = { submitProfile };
