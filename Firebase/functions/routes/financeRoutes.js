const express = require("express");
const router = express.Router();
const { verifyToken } = require("../middlewares/verifyToken");
const { requireAdmin } = require("../middlewares/requireAdmin");
const financeController = require("../controllers/financeController");

// Định nghĩa các route tài chính tại đây
// Ví dụ:
// router.get("/logs", verifyToken, financeController.listFinanceLogs);
// router.post("/payment-request", verifyToken, financeController.createPaymentRequest);
// ...

module.exports = router;
