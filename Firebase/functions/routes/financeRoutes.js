const express = require("express");
const router = express.Router();
const { verifyToken } = require("../middlewares/verifyToken");
const { requireAdmin } = require("../middlewares/requireAdmin");
const financeController = require("../controllers/financeController");

// Định nghĩa các route tài chính tại đây
// Ví dụ:
// router.get("/logs", verifyToken, financeController.listFinanceLogs);
// router.post("/payment-request", verifyToken, financeController.createPaymentRequest);
// Lấy trạng thái quỹ của 1 user theo tháng
router.get("/fund-status/:userId", verifyToken, financeController.getUserFundStatusByMonth);
// Lấy log tài chính của 1 user theo tháng
router.get("/finance-logs/:userId", verifyToken, financeController.getUserFinanceLogsByMonth);
// Lấy tổng số quỹ còn lại của CLB
router.get("/club/balance", verifyToken, financeController.getClubFundBalance);
// Lấy tổng thu, tổng chi của CLB theo tháng
router.get("/club/summary", verifyToken, financeController.getClubFinanceSummaryByMonth);
// Lấy lịch sử chi tiêu của CLB theo tháng
router.get("/club/expenses", verifyToken, financeController.getClubExpenseLogsByMonth);
// Lấy danh sách trạng thái quỹ của các thành viên theo tháng
router.get("/fund-status-all", verifyToken, financeController.getAllUserFundStatusByMonth);
// Thêm một khoản chi tiêu mới cho CLB (chỉ admin)
router.post("/club/expense", verifyToken, requireAdmin, financeController.createExpense);

router.get("/now",financeController.getCurrentTime);
module.exports = router;
