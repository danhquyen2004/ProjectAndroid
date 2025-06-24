const axios = require('axios');
const crypto = require('crypto');
const zalopayConfig = require('../utils/zalopayConfig');
const admin = require("../utils/firebase");
const { v4: uuidv4 } = require('uuid');
const CryptoJS = require('crypto-js'); // npm install crypto-js
const moment = require('moment-timezone');

// [POST] /payment/zalopay/create-order
exports.createZaloPayOrder = async (req, res) => {
  try {
    const { amount, userId, fundType, forMonth } = req.body;
    if (!amount || !userId || !fundType) {
      return res.status(400).json({ error: 'Missing amount, userId, or fundType' });
    }
    // Tạo mã giao dịch duy nhất
    const now = new Date();
    const transID = Math.floor(Math.random() * 1000000);

    // Chuẩn hóa id gửi sang ZaloPay để tránh ký tự đặc biệt
    const safeUserId = String(userId).replace(/[^a-zA-Z0-9_\-]/g, '').slice(0, 40) || 'user';
    const safeAppTransId = `${moment().tz('Asia/Ho_Chi_Minh').format('YYMMDD')}_${transID}`;
    const safeAmount = Math.max(1, parseInt(amount, 10));
    const safeCallbackUrl = 'https://us-central1-tlu-pickleball-459716.cloudfunctions.net/api/payment/zalopay-callback';
    const safeItem = JSON.stringify([{}]);

    const order = {
      app_id: zalopayConfig.app_id,
      app_trans_id: safeAppTransId,
      app_user: safeUserId,
      app_time: Date.now(),
      amount: safeAmount,
      item: safeItem,
      embed_data: JSON.stringify({}),
      description: `TLU Pickleball - Payment for the order #${transID}`,
      bank_code: 'zalopayapp', // Sử dụng ZaloPay app
      callback_url: safeCallbackUrl,
    };
    // Tạo mac
    const data = zalopayConfig.app_id + "|" + order.app_trans_id + "|" + order.app_user + "|" + order.amount + "|" + order.app_time + "|" + order.embed_data + "|" + order.item;
    order.mac = CryptoJS.HmacSHA256(data, zalopayConfig.key1).toString();
    // Debug: In ra order gửi đi
    console.log('ZaloPay order:', order);
    // Gửi request tới ZaloPay
    const response = await axios.post(zalopayConfig.endpoint, order);
    // Lưu trạng thái order vào Firestore để callback có thể lấy thông tin
    await admin.firestore().collection('paymentStatus').doc(safeAppTransId).set({
      status: 'pending',
      userId,
      fundType,
      forMonth: forMonth || null,
      amount: safeAmount,
      createdAt: new Date()
    }, { merge: true });
    if (response.data.return_code === 1) {
      // Thành công, trả về order_url (link QR)
      return res.status(200).json({
        order_url: response.data.order_url,
        zp_trans_token: response.data.zp_trans_token,
        app_trans_id: safeAppTransId
      });
    } else {
      return res.status(400).json({ error: 'ZaloPay error', detail: response.data });
    }
  } catch (e) {
    console.error('createZaloPayOrder error:', e);
    return res.status(500).json({ error: 'Failed to create ZaloPay order', message: e.message });
  }
};

// [GET] /payment/status?app_trans_id=...
exports.getPaymentStatus = async (req, res) => {
  try {
    const { app_trans_id } = req.query;
    if (!app_trans_id) {
      return res.status(400).json({ error: 'Missing app_trans_id' });
    }
    // Truy vấn trạng thái thanh toán từ Firestore (ví dụ lưu trạng thái vào collection 'paymentStatus')
    const statusDoc = await admin.firestore().collection('paymentStatus').doc(app_trans_id).get();
    if (!statusDoc.exists) {
      return res.status(200).json({ status: 'pending' });
    }
    return res.status(200).json({ status: statusDoc.data().status });
  } catch (e) {
    console.error('getPaymentStatus error:', e);
    return res.status(500).json({ error: 'Failed to get payment status', message: e.message });
  }
};

// [POST] /payment/zalopay-callback
exports.zaloPayCallback = async (req, res) => {
  try {
    const { app_trans_id, return_code } = req.body;
    if (!app_trans_id) {
      return res.status(400).json({ error: 'Missing app_trans_id' });
    }
    // Nếu thanh toán thành công (return_code === 1)
    if (String(return_code) === '1') {
      await admin.firestore().collection('paymentStatus').doc(app_trans_id).set({ status: 'success', updatedAt: new Date() }, { merge: true });
      const statusDoc = await admin.firestore().collection('paymentStatus').doc(app_trans_id).get();
      const info = statusDoc.exists ? statusDoc.data() : {};
      const { userId, fundType, forMonth } = info;
      if (userId && fundType) {
        if (fundType === 'donation') {
          // Chỉ tạo transaction cho donation, không cập nhật paymentRequests
          const amount = info.amount || 0;
          if (amount > 0) {
            const transactionId = uuidv4();
            const transactionRef = admin.firestore().collection('users').doc(userId).collection('transactions').doc(transactionId);
            await transactionRef.set({
              amount,
              createdAt: new Date(),
              transactionId,
              type: 'donation',
              userId
            });
            // Ghi log finance event vào users/{userId}/financeLogs
            const logId = uuidv4();
            await admin.firestore().collection('users').doc(userId).collection('financeLogs').doc(logId).set({
              logId,
              userId,
              type: 'donation',
              amount,
              transactionId,
              description: `Ủng hộ tự nguyện`,
              createdAt: new Date()
            });
          }
        } else if (fundType === 'fixed' || fundType === 'penalty') {
          const type = fundType;
          let paymentRequestsSnap = null;
          let totalAmount = 0;
          let relatedRequestIds = [];
          if (type === 'fixed' && forMonth) {
            // forMonth dạng 'YYYY-MM', ví dụ '2025-06'
            const [year, month] = forMonth.split('-').map(Number);
            const start = new Date(year, month - 1, 1);
            const end = new Date(year, month, 1);
            paymentRequestsSnap = await admin.firestore()
              .collection('users').doc(userId)
              .collection('paymentRequests')
              .where('type', '==', 'fixed')
              .where('forMonth', '>=', start)
              .where('forMonth', '<', end)
              .get();
          } else if (type === 'penalty' && forMonth) {
            // forMonth dạng 'YYYY-MM', ví dụ '2025-06'
            const [year, month] = forMonth.split('-').map(Number);
            const start = new Date(year, month - 1, 1);
            const end = new Date(year, month, 1);
            paymentRequestsSnap = await admin.firestore()
              .collection('users').doc(userId)
              .collection('paymentRequests')
              .where('type', '==', 'penalty')
              .where('createdAt', '>=', start)
              .where('createdAt', '<', end)
              .get();
          }
          if (paymentRequestsSnap) {
            const batch = admin.firestore().batch();
            paymentRequestsSnap.docs.forEach(doc => {
              const d = doc.data();
              totalAmount += d.amount || 0;
              relatedRequestIds.push(doc.id);
              batch.update(doc.ref, { status: 'paid', paidAt: new Date() });
            });
            await batch.commit();
            // Tạo bản ghi transaction chỉ với các trường yêu cầu
            if (totalAmount > 0) {
              const transactionId = uuidv4();
              const transactionRef = admin.firestore().collection('users').doc(userId).collection('transactions').doc(transactionId);
              await transactionRef.set({
                amount: totalAmount,
                createdAt: new Date(),
                transactionId,
                type,
                userId
              });
              // Ghi log finance event vào users/{userId}/financeLogs
              const logId = uuidv4();
              let description = '';
              if (type === 'fixed') {
                description = `Đóng quỹ tháng ${forMonth}`;
              }
              else if (type === 'penalty') {
                description = `Đóng phạt thua trận`;
              }
              await admin.firestore().collection('users').doc(userId).collection('financeLogs').doc(logId).set({
                logId,
                userId,
                type: type === 'fixed' ? 'fixed_paid' : 'penalty_paid',
                amount: totalAmount,
                relatedRequestId: relatedRequestIds,
                transactionId,
                description: `User ${userId} paid ${totalAmount} VND for ${type === 'fixed' ? 'fixed fund' : 'penalty'}`,
                createdAt: new Date()
              });
            }
            // Cộng thêm số tiền vào tổng thu và quỹ CLB
            const fundRef = admin.firestore().collection('clubStats').doc('fundBalance');
            await admin.firestore().runTransaction(async (t) => {
              const doc = await t.get(fundRef);
              const prevIncome = doc.exists && doc.data().totalIncome ? doc.data().totalIncome : 0;
              const prevBalance = doc.exists && doc.data().clubFundBalance ? doc.data().clubFundBalance : 0;
              t.set(fundRef, {
                totalIncome: prevIncome + totalAmount,
                clubFundBalance: prevBalance + totalAmount,
                updatedAt: new Date()
              }, { merge: true });
            });
            // Cộng thêm số tiền vào tổng thu theo tháng
            let summaryMonth = forMonth;
            if (!summaryMonth) {
              const now = new Date();
              summaryMonth = `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}`;
            }
            const [summaryYear, summaryMonthNum] = summaryMonth.split('-').map(Number);
            const monthlyRef = admin.firestore().collection('clubStats').doc('monthlySummary').collection('financeSummaryByMonth').doc(summaryMonth);
            await admin.firestore().runTransaction(async (t) => {
              const doc = await t.get(monthlyRef);
              const prevIncome = doc.exists && doc.data().totalIncome ? doc.data().totalIncome : 0;
              t.set(monthlyRef, {
                year: summaryYear,
                month: summaryMonthNum,
                totalIncome: prevIncome + totalAmount,
                updatedAt: new Date()
              }, { merge: true });
            });
          }
        }
      }
      // Có thể mở rộng cho donation nếu cần
    } else {
      await admin.firestore().collection('paymentStatus').doc(app_trans_id).set({ status: 'failed', updatedAt: new Date() }, { merge: true });
    }
    res.status(200).json({ return_code: 1, return_message: 'success' });
  } catch (e) {
    console.error('zaloPayCallback error:', e);
    res.status(500).json({ error: 'Failed to handle callback', message: e.message });
  }
};
