const admin = require("firebase-admin");

async function verifyToken(req, res) {
  const header = req.headers.authorization;
  if (!header || !header.startsWith("Bearer ")) {
    res.status(401).send("Thiếu token xác thực");
    return null;
  }

  const token = header.split(" ")[1];
  try {
    const decoded = await admin.auth().verifyIdToken(token);
    return decoded.uid;
  } catch (e) {
    res.status(401).send("Token không hợp lệ");
    return null;
  }
}

module.exports = { verifyToken };
