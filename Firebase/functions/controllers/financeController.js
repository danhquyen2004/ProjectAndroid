const admin = require("../utils/firebase");

// Controller tài chính (financeController)
// Viết các hàm xử lý tài chính tại đây

module.exports = {
  /**
   * Lấy trạng thái đóng quỹ/thanh toán của 1 user theo tháng
   * Trả về:
   * - Số tiền quỹ cố định tháng đó, đã đóng hay chưa
   * - Số tiền ủng hộ (donation) tháng đó
   * - Tổng số tiền phạt (penalty) tháng đó: đã đóng + chưa đóng
   * - Tổng số tiền phạt đã đóng
   * - Tổng số tiền phạt chưa đóng
   */
  getUserFundStatusByMonth: async (req, res) => {
    try {
      const { userId } = req.params;
      const { month, year } = req.query;
      if (!userId || !month || !year) {
        return res.status(400).json({ error: "Missing userId, month, or year" });
      }
      const monthInt = parseInt(month);
      const yearInt = parseInt(year);
      // Tính khoảng thời gian đầu-cuối tháng
      const startDate = new Date(yearInt, monthInt - 1, 1, 0, 0, 0);
      const endDate = new Date(yearInt, monthInt, 0, 23, 59, 59, 999);

      // 1. Quỹ cố định tháng đó (paymentRequest type=fixed, forMonth)
      const paymentRequestsSnap = await admin.firestore()
        .collection("users").doc(userId)
        .collection("paymentRequests")
        .where("type", "==", "fixed")
        .where("forMonth", ">=", new Date(yearInt, monthInt - 1, 1))
        .where("forMonth", "<=", new Date(yearInt, monthInt - 1, 31))
        .limit(1)
        .get();
      let fixedFund = null;
      if (!paymentRequestsSnap.empty) {
        const doc = paymentRequestsSnap.docs[0].data();
        fixedFund = {
          amount: doc.amount,
          status: doc.status,
        };
      }

      // 2. Số tiền ủng hộ (transaction type=donation, trong tháng)
      const donationSnap = await admin.firestore()
        .collection("users").doc(userId)
        .collection("transactions")
        .where("type", "==", "donation")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .get();
      let totalDonation = 0;
      donationSnap.docs.forEach(doc => {
        totalDonation += doc.data().amount || 0;
      });

      // 3. Tổng số tiền phạt (paymentRequest type=penalty, trong tháng)
      const penaltySnap = await admin.firestore()
        .collection("users").doc(userId)
        .collection("paymentRequests")
        .where("type", "==", "penalty")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .get();
      let totalPenalty = 0, totalPenaltyPaid = 0, totalPenaltyUnpaid = 0;
      penaltySnap.docs.forEach(doc => {
        const d = doc.data();
        totalPenalty += d.amount || 0;
        if (d.status === "paid") totalPenaltyPaid += d.amount || 0;
        else totalPenaltyUnpaid += d.amount || 0;
      });

      return res.status(200).json({
        fixedFund, // null nếu chưa có yêu cầu đóng quỹ tháng đó
        totalDonation,
        totalPenalty,
        totalPenaltyPaid,
        totalPenaltyUnpaid
      });
    } catch (e) {
      console.error("getUserFundStatusByMonth error:", e);
      return res.status(500).json({ error: "Failed to get fund status", message: e.message });
    }
  },

  /**
   * Lấy danh sách hoạt động tài chính (finance log) của user theo tháng
   * Trả về: nội dung (description), thời gian (createdAt), số tiền (amount)
   */
  getUserFinanceLogsByMonth: async (req, res) => {
    try {
      const { userId } = req.params;
      const { month, year } = req.query;
      if (!userId || !month || !year) {
        return res.status(400).json({ error: "Missing userId, month, or year" });
      }
      const monthInt = parseInt(month);
      const yearInt = parseInt(year);
      const startDate = new Date(yearInt, monthInt - 1, 1, 0, 0, 0);
      const endDate = new Date(yearInt, monthInt, 0, 23, 59, 59, 999);

      const logsSnap = await admin.firestore()
        .collection("users").doc(userId)
        .collection("financeLogs")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .orderBy("createdAt", "desc")
        .get();

      const logs = logsSnap.docs.map(doc => {
        const d = doc.data();
        let createdAt = d.createdAt ? (d.createdAt.toDate ? d.createdAt.toDate() : d.createdAt) : null;
        if (createdAt) {
          // Chuyển sang giờ Việt Nam (UTC+7)
          createdAt = new Date(createdAt.getTime() + 7 * 60 * 60 * 1000);
          createdAt = createdAt.toISOString();
        }
        return {
          description: d.description || d.type || "",
          createdAt,
          amount: d.amount || 0
        };
      });

      return res.status(200).json({ logs });
    } catch (e) {
      console.error("getUserFinanceLogsByMonth error:", e);
      return res.status(500).json({ error: "Failed to get finance logs", message: e.message });
    }
  },

  /**
   * Lấy tổng số quỹ còn lại của CLB
   * Tổng = (tất cả paymentRequests đã đóng (fixed + penalty) + donation) - tổng expenses
   */
  getClubFundBalance: async (req, res) => {
    try {
      const doc = await admin.firestore().collection("clubStats").doc("fundBalance").get();
      if (!doc.exists) {
        return res.status(200).json({ clubFundBalance: 0, totalIncome: 0, totalExpense: 0 });
      }
      return res.status(200).json(doc.data());
    } catch (e) {
      console.error("getClubFundBalance error:", e);
      return res.status(500).json({ error: "Failed to get club fund balance", message: e.message });
    }
  },

  /**
   * Lấy tổng thu, tổng chi của CLB theo tháng
   * Query: month, year
   * Tổng thu = paymentRequests (fixed+penalty, status=paid, trong tháng) + donation (trong tháng)
   * Tổng chi = expenses (trong tháng)
   */
  getClubFinanceSummaryByMonth: async (req, res) => {
    try {
      const { month, year } = req.query;
      if (!month || !year) {
        return res.status(400).json({ error: "Missing month or year" });
      }
      const monthInt = parseInt(month);
      const yearInt = parseInt(year);
      // Đọc dữ liệu tổng hợp từ Firestore, không tính toán lại
      const docId = `${yearInt}-${String(monthInt).padStart(2, "0")}`;
      const doc = await admin.firestore()
        .collection("clubStats").doc("monthlySummary")
        .collection("financeSummaryByMonth").doc(docId).get();
      if (!doc.exists) {
        return res.status(200).json({ month: monthInt, year: yearInt, totalIncome: 0, totalExpense: 0 });
      }
      const data = doc.data();
      return res.status(200).json({
        month: data.month,
        year: data.year,
        totalIncome: data.totalIncome,
        totalExpense: data.totalExpense
      });
    } catch (e) {
      console.error("getClubFinanceSummaryByMonth error:", e);
      return res.status(500).json({ error: "Failed to get club finance summary", message: e.message });
    }
  },

  /**
   * Lấy lịch sử chi tiêu của CLB theo tháng
   * Query: month, year
   * Trả về: [{ description, createdAt (giờ VN), amount }]
   */
  getClubExpenseLogsByMonth: async (req, res) => {
    try {
      const { month, year } = req.query;
      if (!month || !year) {
        return res.status(400).json({ error: "Missing month or year" });
      }
      const monthInt = parseInt(month);
      const yearInt = parseInt(year);
      const startDate = new Date(yearInt, monthInt - 1, 1, 0, 0, 0);
      const endDate = new Date(yearInt, monthInt, 0, 23, 59, 59, 999);
      const expensesSnap = await admin.firestore().collection("expenses")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .orderBy("createdAt", "desc")
        .get();
      const logs = expensesSnap.docs.map(doc => {
        const d = doc.data();
        let createdAt = d.createdAt ? (d.createdAt.toDate ? d.createdAt.toDate() : d.createdAt) : null;
        if (createdAt) {
          // Chuyển sang giờ Việt Nam (UTC+7)
          createdAt = new Date(createdAt.getTime() + 7 * 60 * 60 * 1000);
          createdAt = createdAt.toISOString();
        }
        return {
          expenseId: d.expenseId || doc.id,
          reason: d.reason || "",
          amount: d.amount || 0,
          createdBy: d.createdBy || "",
          createdAt
        };
      });
      return res.status(200).json({ logs });
    } catch (e) {
      console.error("getClubExpenseLogsByMonth error:", e);
      return res.status(500).json({ error: "Failed to get club expense logs", message: e.message });
    }
  },

  /**
   * Lấy danh sách trạng thái quỹ của các thành viên theo tháng
   * Query: month, year
   * Trả về: [{ userId, fixedFund: {amount, status}, penalty: {total, paid, unpaid, status}, totalDonation }]
   */
  getAllUserFundStatusByMonth: async (req, res) => {
    try {
      const { month, year } = req.query;
      if (!month || !year) {
        return res.status(400).json({ error: "Missing month or year" });
      }
      const monthInt = parseInt(month);
      const yearInt = parseInt(year);
      const startDate = new Date(yearInt, monthInt - 1, 1, 0, 0, 0);
      const endDate = new Date(yearInt, monthInt, 0, 23, 59, 59, 999);
      const usersSnap = await admin.firestore().collection("users").get();
      // Xử lý song song cho từng user
      const results = await Promise.all(usersSnap.docs.map(async userDoc => {
        const userId = userDoc.id;
        // 1. Quỹ cố định tháng đó
        let fixedFund = { amount: 0, status: "unpaid" };
        const fixedSnap = await admin.firestore()
          .collection("users").doc(userId)
          .collection("paymentRequests")
          .where("type", "==", "fixed")
          .where("forMonth", ">=", new Date(yearInt, monthInt - 1, 1))
          .where("forMonth", "<=", new Date(yearInt, monthInt - 1, 31))
          .limit(1)
          .get();
        if (!fixedSnap.empty) {
          const doc = fixedSnap.docs[0].data();
          fixedFund = { amount: doc.amount, status: doc.status };
        }
        // 2. Phạt tháng đó
        let totalPenalty = 0, totalPenaltyPaid = 0, totalPenaltyUnpaid = 0;
        const penaltySnap = await admin.firestore()
          .collection("users").doc(userId)
          .collection("paymentRequests")
          .where("type", "==", "penalty")
          .where("createdAt", ">=", startDate)
          .where("createdAt", "<=", endDate)
          .get();
        penaltySnap.docs.forEach(doc => {
          const d = doc.data();
          totalPenalty += d.amount || 0;
          if (d.status === "paid") totalPenaltyPaid += d.amount || 0;
          else totalPenaltyUnpaid += d.amount || 0;
        });
        let penaltyStatus = "unpaid";
        if (totalPenalty > 0) {
          if (totalPenaltyPaid === totalPenalty) penaltyStatus = "paid";
          else if (totalPenaltyPaid > 0) penaltyStatus = "partial";
        }
        // 3. Tổng donation tháng đó
        let totalDonation = 0;
        const donationSnap = await admin.firestore()
          .collection("users").doc(userId)
          .collection("transactions")
          .where("type", "==", "donation")
          .where("createdAt", ">=", startDate)
          .where("createdAt", "<=", endDate)
          .get();
        donationSnap.docs.forEach(doc => {
          totalDonation += doc.data().amount || 0;
        });
        return {
          userId,
          fixedFund,
          penalty: {
            total: totalPenalty,
            paid: totalPenaltyPaid,
            unpaid: totalPenaltyUnpaid,
            status: penaltyStatus
          },
          totalDonation
        };
      }));
      return res.status(200).json({ results });
    } catch (e) {
      console.error("getAllUserFundStatusByMonth error:", e);
      return res.status(500).json({ error: "Failed to get all user fund status", message: e.message });
    }
  },

  /**
   * Thêm một khoản chi tiêu mới cho CLB
   * Body: { reason, amount, createdAt }
   * createdBy: lấy từ req.userId nếu có, hoặc để trống
   */
  createExpense: async (req, res) => {
    try {
      const { reason, amount, createdAt } = req.body;
      if (!reason || !amount || !createdAt) {
        return res.status(400).json({ error: "Missing reason, amount, or createdAt" });
      }
      // createdBy: nếu có xác thực, lấy từ req.userId, nếu không thì để rỗng
      const createdBy = req.userId || "";
      const expenseData = {
        reason,
        amount: Number(amount),
        createdAt: new Date(createdAt),
        createdBy
      };
      const docRef = await admin.firestore().collection("expenses").add(expenseData);
      // === Cập nhật clubStats/fundBalance ===
      const fundRef = admin.firestore().collection("clubStats").doc("fundBalance");
      await admin.firestore().runTransaction(async (t) => {
        const doc = await t.get(fundRef);
        let { totalIncome = 0, totalExpense = 0 } = doc.exists ? doc.data() : {};
        totalExpense += Number(amount);
        t.set(fundRef, {
          totalIncome,
          totalExpense,
          clubFundBalance: totalIncome - totalExpense,
        }, { merge: true });
      });
      // === Cập nhật tổng hợp tháng (monthlySummary/financeSummaryByMonth) ===
      const d = new Date(createdAt);
      const year = d.getFullYear();
      const month = d.getMonth() + 1;
      const docId = `${year}-${String(month).padStart(2, "0")}`;
      const summaryRef = admin.firestore()
        .collection("clubStats").doc("monthlySummary")
        .collection("financeSummaryByMonth").doc(docId);
      await admin.firestore().runTransaction(async (t) => {
        const doc = await t.get(summaryRef);
        let { totalIncome = 0, totalExpense = 0 } = doc.exists ? doc.data() : {};
        totalExpense += Number(amount);
        t.set(summaryRef, {
          month,
          year,
          totalIncome,
          totalExpense,
          updatedAt: new Date()
        }, { merge: true });
      });
      // === END ===
      return res.status(201).json({ expenseId: docRef.id, ...expenseData });
    } catch (e) {
      console.error("createExpense error:", e);
      return res.status(500).json({ error: "Failed to create expense", message: e.message });
    }
  },

  /**
   * [GET] /finance/now - Lấy giờ hệ thống hiện tại (UTC và giờ Việt Nam)
   */
  getCurrentTime: (req, res) => {
    const nowUTC = new Date();
    const nowVN = new Date(nowUTC.getTime() + 7 * 60 * 60 * 1000);
    return res.status(200).json({
      utc: nowUTC.toISOString(),
      vietnam: nowVN.toISOString()
    });
  },

  // Thêm các hàm API khác ở đây, ví dụ:
  // createPaymentRequest: async (req, res) => {},
  // ...
};
