const express = require("express");
const router = express.Router();
const userController = require("../controllers/userController");
const { verifyToken } = require("../middlewares/verifyToken");
const { requireAdmin } = require("../middlewares/requireAdmin");

const fileParser = require("express-multipart-file-parser");

// Người dùng gửi thông tin cá nhân của mình
router.post("/submit", verifyToken, userController.submitProfile);

// Quản lý trạng thái phê duyệt người dùng 
router.post("/:uid/approval-request", verifyToken, userController.submitApprovalRequest);
router.post("/:uid/reject", verifyToken, requireAdmin, userController.rejectUser);
router.post("/:uid/approve", verifyToken, requireAdmin, userController.approveUser);

router.post("/disable-user/:uid", verifyToken, requireAdmin, userController.disableUser);
router.post("/enable-user/:uid", verifyToken, requireAdmin, userController.enableUser);

router.get("/pending-users", verifyToken, requireAdmin, userController.getPendingUsers);
router.get("/disabled-users", verifyToken, requireAdmin, userController.getDisabledUsers);
router.get("/approved-users", verifyToken, requireAdmin, userController.getApprovedUsers);

// thông tin người dùng
router.post("/:uid/avatar", verifyToken, fileParser, userController.uploadAvatar);
router.get("/:uid/profile", verifyToken, userController.getUserProfile);
router.patch("/:uid/profile", verifyToken, userController.updateUserProfile);

// Bảng xếp hạng người dùng
router.get("/rankings/single", verifyToken, userController.getSingleRanking);
router.get("/rankings/double", verifyToken, userController.getDoubleRanking);


module.exports = router;
