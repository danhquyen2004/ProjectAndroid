const express = require("express");
const router = express.Router();
const userController = require("../controllers/userController");
const { verifyToken } = require("../middlewares/verifyToken");
const { requireAdmin } = require("../middlewares/requireAdmin");

// Người dùng gửi thông tin cá nhân của mình
router.post("/submit", verifyToken, userController.submitProfile);

// Xem thông tin người dùng cụ thể (tự xem hoặc admin)
router.get("/:uid", verifyToken, userController.getUserById);
router.post("/", verifyToken, requireAdmin, userController.createUser);
router.put("/:uid", verifyToken, requireAdmin, userController.updateUser);
router.get("/", verifyToken, requireAdmin, userController.getAllUsers);
router.delete("/:uid", verifyToken, requireAdmin, userController.deleteUser);


module.exports = router;
