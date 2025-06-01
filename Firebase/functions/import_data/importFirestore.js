
const admin = require('firebase-admin');
const data = require('./firestore_seed_data.json');

if (!admin.apps.length) {
  admin.initializeApp();
}

const firestore = admin.firestore();

function toTimestamp(ts) {
  if (!ts || typeof ts !== 'object' || !ts._seconds) return null;
  return admin.firestore.Timestamp.fromMillis(ts._seconds * 1000);
}

async function importFirestoreSampleData() {
  // USERS
  for (const user of data.users) {
    await firestore.collection('users').doc(user.uid).set({
      role: user.role,
      isApproved: user.isApproved,
      isDisabled: user.isDisabled,
      createdAt: toTimestamp(user.createdAt),
    });

    // PROFILE
    const profile = data.user_profiles[user.uid];
    if (profile) {
      await firestore.collection('users').doc(user.uid).collection('profile').doc('info').set({
        ...profile,
        birthDate: toTimestamp(profile.birthDate),
      });
    }

    // TRANSACTIONS
    const transactions = data.transactions[user.uid] || [];
    for (const tx of transactions) {
      await firestore.collection('users').doc(user.uid).collection('transactions').doc(tx.transactionId).set({
        ...tx,
        createdAt: toTimestamp(tx.createdAt),
      });
    }

    // PAYMENT REQUESTS
    const requests = data.payment_requests[user.uid] || [];
    for (const r of requests) {
      await firestore.collection('users').doc(user.uid).collection('paymentRequests').doc(r.requestId).set({
        ...r,
        createdAt: toTimestamp(r.createdAt),
        paidAt: r.paidAt ? toTimestamp(r.paidAt) : null,
      });
    }

    // FINANCE LOGS
    const logs = data.finance_logs[user.uid] || [];
    for (const log of logs) {
      await firestore.collection('users').doc(user.uid).collection('financeLogs').doc(log.logId).set({
        ...log,
        createdAt: toTimestamp(log.createdAt),
      });
    }

    // SCORE HISTORIES
    const scores = data.score_histories[user.uid] || [];
    for (const s of scores) {
      await firestore.collection('users').doc(user.uid).collection('scoreHistories').doc(s.scoreId).set({
        ...s,
        createdAt: toTimestamp(s.createdAt),
      });
    }
  }

  // MATCHES
  for (const match of data.matches) {
    await firestore.collection('matches').doc(match.matchId).set({
      ...match,
      startTime: toTimestamp(match.startTime),
    });
  }

  // EXPENSES
  for (const exp of data.expenses) {
    await firestore.collection('expenses').doc(exp.expenseId).set({
      ...exp,
      createdAt: toTimestamp(exp.createdAt),
    });
  }

  console.log('✅ Dữ liệu mẫu đã được import thành công vào Firestore');
}

module.exports = { importFirestoreSampleData };