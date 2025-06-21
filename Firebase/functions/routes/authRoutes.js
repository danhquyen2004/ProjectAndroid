const express = require("express");
const router = express.Router();
const authController = require("../controllers/authController");

router.post("/register", authController.register);
router.post("/login", authController.login);
router.post("/send-verification", authController.sendVerificationEmail);
router.get("/verify-status/:uid", authController.checkEmailVerified);

module.exports = router;
