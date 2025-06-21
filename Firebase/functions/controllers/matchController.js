const admin = require("../utils/firebase");


// Hàm chuyển UTC sang giờ Việt Nam (UTC+7)
function toVietnamTime(date) {
  if (!date) return null;
  const d = (date instanceof Date) ? date : new Date(date);
  return new Date(d.getTime() + 7 * 60 * 60 * 1000);
}

// Cập nhật trạng thái các trận đã đến giờ bắt đầu
const updatePendingMatchesStatus = async () => {
  const now = new Date();
  const pendingMatches = await admin.firestore()
    .collection("matches")
    .where("status", "==", "pending")
    .where("startTime", "<=", now)
    .get();
  const updateBatch = admin.firestore().batch();
  pendingMatches.forEach(doc => {
    updateBatch.update(doc.ref, { status: "in_progress" });
  });
  if (!pendingMatches.empty) await updateBatch.commit();
};

// Lấy danh sách các trận đấu trong tháng, phân trang theo ngày
exports.listMonthlyMatches = async (req, res) => {
  try {
    await updatePendingMatchesStatus(); // Cập nhật trạng thái trận đấu

    const { month, year, page = 1, pageSize = 10 } = req.query;
    if (!month || !year) {
      return res.status(400).json({ error: "Missing month or year" });
    }
    const monthInt = parseInt(month);
    const yearInt = parseInt(year);
    const pageInt = parseInt(page);
    const pageSizeInt = parseInt(pageSize);

    // Tính ngày đầu và cuối tháng
    const startDate = new Date(yearInt, monthInt - 1, 1, 0, 0, 0);
    const endDate = new Date(yearInt, monthInt, 0, 23, 59, 59);

    // Lấy tất cả trận đấu trong tháng
    const matchesSnap = await admin.firestore()
      .collection("matches")
      .where("startTime", ">=", startDate)
      .where("startTime", "<=", endDate)
      .orderBy("startTime", "desc")
      .get();

    // Gom nhóm theo ngày
    const matchesByDay = {};
    matchesSnap.docs.forEach(doc => {
      const data = doc.data();
      const matchDate = data.startTime.toDate ? data.startTime.toDate() : data.startTime;
      const dayKey = matchDate.toISOString().slice(0, 10); // yyyy-mm-dd
      if (!matchesByDay[dayKey]) matchesByDay[dayKey] = [];
      matchesByDay[dayKey].push({ id: doc.id, ...data });
    });

    // Phân trang theo ngày
    const allDays = Object.keys(matchesByDay).sort((a, b) => b.localeCompare(a));
    const pagedDays = allDays.slice((pageInt - 1) * pageSizeInt, pageInt * pageSizeInt);

    // Lấy thông tin chi tiết từng trận đấu trong các ngày phân trang
    const result = [];
    for (const day of pagedDays) {
      const matches = await Promise.all(matchesByDay[day].map(async match => {
        // Lấy danh sách người tham gia
        const participantsSnap = await admin.firestore()
          .collection("matches").doc(match.id)
          .collection("participants").get();
        const participants = await Promise.all(participantsSnap.docs.map(async pDoc => {
          const pData = pDoc.data();
          // Lấy tên người chơi
          const profileSnap = await admin.firestore()
            .collection("users").doc(pData.userId)
            .collection("profile").doc("info").get();
          const fullName = profileSnap.exists ? (profileSnap.data().fullName || "Unknown") : "Unknown";
          return {
            userId: pData.userId,
            fullName,
            team: pData.team,
            isConfirmed: pData.isConfirmed
          };
        }));
        // Lấy kết quả set
        const setResultsSnap = await admin.firestore()
          .collection("matches").doc(match.id)
          .collection("setResults").get();
        let team1Wins = 0, team2Wins = 0;
        setResultsSnap.docs.forEach(setDoc => {
          const set = setDoc.data();
          if (set.team1Score > set.team2Score) team1Wins++;
          else if (set.team2Score > set.team1Score) team2Wins++;
        });
        return {
          matchId: match.id,
          status: match.status,
          startTime: toVietnamTime(match.startTime),
          participants,
          team1Wins,
          team2Wins
        };
      }));
      result.push({ day, matches });
    }

    return res.status(200).json({
      days: result,
      totalDays: allDays.length,
      page: pageInt,
      pageSize: pageSizeInt,
      hasNextPage: pageInt * pageSizeInt < allDays.length
    });
  } catch (e) {
    console.error("listMonthlyMatches error:", e);
    return res.status(500).json({ error: "Failed to list matches", message: e.message });
  }
};

// Lấy danh sách các trận đấu theo ngày (YYYY-MM-DD)
exports.listMatchesByDay = async (req, res) => {
  try {
    await updatePendingMatchesStatus(); // Cập nhật trạng thái trận đấu

    const { date, page = 1, pageSize = 10 } = req.query;
    if (!date) {
      return res.status(400).json({ error: "Missing date (YYYY-MM-DD)" });
    }
    const pageInt = parseInt(page);
    const pageSizeInt = parseInt(pageSize);

    // Tính thời gian bắt đầu và kết thúc của ngày
    const [year, month, day] = date.split("-").map(Number);
    const startDate = new Date(year, month - 1, day, 0, 0, 0);
    const endDate = new Date(year, month - 1, day, 23, 59, 59, 999);

    // Lấy các trận đấu trong ngày
    const matchesSnap = await admin.firestore()
      .collection("matches")
      .where("startTime", ">=", startDate)
      .where("startTime", "<=", endDate)
      .orderBy("startTime", "desc")
      .offset((pageInt - 1) * pageSizeInt)
      .limit(pageSizeInt)
      .get();

    const matches = await Promise.all(matchesSnap.docs.map(async doc => {
      const match = doc.data();
      // Lấy danh sách người tham gia
      const participantsSnap = await admin.firestore()
        .collection("matches").doc(doc.id)
        .collection("participants").get();
      const participants = await Promise.all(participantsSnap.docs.map(async pDoc => {
        const pData = pDoc.data();
        // Lấy tên người chơi
        const profileSnap = await admin.firestore()
          .collection("users").doc(pData.userId)
          .collection("profile").doc("info").get();
        const fullName = profileSnap.exists ? (profileSnap.data().fullName || "Unknown") : "Unknown";
        return {
          userId: pData.userId,
          fullName,
          team: pData.team,
          isConfirmed: pData.isConfirmed
        };
      }));
      // Lấy kết quả set
      const setResultsSnap = await admin.firestore()
        .collection("matches").doc(doc.id)
        .collection("setResults").get();
      let team1Wins = 0, team2Wins = 0;
      setResultsSnap.docs.forEach(setDoc => {
        const set = setDoc.data();
        if (set.team1Score > set.team2Score) team1Wins++;
        else if (set.team2Score > set.team1Score) team2Wins++;
      });
      return {
        matchId: doc.id,
        status: match.status,
        startTime: toVietnamTime(match.startTime),
        participants,
        team1Wins,
        team2Wins
      };
    }));

    return res.status(200).json({
      matches,
      page: pageInt,
      pageSize: pageSizeInt,
      hasNextPage: matches.length === pageSizeInt
    });
  } catch (e) {
    console.error("listMatchesByDay error:", e);
    return res.status(500).json({ error: "Failed to list matches by day", message: e.message });
  }
};

// Lấy danh sách các trận đấu theo ngày của một user (YYYY-MM-DD, userId)
exports.listUserMatchesByDay = async (req, res) => {
  try {
    await updatePendingMatchesStatus(); // Cập nhật trạng thái trận đấu

    const { date, userId, page = 1, pageSize = 10 } = req.query;
    if (!date || !userId) {
      return res.status(400).json({ error: "Missing date (YYYY-MM-DD) or userId" });
    }
    const pageInt = parseInt(page);
    const pageSizeInt = parseInt(pageSize);

    // Tính thời gian bắt đầu và kết thúc của ngày
    const [year, month, day] = date.split("-").map(Number);
    const startDate = new Date(year, month - 1, day, 0, 0, 0);
    const endDate = new Date(year, month - 1, day, 23, 59, 59, 999);

    // Lấy các trận đấu trong ngày
    const matchesSnap = await admin.firestore()
      .collection("matches")
      .where("startTime", ">=", startDate)
      .where("startTime", "<=", endDate)
      .orderBy("startTime", "desc")
      .get();

    // Lọc các trận mà user tham gia
    const userMatches = [];
    for (const doc of matchesSnap.docs) {
      const matchId = doc.id;
      const participantsSnap = await admin.firestore()
        .collection("matches").doc(matchId)
        .collection("participants")
        .where("userId", "==", userId)
        .get();
      if (!participantsSnap.empty) {
        const match = doc.data();
        // Lấy danh sách người tham gia
        const allParticipantsSnap = await admin.firestore()
          .collection("matches").doc(matchId)
          .collection("participants").get();
        const participants = await Promise.all(allParticipantsSnap.docs.map(async pDoc => {
          const pData = pDoc.data();
          const profileSnap = await admin.firestore()
            .collection("users").doc(pData.userId)
            .collection("profile").doc("info").get();
          const fullName = profileSnap.exists ? (profileSnap.data().fullName || "Unknown") : "Unknown";
          return {
            userId: pData.userId,
            fullName,
            team: pData.team,
            isConfirmed: pData.isConfirmed
          };
        }));
        // Lấy kết quả set
        const setResultsSnap = await admin.firestore()
          .collection("matches").doc(matchId)
          .collection("setResults").get();
        let team1Wins = 0, team2Wins = 0;
        setResultsSnap.docs.forEach(setDoc => {
          const set = setDoc.data();
          if (set.team1Score > set.team2Score) team1Wins++;
          else if (set.team2Score > set.team1Score) team2Wins++;
        });
        userMatches.push({
          matchId,
          status: match.status,
          startTime: toVietnamTime(match.startTime),
          participants,
          team1Wins,
          team2Wins
        });
      }
    }

    // Phân trang
    const pagedMatches = userMatches.slice((pageInt - 1) * pageSizeInt, pageInt * pageSizeInt);

    return res.status(200).json({
      matches: pagedMatches,
      total: userMatches.length,
      page: pageInt,
      pageSize: pageSizeInt,
      hasNextPage: pageInt * pageSizeInt < userMatches.length
    });
  } catch (e) {
    console.error("listUserMatchesByDay error:", e);
    return res.status(500).json({ error: "Failed to list user matches by day", message: e.message });
  }
};

// [POST] /matches - Tạo trận đấu mới
exports.createMatch = async (req, res) => {
  try {
    const { startDate, startTime, type, setCount, teams, setResults } = req.body;
    if (!startDate || !startTime || !type || !setCount || !teams || !setResults) {
      return res.status(400).json({ error: "Missing required fields" });
    }
    if (!Array.isArray(teams.team1) || !Array.isArray(teams.team2)) {
      return res.status(400).json({ error: "Teams must be arrays" });
    }
    if (type === "single" && (teams.team1.length !== 1 || teams.team2.length !== 1)) {
      return res.status(400).json({ error: "Single match must have 1 member per team" });
    }
    if (type === "double" && (teams.team1.length !== 2 || teams.team2.length !== 2)) {
      return res.status(400).json({ error: "Double match must have 2 members per team" });
    }

    // Tạo timestamp bắt đầu trận đấu (theo giờ Việt Nam, chuyển sang UTC)
    const [year, month, day] = startDate.split("-").map(Number);
    const [hour, minute] = startTime.split(":").map(Number);
    // Giả định client nhập giờ Việt Nam (UTC+7)
    const startDateTimeVN = new Date(year, month - 1, day, hour, minute, 0);
    const startDateTimeUTC = new Date(startDateTimeVN.getTime() - 7 * 60 * 60 * 1000);

    // Xác định trạng thái trận đấu
    let status = "pending";
    const now = new Date(); // UTC
    if (startDateTimeUTC > now) {
      status = "pending"; // Chưa bắt đầu
    } else {
      // Kiểm tra tỉ số các set
      const enoughScores = setResults.length === setCount && setResults.every(set => (set.team1Score !== 0 || set.team2Score !== 0));
      if (enoughScores) {
        status = "finished";
      } else {
        status = "in_progress";
      }
    }

    // Tạo document trận đấu
    const matchRef = await admin.firestore().collection("matches").add({
      type,
      setCount,
      startTime: startDateTimeUTC,
      status,
      createdBy: req.uid || null,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
    const matchId = matchRef.id;

    // Thêm participants
    const batch = admin.firestore().batch();
    teams.team1.forEach(userId => {
      const partRef = matchRef.collection("participants").doc(userId);
      batch.set(partRef, { matchId, userId, team: 1, isConfirmed: false });
    });
    teams.team2.forEach(userId => {
      const partRef = matchRef.collection("participants").doc(userId);
      batch.set(partRef, { matchId, userId, team: 2, isConfirmed: false });
    });

    // Thêm setResults
    setResults.forEach(set => {
      const setRef = matchRef.collection("setResults").doc(set.setNumber.toString());
      batch.set(setRef, {
        matchId,
        setNumber: set.setNumber,
        team1Score: set.team1Score,
        team2Score: set.team2Score
      });
    });

    await batch.commit();

    // Nếu trận đấu đã hoàn thành ngay khi tạo, thực hiện cập nhật điểm và phạt
    if (status === "finished") {
      // Lấy lại participants và setResults mới nhất
      const [participantsSnap, setResultsSnap] = await Promise.all([
        matchRef.collection("participants").get(),
        matchRef.collection("setResults").get()
      ]);
      const participants = participantsSnap.docs.map(doc => doc.data());
      const setResultsArr = setResultsSnap.docs.map(doc => doc.data());
      // Tính số set thắng của mỗi đội
      let team1Wins = 0, team2Wins = 0;
      setResultsArr.forEach(set => {
        if (set.team1Score > set.team2Score) team1Wins++;
        else if (set.team2Score > set.team1Score) team2Wins++;
      });
      // Xác định đội thắng/thua
      let winnerTeam = null, loserTeam = null;
      if (team1Wins > team2Wins) {
        winnerTeam = 1; loserTeam = 2;
      } else if (team2Wins > team1Wins) {
        winnerTeam = 2; loserTeam = 1;
      }
      // Tính điểm từng set cho từng user
      const userScoreChange = {};
      setResultsArr.forEach(set => {
        let setWinner = null, setLoser = null;
        if (set.team1Score > set.team2Score) { setWinner = 1; setLoser = 2; }
        else if (set.team2Score > set.team1Score) { setWinner = 2; setLoser = 1; }
        participants.forEach(p => {
          if (!userScoreChange[p.userId]) userScoreChange[p.userId] = 0;
          if (setWinner && p.team === setWinner) userScoreChange[p.userId] += 0.1;
          if (setLoser && p.team === setLoser) userScoreChange[p.userId] -= 0.1;
        });
      });
      // Ghi scoreHistories cho từng user
      const scoreBatch = admin.firestore().batch();
      for (const p of participants) {
        const userId = p.userId;
        const scoreChange = +(userScoreChange[userId] || 0).toFixed(2);
        if (scoreChange !== 0) {
          // Lấy tổng điểm hiện tại từ scoreHistories (không lấy từ profile)
          const scoreType = type === "single" ? "single" : "double";
          const scoreHistoriesRef = admin.firestore().collection("users").doc(userId).collection("scoreHistories");
          const latestScoreSnap = await scoreHistoriesRef
            .where("scoreType", "==", scoreType)
            .orderBy("createdAt", "desc")
            .limit(1)
            .get();
          let currentScore = 0;
          if (!latestScoreSnap.empty && latestScoreSnap.docs[0].data().newTotalScore !== undefined) {
            currentScore = latestScoreSnap.docs[0].data().newTotalScore;
          }
          const newTotalScore = +(currentScore + scoreChange).toFixed(2);
          // Ghi lịch sử điểm
          const scoreHistoryRef = scoreHistoriesRef.doc();
          scoreBatch.set(scoreHistoryRef, {
            scoreType, // Đúng chuẩn schema
            matchId,
            scoreChange,
            newTotalScore,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
          });
          // Không cập nhật tổng điểm vào profile nữa
        }
      }
      await scoreBatch.commit();
      // Ghi nhận khoản phạt cho đội thua (nếu có)
      if (loserTeam) {
        const penaltyAmount = 10000;
        const penaltyBatch = admin.firestore().batch();
        const nowTs = admin.firestore.FieldValue.serverTimestamp();
        participants.filter(p => p.team === loserTeam).forEach(p => {
          const userId = p.userId;
          // Ghi paymentRequest
          const paymentRef = admin.firestore().collection("users").doc(userId).collection("paymentRequests").doc();
          penaltyBatch.set(paymentRef, {
            type: "penalty",
            amount: penaltyAmount,
            matchId,
            status: "unpaid",
            createdAt: nowTs
          });
          // Ghi financeLog
          const financeRef = admin.firestore().collection("users").doc(userId).collection("financeLogs").doc();
          penaltyBatch.set(financeRef, {
            type: "penalty_incurred",
            amount: penaltyAmount,
            matchId,
            description: `Phạt thua trận ${matchId}`,
            createdAt: nowTs
          });
        });
        await penaltyBatch.commit();
      }
    }

    return res.status(201).json({ message: "Match created successfully", matchId });
  } catch (e) {
    console.error("createMatch error:", e);
    return res.status(500).json({ error: "Failed to create match", message: e.message });
  }
};

// [PUT] /matches/:matchId - Cập nhật tỉ số các set của trận đấu đã tạo
exports.updateMatchScores = async (req, res) => {
  try {
    const { matchId } = req.params;
    const { setResults } = req.body; // [{ setNumber, team1Score, team2Score }]
    if (!setResults || !Array.isArray(setResults) || setResults.length === 0) {
      return res.status(400).json({ error: "Missing or invalid setResults" });
    }

    const matchRef = admin.firestore().collection("matches").doc(matchId);
    const matchDoc = await matchRef.get();
    if (!matchDoc.exists) {
      return res.status(404).json({ error: "Match not found" });
    }
    const matchData = matchDoc.data();
    const setCount = matchData.setCount;
    // startTime lưu trong DB là UTC
    const startTime = matchData.startTime.toDate ? matchData.startTime.toDate() : matchData.startTime;
    const now = new Date(); // UTC

    // Cập nhật từng setResult (ghi đè hoặc thêm mới)
    const batch = admin.firestore().batch();
    setResults.forEach(set => {
      const setRef = matchRef.collection("setResults").doc(set.setNumber.toString());
      batch.set(setRef, {
        matchId,
        setNumber: set.setNumber,
        team1Score: set.team1Score,
        team2Score: set.team2Score
      }, { merge: true });
    });
    await batch.commit();

    // Xác định trạng thái mới
    let status = matchData.status;
    if (startTime > now) {
      status = "pending";
    } else {
      const enoughScores = setResults.length === setCount && setResults.every(set => (set.team1Score !== 0 || set.team2Score !== 0));
      if (enoughScores) {
        status = "finished";
      } else {
        status = "in_progress";
      }
    }
    await matchRef.update({ status });

    // === TỰ ĐỘNG CẬP NHẬT ĐIỂM VÀ GHI NHẬN PHẠT KHI TRẬN ĐẤU KẾT THÚC ===
    if (status === "finished") {
      // Lấy lại participants và setResults mới nhất
      const [participantsSnap, setResultsSnap] = await Promise.all([
        matchRef.collection("participants").get(),
        matchRef.collection("setResults").get()
      ]);
      const participants = participantsSnap.docs.map(doc => doc.data());
      const setResultsArr = setResultsSnap.docs.map(doc => doc.data());
      // Tính số set thắng của mỗi đội
      let team1Wins = 0, team2Wins = 0;
      setResultsArr.forEach(set => {
        if (set.team1Score > set.team2Score) team1Wins++;
        else if (set.team2Score > set.team1Score) team2Wins++;
      });
      // Xác định đội thắng/thua
      let winnerTeam = null, loserTeam = null;
      if (team1Wins > team2Wins) {
        winnerTeam = 1; loserTeam = 2;
      } else if (team2Wins > team1Wins) {
        winnerTeam = 2; loserTeam = 1;
      }
      // Tính điểm từng set cho từng user
      const userScoreChange = {};
      setResultsArr.forEach(set => {
        let setWinner = null, setLoser = null;
        if (set.team1Score > set.team2Score) { setWinner = 1; setLoser = 2; }
        else if (set.team2Score > set.team1Score) { setWinner = 2; setLoser = 1; }
        participants.forEach(p => {
          if (!userScoreChange[p.userId]) userScoreChange[p.userId] = 0;
          if (setWinner && p.team === setWinner) userScoreChange[p.userId] += 0.1;
          if (setLoser && p.team === setLoser) userScoreChange[p.userId] -= 0.1;
        });
      });
      // Ghi scoreHistories cho từng user
      const scoreBatch = admin.firestore().batch();
      for (const p of participants) {
        const userId = p.userId;
        const scoreChange = +(userScoreChange[userId] || 0).toFixed(2);
        if (scoreChange !== 0) {
          // Lấy tổng điểm hiện tại từ scoreHistories (không lấy từ profile)
          const scoreType = matchData.type === "single" ? "single" : "double";
          const scoreHistoriesRef = admin.firestore().collection("users").doc(userId).collection("scoreHistories");
          const latestScoreSnap = await scoreHistoriesRef
            .where("scoreType", "==", scoreType)
            .orderBy("createdAt", "desc")
            .limit(1)
            .get();
          let currentScore = 0;
          if (!latestScoreSnap.empty && latestScoreSnap.docs[0].data().newTotalScore !== undefined) {
            currentScore = latestScoreSnap.docs[0].data().newTotalScore;
          }
          const newTotalScore = +(currentScore + scoreChange).toFixed(2);
          // Ghi lịch sử điểm
          const scoreHistoryRef = scoreHistoriesRef.doc();
          scoreBatch.set(scoreHistoryRef, {
            scoreType, // Đúng chuẩn schema
            matchId,
            scoreChange,
            newTotalScore,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
          });
          // Không cập nhật tổng điểm vào profile nữa
        }
      }
      await scoreBatch.commit();
      // Ghi nhận khoản phạt cho đội thua (nếu có)
      if (loserTeam) {
        const penaltyAmount = 10000;
        const penaltyBatch = admin.firestore().batch();
        const nowTs = admin.firestore.FieldValue.serverTimestamp();
        participants.filter(p => p.team === loserTeam).forEach(p => {
          const userId = p.userId;
          // Ghi paymentRequest
          const paymentRef = admin.firestore().collection("users").doc(userId).collection("paymentRequests").doc();
          penaltyBatch.set(paymentRef, {
            type: "penalty",
            amount: penaltyAmount,
            matchId,
            status: "unpaid",
            createdAt: nowTs
          });
          // Ghi financeLog
          const financeRef = admin.firestore().collection("users").doc(userId).collection("financeLogs").doc();
          penaltyBatch.set(financeRef, {
            type: "penalty_incurred",
            amount: penaltyAmount,
            matchId,
            description: `Phạt thua trận ${matchId}`,
            createdAt: nowTs
          });
        });
        await penaltyBatch.commit();
      }
    }
    // === END ===

    return res.status(200).json({ message: "Match scores updated successfully", status });
  } catch (e) {
    console.error("updateMatchScores error:", e);
    return res.status(500).json({ error: "Failed to update match scores", message: e.message });
  }
};

// Xóa 1 trận đấu và toàn bộ dữ liệu liên quan (participants, setResults, scoreHistories, paymentRequests, financeLogs)
exports.deleteMatch = async (req, res) => {
  try {
    const { matchId } = req.params;
    if (!matchId) {
      return res.status(400).json({ error: "Missing matchId" });
    }
    const matchRef = admin.firestore().collection("matches").doc(matchId);
    const matchDoc = await matchRef.get();
    if (!matchDoc.exists) {
      return res.status(404).json({ error: "Match not found" });
    }
    // Xóa các subcollection: participants, setResults
    const participantsSnap = await matchRef.collection("participants").get();
    const setResultsSnap = await matchRef.collection("setResults").get();
    const batch = admin.firestore().batch();
    participantsSnap.docs.forEach(doc => batch.delete(doc.ref));
    setResultsSnap.docs.forEach(doc => batch.delete(doc.ref));
    // Xóa document trận đấu
    batch.delete(matchRef);
    await batch.commit();

    // Xóa các bản ghi scoreHistories, paymentRequests, financeLogs liên quan đến matchId ở tất cả user
    const usersSnap = await admin.firestore().collection("users").get();
    for (const userDoc of usersSnap.docs) {
      const userId = userDoc.id;
      // Xóa scoreHistories
      const scoreHistoriesSnap = await admin.firestore().collection("users").doc(userId).collection("scoreHistories").where("matchId", "==", matchId).get();
      const batch1 = admin.firestore().batch();
      scoreHistoriesSnap.docs.forEach(doc => batch1.delete(doc.ref));
      await batch1.commit();
      // Xóa paymentRequests
      const paymentRequestsSnap = await admin.firestore().collection("users").doc(userId).collection("paymentRequests").where("matchId", "==", matchId).get();
      const batch2 = admin.firestore().batch();
      paymentRequestsSnap.docs.forEach(doc => batch2.delete(doc.ref));
      await batch2.commit();
      // Xóa financeLogs
      const financeLogsSnap = await admin.firestore().collection("users").doc(userId).collection("financeLogs").where("matchId", "==", matchId).get();
      const batch3 = admin.firestore().batch();
      financeLogsSnap.docs.forEach(doc => batch3.delete(doc.ref));
      await batch3.commit();
    }
    return res.status(200).json({ message: "Match and all related records deleted successfully" });
  } catch (e) {
    console.error("deleteMatch error:", e);
    return res.status(500).json({ error: "Failed to delete match", message: e.message });
  }
};

// ...các hàm khác nếu cần
