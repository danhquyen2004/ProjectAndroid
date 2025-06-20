const admin = require("../utils/firebase");


exports.uploadAvatar = async (req, res) => {
  const uid = req.params.uid;
  if (!uid) {
    return res.status(400).json({ error: 'Missing UID' });
  }

  if (!req.files || req.files.length === 0) {
    return res.status(400).json({ error: 'No file uploaded' });
  }

  const fileData = req.files[0];
  console.log(`Received file: ${fileData.originalname}, mimetype: ${fileData.mimetype}`);

  const bucket = admin.storage().bucket();
  const filePath = `avatars/${uid}/avatar.png`;
  const fileUpload = bucket.file(filePath);

  try {
    await fileUpload.save(fileData.buffer, {
      metadata: {
        contentType: fileData.mimetype
      }
    });

    const [url] = await fileUpload.getSignedUrl({
      action: 'read',
      expires: '03-01-2500'
    });

    await admin.firestore()
      .collection('users')
      .doc(uid)
      .collection('profile')
      .doc('info')
      .set({ avatarUrl: url }, { merge: true });

    return res.status(200).json({
      message: 'Avatar uploaded successfully',
      avatarUrl: url
    });
  } catch (err) {
    console.error('Upload error:', err);
    return res.status(500).json({ error: 'Upload failed', message: err.message });
  }
};

// [POST] /users/profile/submit
exports.submitProfile = async (req, res) => {
  try {
    const uid = req.uid;
    const { fullName, birthDate, gender, avatarUrl } = req.body;

    if (!fullName || !birthDate || !gender) {
      return res.status(400).send("Missing required profile fields.");
    }

    const profileData = {
      fullName,
      gender,
      birthDate: new Date(birthDate),
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

exports.getUserProfile = async (req, res) => {
  try {
    const uid = req.params.uid;

    // Lấy user document
    const userDoc = await admin.firestore().collection("users").doc(uid).get();
    if (!userDoc.exists) {
      return res.status(404).json({ error: "User not found" });
    }
    const userData = userDoc.data();
    const email = userData.email || null;

    // Lấy profile document
    const profileDoc = await admin.firestore()
      .collection("users")
      .doc(uid)
      .collection("profile")
      .doc("info")
      .get();

    let profileData = {
      fullName: null,
      gender: null,
      birthDate: null,
      avatarUrl: null
    };

    if (profileDoc.exists) {
      const data = profileDoc.data();
      profileData = {
        fullName: data.fullName || null,
        gender: data.gender || null,
        birthDate: data.birthDate ? data.birthDate.toDate().toISOString() : null,
        avatarUrl: data.avatarUrl || ""
      };
    }

    // Lấy điểm đơn mới nhất
    const singleScoreSnap = await admin.firestore()
      .collection("users")
      .doc(uid)
      .collection("scoreHistories")
      .where("scoreType", "==", "single")
      .orderBy("createdAt", "desc")
      .limit(1)
      .get();

    let currentSingleScore = singleScoreSnap.empty
      ? 0
      : singleScoreSnap.docs[0].data().newTotalScore || 0;
    currentSingleScore = Math.round(currentSingleScore * 10) / 10;

    // Lấy điểm đôi mới nhất
    const doubleScoreSnap = await admin.firestore()
      .collection("users")
      .doc(uid)
      .collection("scoreHistories")
      .where("scoreType", "==", "double")
      .orderBy("createdAt", "desc")
      .limit(1)
      .get();

    let currentDoubleScore = doubleScoreSnap.empty
      ? 0
      : doubleScoreSnap.docs[0].data().newTotalScore || 0;
    currentDoubleScore = Math.round(currentDoubleScore * 10) / 10;

    return res.status(200).json({
      uid,
      email,
      ...profileData,
      currentSingleScore,
      currentDoubleScore
    });

  } catch (e) {
    console.error("getUserProfile error:", e);
    return res.status(500).json({ error: "Error fetching user profile", message: e.message, stack: e.stack });
  }
};

exports.updateUserProfile = async (req, res) => {
  try {
    const uid = req.params.uid;
    const { fullName, gender, birthDate, avatarUrl } = req.body;

    const updateData = {};

    if (fullName !== undefined) updateData.fullName = fullName;
    if (gender !== undefined) updateData.gender = gender;
    if (birthDate !== undefined) {
      updateData.birthDate = admin.firestore.Timestamp.fromDate(new Date(birthDate));
    }
    if (avatarUrl !== undefined) updateData.avatarUrl = avatarUrl;

    if (Object.keys(updateData).length === 0) {
      return res.status(400).json({ error: "No fields provided to update." });
    }

    const profileRef = admin.firestore()
      .collection("users")
      .doc(uid)
      .collection("profile")
      .doc("info");

    await profileRef.set(updateData, { merge: true });

    return res.status(200).json({ message: "Profile updated successfully." });

  } catch (e) {
    console.error("updateUserProfile error:", e);
    return res.status(500).json({ error: "Error updating profile.", message: e.message, stack: e.stack });
  }
};

// 
exports.submitApprovalRequest = async (req, res) => {
  try {
    const uid = req.uid; // middleware đã decode từ token
    const userRef = admin.firestore().collection("users").doc(uid);

    await userRef.set({
      approvalStatus: "pending",
      createdAt: admin.firestore.Timestamp.now()
    }, { merge: true });

    return res.status(200).send("Approval request submitted.");
  } catch (e) {
    console.error("submitApprovalRequest error:", e);
    return res.status(500).send("Failed to submit approval request.");
  }
};
// Reject user
exports.rejectUser = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    await admin.firestore().collection("users").doc(targetUid).set({
      approvalStatus: "rejected",
      createdAt: admin.firestore.Timestamp.now()
    }, { merge: true });

    return res.status(200).send("User has been rejected.");
  } catch (e) {
    console.error("rejectUser error:", e);
    return res.status(500).send("Failed to reject user.");
  }
};
// Approve user
exports.approveUser = async (req, res) => {
  try {
    const targetUid = req.params.uid;

    await admin.firestore().collection("users").doc(targetUid).set({
      approvalStatus: "approved",
      createdAt: admin.firestore.Timestamp.now()
    }, { merge: true });

    return res.status(200).send("User has been approved.");
  } catch (e) {
    console.error("approveUser error:", e);
    return res.status(500).send("Failed to approve user.");
  }
};
// Disable user
exports.disableUser = async (req, res) => {
  const uid = req.params.uid;

  try {
    // Firestore: cập nhật trạng thái
    await admin.firestore().collection("users").doc(uid).set({
      isDisabled: true,
      createdAt: admin.firestore.Timestamp.now()
    }, { merge: true });

    // Firebase Auth: disable tài khoản
    await admin.auth().updateUser(uid, {
      disabled: true
    });

    return res.status(200).json({ message: `User ${uid} disabled successfully.` });
  } catch (e) {
    console.error("disableUser error:", e);
    return res.status(500).json({ error: "Failed to disable user", message: e.message });
  }
};
// Enable user
exports.enableUser = async (req, res) => {
  const uid = req.params.uid;

  try {
    // Firestore: cập nhật trạng thái
    await admin.firestore().collection("users").doc(uid).set({
      isDisabled: false,
      createdAt: admin.firestore.Timestamp.now()
    }, { merge: true });

    // Firebase Auth: enable tài khoản
    await admin.auth().updateUser(uid, {
      disabled: false
    });

    return res.status(200).json({ message: `User ${uid} enabled successfully.` });
  } catch (e) {
    console.error("enableUser error:", e);
    return res.status(500).json({ error: "Failed to enable user", message: e.message });
  }
};

// Danh sach hoi vien dang cho phe duyet - Done
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
      let avatarUrl = null;
      try {
        const profileSnap = await admin.firestore()
          .collection("users")
          .doc(uid)
          .collection("profile")
          .doc("info")
          .get();

        if (profileSnap.exists) {
          fullName = profileSnap.data().fullName || null;
          avatarUrl = profileSnap.data().avatarUrl || null;
        }
      } catch (_) { }

      return {
        uid,
        email: data.email || null,
        createdAt: data.createdAt?.toDate().toISOString() || null,
        fullName,
        avatarUrl,
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

// Danh sách hội viên đã được phê duyệt, hoạt động bình thường
exports.getApprovedUsers = async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 10;
    const startAfterUid = req.query.startAfter || null;

    let query = admin.firestore()
      .collection("users")
      .where("approvalStatus", "==", "approved")
      .where("isDisabled", "==", false)
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

    const users = await Promise.all(snapshot.docs.map(async doc => {
      const uid = doc.id;
      const data = doc.data();

      let fullName = null;
      let avatarUrl = null;
      try {
        const profileSnap = await admin.firestore()
          .collection("users")
          .doc(uid)
          .collection("profile")
          .doc("info")
          .get();

        if (profileSnap.exists) {
          fullName = profileSnap.data().fullName || null;
          avatarUrl = profileSnap.data().avatarUrl || null;
        }
      } catch (_) { }

      return {
        uid,
        email: data.email || null,
        createdAt: data.createdAt?.toDate().toISOString() || null,
        fullName,
        avatarUrl,
        approvalStatus: data.approvalStatus || null
      };
    }));

    const lastDoc = snapshot.docs[snapshot.docs.length - 1];
    const nextPageToken = lastDoc ? lastDoc.id : null;

    return res.status(200).json({ users, nextPageToken });

  } catch (e) {
    console.error("getApprovedUsers error:", e);
    return res.status(500).json({ error: "Failed to fetch approved users", message: e.message, stack: e.stack });
  }
};

// Danh sách hội viên bị vô hiệu hóa
exports.getDisabledUsers = async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 10;
    const startAfterUid = req.query.startAfter || null;

    let query = admin.firestore()
      .collection("users")
      .where("approvalStatus", "==", "approved")
      .where("isDisabled", "==", true)
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

    const users = await Promise.all(snapshot.docs.map(async doc => {
      const uid = doc.id;
      const data = doc.data();

      let fullName = null;
      let avatarUrl = null;
      try {
        const profileSnap = await admin.firestore()
          .collection("users")
          .doc(uid)
          .collection("profile")
          .doc("info")
          .get();

        if (profileSnap.exists) {
          fullName = profileSnap.data().fullName || null;
          avatarUrl = profileSnap.data().avatarUrl || null;
        }
      } catch (_) { }

      return {
        uid,
        email: data.email || null,
        createdAt: data.createdAt?.toDate().toISOString() || null,
        fullName,
        avatarUrl,
        approvalStatus: data.approvalStatus || null
      };
    }));

    const lastDoc = snapshot.docs[snapshot.docs.length - 1];
    const nextPageToken = lastDoc ? lastDoc.id : null;

    return res.status(200).json({ users, nextPageToken });

  } catch (e) {
    console.error("getDisabledUsers error:", e);
    return res.status(500).json({ error: "Failed to fetch disabled users", message: e.message, stack: e.stack });
  }
};


exports.getSingleRanking = async (req, res) => {
  try {
    const usersSnapshot = await admin.firestore().collection("users").get();

    const rankingData = [];

    // Loop through users to get their latest single score
    for (const userDoc of usersSnapshot.docs) {
      const uid = userDoc.id;
      const userData = userDoc.data();

      // Lấy profile để lấy fullName
      const profileDoc = await admin.firestore()
        .collection("users").doc(uid)
        .collection("profile").doc("info")
        .get();

      const fullName = profileDoc.exists ? (profileDoc.data().fullName || "Unknown") : "Unknown";

      // Lấy điểm đơn mới nhất
      const scoreSnapshot = await admin.firestore()
        .collection("users").doc(uid)
        .collection("scoreHistories")
        .where("scoreType", "==", "single")
        .orderBy("createdAt", "desc")
        .limit(1)
        .get();

      if (!scoreSnapshot.empty) {
        const scoreData = scoreSnapshot.docs[0].data();
        let point = scoreData.newTotalScore ;
        point = Math.round(point * 10) / 10; // Làm tròn đến 1 chữ số thập phân

        rankingData.push({
          fullName,
          point
        });
      }
    }

    // Sort toàn bộ theo point giảm dần
    rankingData.sort((a, b) => b.point - a.point);

    // Thêm rank
    const result = rankingData.map((item, index) => ({
      rank: index + 1,
      fullName: item.fullName,
      point: item.point
    }));

    return res.status(200).json(result);

  } catch (e) {
    console.error("getSingleRanking error:", e);
    return res.status(500).json({ error: "Failed to get single ranking", message: e.message });
  }
};


exports.getDoubleRanking = async (req, res) => {
  try {
    const usersSnapshot = await admin.firestore().collection("users").get();

    const rankingData = [];

    // Loop through users to get their latest double score
    for (const userDoc of usersSnapshot.docs) {
      const uid = userDoc.id;

      // Lấy profile để lấy fullName
      const profileDoc = await admin.firestore()
        .collection("users").doc(uid)
        .collection("profile").doc("info")
        .get();

      const fullName = profileDoc.exists ? (profileDoc.data().fullName || "Unknown") : "Unknown";

      // Lấy điểm đôi mới nhất
      const scoreSnapshot = await admin.firestore()
        .collection("users").doc(uid)
        .collection("scoreHistories")
        .where("scoreType", "==", "double")
        .orderBy("createdAt", "desc")
        .limit(1)
        .get();

      if (!scoreSnapshot.empty) {
        const scoreData = scoreSnapshot.docs[0].data();
        let point = scoreData.newTotalScore;
        point = Math.round(point * 10) / 10; // Làm tròn đến 1 chữ số thập phân

        rankingData.push({
          fullName,
          point
        });
      }
    }

    // Sort toàn bộ theo point giảm dần
    rankingData.sort((a, b) => b.point - a.point);

    // Thêm rank
    const result = rankingData.map((item, index) => ({
      rank: index + 1,
      fullName: item.fullName,
      point: item.point
    }));

    return res.status(200).json(result);

  } catch (e) {
    console.error("getDoubleRanking error:", e);
    return res.status(500).json({ error: "Failed to get double ranking", message: e.message });
  }
};


//----------------------------------------------------------------------------------------------------------------------------------------------------------------------
