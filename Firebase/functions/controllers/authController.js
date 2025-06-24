const admin = require("../utils/firebase");
const axios = require("axios");
const FIREBASE_API_KEY = require("../apiKey").FIREBASE_API_KEY;

// [POST] /auth/register
exports.register = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).send("Missing email or password.");
  }

  try {
    // 1. Gọi Firebase REST API để tạo user (xác thực)
    const response = await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${FIREBASE_API_KEY}`,
      {
        email,
        password,
        returnSecureToken: true
      }
    );

    const { idToken, refreshToken, localId: uid } = response.data;

    // 2. Tạo document trong Firestore: users/{uid}
    await admin.firestore().collection("users").doc(uid).set({
      email,
      role: "member",
      approvalStatus: "pending",
      isDisabled: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    // Lấy role từ Firestore (phòng trường hợp sau này có thể thay đổi role)
    const userDoc = await admin.firestore().collection("users").doc(uid).get();
    const role = userDoc.exists && userDoc.data().role ? userDoc.data().role : "member";

    // 3. Gửi email xác minh
    await axios.post(
      `https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=${FIREBASE_API_KEY}`,
      {
        requestType: "VERIFY_EMAIL",
        idToken
      }
    );

    return res.status(201).send({
      message: "User registered successfully. Please verify your email.",
      uid,
      idToken,
      refreshToken,
      role
    });

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

    const userRecord = await admin.auth().getUser(localId);
    // Lấy role từ Firestore
    let role = "member";
    try {
      const userDoc = await admin.firestore().collection("users").doc(localId).get();
      if (userDoc.exists && userDoc.data().role) role = userDoc.data().role;
    } catch (e) {
      // Nếu lỗi vẫn trả về member
    }

    // ✅ Gửi email xác minh nếu chưa xác minh
    if (!userRecord.emailVerified) {
      try {
        await axios.post(
          `https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=${FIREBASE_API_KEY}`,
          {
            requestType: "VERIFY_EMAIL",
            idToken
          }
        );
        console.log(`Verification email re-sent to ${email}`);
      } catch (verifyErr) {
        console.warn("Failed to send verification email:", verifyErr.response?.data || verifyErr.message);
        // Không chặn flow login, chỉ log
      }
    }

    return res.send({
      idToken,
      refreshToken,
      uid: localId,
      emailVerified: userRecord.emailVerified,
      disabled: userRecord.disabled,
      role
    });

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
    return res.json({ message: "Verification email sent" });
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
