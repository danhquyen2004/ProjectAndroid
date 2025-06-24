const express = require('express');
const router = express.Router();
const paymentController = require('../controllers/paymentController');

// Tạo đơn hàng ZaloPay (tạo QR)
router.post('/zalopay/create-order', paymentController.createZaloPayOrder);

// Callback từ ZaloPay
router.post('/zalopay-callback', paymentController.zaloPayCallback);

// Lấy trạng thái thanh toán
router.get('/status', paymentController.getPaymentStatus);

module.exports = router;
