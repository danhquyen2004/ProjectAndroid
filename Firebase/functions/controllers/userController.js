const admin = require("../utils/firebase");

// Gửi thông tin hồ sơ cá nhân (người dùng tự gọi)
exports.submitProfile = async (req, res) => {
  try {
    const uid = req.uid;
    const { name, dob, gender } = req.body;

    if (!name || !dob || !gender) {
      return res.status(400).send("Missing profile fields");
    }

    const userRecord = await admin.auth().getUser(uid);
    const email = userRecord.email;

    const userRef = admin.firestore().collection("users").doc(uid);
    const doc = await userRef.get();

    if (!doc.exists) {
      await userRef.set({
        name, dob, gender, email,
        role: "member",
        approved: false,
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
    } else {
      await userRef.set({ name, dob, gender }, { merge: true });
    }

    res.send("Profile updated");
  } catch (e) {
    console.error(e);
    res.status(500).send("Error updating profile");
  }
};

// Admin duyệt người dùng
exports.approveUser = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    await admin.firestore().collection("users").doc(targetUid).set({
      approved: true
    }, { merge: true });

    res.send("User approved");
  } catch (e) {
    console.error(e);
    res.status(500).send("Error approving user");
  }
};

// Admin thay đổi vai trò người dùng
exports.setUserRole = async (req, res) => {
  try {
    const targetUid = req.params.uid;
    const { newRole } = req.body;

    if (!newRole) return res.status(400).send("Missing role");

    await admin.firestore().collection("users").doc(targetUid).set({
      role: newRole
    }, { merge: true });

    res.send("User role updated");
  } catch (e) {
    console.error(e);
    res.status(500).send("Error updating role");
  }
};

// Xem thông tin user
exports.getUserById = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    const doc = await admin.firestore().collection("users").doc(targetUid).get();
    if (!doc.exists) return res.status(404).send("User not found");

    res.send(doc.data());
  } catch (e) {
    console.error(e);
    res.status(500).send("Error retrieving user");
  }
};


// [POST] /users – Admin thêm mới user
exports.createUser = async (req, res) => {
  try {
    const { uid, name, dob, gender, email, role, approved } = req.body;

    if (!uid || !name || !dob || !gender || !email) {
      return res.status(400).send("Missing required fields");
    }

    const userRef = admin.firestore().collection("users").doc(uid);
    const doc = await userRef.get();

    if (doc.exists) {
      return res.status(409).send("User already exists");
    }

    await userRef.set({
      name, dob, gender, email,
      role: role || "member",
      approved: approved || false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    res.status(201).send("User created");
  } catch (e) {
    console.error("createUser error:", e);
    res.status(500).send("Internal server error");
  }
};

// [PUT] /users/:uid – Cập nhật toàn bộ hồ sơ
exports.updateUser = async (req, res) => {
  try {
    const { uid } = req.params;
    const { name, dob, gender, role, approved } = req.body;

    if (!name || !dob || !gender) {
      return res.status(400).send("Missing required fields");
    }

    await admin.firestore().collection("users").doc(uid).set({
      name, dob, gender, role, approved
    }, { merge: true });

    res.send("User updated");
  } catch (e) {
    console.error("updateUser error:", e);
    res.status(500).send("Internal server error");
  }
};


// [GET] /users – Lấy danh sách toàn bộ user
exports.getAllUsers = async (req, res) => {
  try {
    const snapshot = await admin.firestore().collection("users").get();
    const users = snapshot.docs.map(doc => ({ uid: doc.id, ...doc.data() }));
    res.send(users);
  } catch (e) {
    console.error("getAllUsers error:", e);
    res.status(500).send("Internal server error");
  }
};

// [DELETE] /users/:uid – Xoá user
exports.deleteUser = async (req, res) => {
  try {
    const { uid } = req.params;
    await admin.firestore().collection("users").doc(uid).delete();
    res.send("User deleted");
  } catch (e) {
    console.error("deleteUser error:", e);
    res.status(500).send("Internal server error");
  }
};