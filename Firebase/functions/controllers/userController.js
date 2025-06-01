const admin = require("../utils/firebase");

// [POST] /users/profile/submit
exports.submitProfile = async (req, res) => {
  try {
    const uid = req.uid; // Middleware đã decode token và gán req.uid
    const { fullName, birthDate, gender, level, memberCode } = req.body;

    // Validate các trường bắt buộc
    if (!fullName || !birthDate || !gender) {
      return res.status(400).send("Missing required profile fields.");
    }

    // Dữ liệu chuẩn hóa
    const profileData = {
      fullName,
      gender,                        // "male" | "female"
      birthDate: new Date(birthDate), // ISO string hoặc yyyy-mm-dd
      level: level || "new",          // default nếu chưa nhập
      memberCode: memberCode || ""
    };

    const profileRef = admin.firestore()
      .collection("users")
      .doc(uid)
      .collection("profile")
      .doc("info");

    await profileRef.set(profileData, { merge: true });

    return res.status(200).send("Profile submitted successfully.");

  } catch (e) {
    console.error("submitProfile error:", e);
    return res.status(500).send("Error submitting profile.");
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