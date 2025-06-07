const admin = require("../utils/firebase");

// [POST] /users/profile/submit
exports.submitProfile = async (req, res) => {
  try {
    const uid = req.uid;
    const { fullName, birthDate, gender, level, memberCode, avatarUrl } = req.body;

    if (!fullName || !birthDate || !gender) {
      return res.status(400).send("Missing required profile fields.");
    }

    const profileData = {
      fullName,
      gender,
      birthDate: new Date(birthDate),
      level: level || "new",
      memberCode: memberCode || "",
      avatarUrl: avatarUrl || ""
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


exports.submitApprovalRequest = async (req, res) => {
  try {
    const uid = req.uid; // middleware đã decode từ token
    const userRef = admin.firestore().collection("users").doc(uid);

    await userRef.set({ approvalStatus: "pending" }, { merge: true });

    return res.status(200).send("Approval request submitted.");
  } catch (e) {
    console.error("submitApprovalRequest error:", e);
    return res.status(500).send("Failed to submit approval request.");
  }
};

exports.rejectUser = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    await admin.firestore().collection("users").doc(targetUid).set({
      approvalStatus: "rejected"
    }, { merge: true });

    return res.status(200).send("User has been rejected.");
  } catch (e) {
    console.error("rejectUser error:", e);
    return res.status(500).send("Failed to reject user.");
  }
};

exports.approveUser = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    await admin.firestore().collection("users").doc(targetUid).set({
      approvalStatus: "approved"
    }, { merge: true });

    return res.status(200).send("User has been approved.");
  } catch (e) {
    console.error("approveUser error:", e);
    return res.status(500).send("Failed to approve user.");
  }
};

exports.getPendingUsers = async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 10;
    const startAfterUid = req.query.startAfter || null;

    let query = admin.firestore()
      .collection("users")
      .where("approvalStatus", "==", "pending")
      .orderBy("createdAt", "desc")
      .limit(limit);

    if (startAfterUid) {
      const startDoc = await admin.firestore().collection("users").doc(startAfterUid).get();
      if (startDoc.exists) {
        query = query.startAfter(startDoc);
      } else {
        return res.status(400).json({ error: "Invalid startAfter UID" });
      }
    }

    const snapshot = await query.get();

    // Lấy dữ liệu profile tương ứng
    const users = await Promise.all(snapshot.docs.map(async doc => {
      const uid = doc.id;
      const data = doc.data();

      let fullName = null;
      try {
        const profileSnap = await admin.firestore()
          .collection("users")
          .doc(uid)
          .collection("profile")
          .doc("info")
          .get();

        if (profileSnap.exists) {
          fullName = profileSnap.data().fullName || null;
        }
      } catch (_) {}

      return {
        uid,
        email: data.email || null,
        createdAt: data.createdAt?.toDate().toISOString() || null,
        fullName,
        approvalStatus: data.approvalStatus || null
      };
    }));

    const lastDoc = snapshot.docs[snapshot.docs.length - 1];
    const nextPageToken = lastDoc ? lastDoc.id : null;

    return res.status(200).json({ users, nextPageToken });

  } catch (e) {
    console.error("getPendingUsers error:", e);
    return res.status(500).json({ error: "Failed to fetch pending users", message: e.message, stack: e.stack });
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