const admin = require("../utils/firebase");
const axios = require("axios");
const FIREBASE_API_KEY = require("../apiKey").FIREBASE_API_KEY;

// [POST] /auth/register
exports.register = async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) return res.status(400).send("Missing email or password");

  try {
    // Đăng ký tài khoản qua Firebase REST API
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${FIREBASE_API_KEY}`,
      {
        email,
        password,
        returnSecureToken: true
      }
    );

    const { idToken, localId } = response.data;

    // Tạo user profile trong Firestore
    await admin.firestore().collection("users").doc(localId).set({
      email,
      role: "member",
      approved: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    // Gửi email xác minh
    await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=${FIREBASE_API_KEY}`,
      {
        requestType: "VERIFY_EMAIL",
        idToken
      }
    );

    return res.status(201).send({
      message: "User registered. Please verify your email.",
      uid: localId
    }
    );
  } catch (err) {
    console.error("Register error:", err.response?.data || err.message);
    return res.status(500).send("Registration failed.");
  }
};

// [POST] /auth/login
exports.login = async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) return res.status(400).send("Missing credentials");

  try {
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${FIREBASE_API_KEY}`,
      {
        email,
        password,
        returnSecureToken: true
      }
    );

    const { idToken, refreshToken, localId } = response.data;

    // ✅ Kiểm tra lại bằng Admin SDK
    const userRecord = await admin.auth().getUser(localId);
    if (!userRecord.emailVerified) {
      return res.status(403).send("Email is not verified (checked by Admin SDK).");
    }

    return res.send({ idToken, refreshToken, uid: localId });
  } catch (err) {
    console.error("Login error:", err.response?.data || err.message);
    return res.status(401).send("Login failed");
  }
};

// [POST] /auth/send-verification
exports.sendVerificationEmail = async (req, res) => {
  const { idToken } = req.body;
  if (!idToken) return res.status(400).send("Missing idToken");

  try {
    await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=${FIREBASE_API_KEY}`,
      {
        requestType: "VERIFY_EMAIL",
        idToken
      }
    );
    return res.send("Verification email sent");
  } catch (err) {
    console.error("Send verification error:", err.response?.data || err.message);
    return res.status(500).send("Failed to send verification email");
  }
};

// [GET] /auth/verify-status/:uid
exports.checkEmailVerified = async (req, res) => {
  const { uid } = req.params;

  try {
    const user = await admin.auth().getUser(uid);
    res.send({ emailVerified: user.emailVerified });
  } catch (err) {
    console.error("Check verify error:", err.message);
    res.status(404).send("User not found");
  }
};
