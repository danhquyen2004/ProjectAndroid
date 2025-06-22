const express = require("express");
const router = express.Router();
const { verifyToken } = require("../middlewares/verifyToken");
const { requireAdmin } = require("../middlewares/requireAdmin");
const matchController = require("../controllers/matchController");

// Lấy danh sách các trận đấu trong tháng, phân trang theo ngày
router.get("/monthly", verifyToken, matchController.listMonthlyMatches);
// Lấy danh sách các trận đấu theo ngày (YYYY-MM-DD)
router.get("/by-day", verifyToken, matchController.listMatchesByDay);
// Lấy danh sách các trận đấu theo ngày của một user (YYYY-MM-DD, userId)
router.get("/by-day/user", verifyToken, matchController.listUserMatchesByDay);
// Cập nhật tỉ số các set cho trận đấu đã tạo
router.put("/:matchId/scores", verifyToken, requireAdmin, matchController.updateMatchScores);
// Tạo trận đấu mới
router.post("/", verifyToken, requireAdmin, matchController.createMatch);
// Xóa 1 trận đấu (chỉ cho admin)
router.delete("/:matchId", verifyToken, requireAdmin, matchController.deleteMatch);
// Lấy chi tiết 1 trận đấu
router.get("/detail/:matchId", verifyToken, matchController.getMatchDetail);

module.exports = router;
