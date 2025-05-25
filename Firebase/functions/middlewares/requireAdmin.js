const admin = require("../utils/firebase");

exports.requireAdmin = async (req, res, next) => {
  const uid = req.uid;
  const userDoc = await admin.firestore().collection("users").doc(uid).get();
  if (userDoc.exists && userDoc.get("role") === "admin") {
    return next();
  } else {
    return res.status(403).send("Permission denied");
  }
};
