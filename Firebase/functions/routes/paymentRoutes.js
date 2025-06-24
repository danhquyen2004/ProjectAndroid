const express = require("express");
const router = express.Router();
const paymentController = require("../controllers/paymentController");

// [POST] /payment/create-qr - Tạo QR thanh toán mock
router.post("/create-qr", paymentController.createPaymentQR);

// [POST] /payment/mock-callback - Xác nhận thanh toán mock
router.post("/mock-callback", paymentController.mockPaymentCallback);

// (Có thể bổ sung các API khác nếu cần)

module.exports = router;
