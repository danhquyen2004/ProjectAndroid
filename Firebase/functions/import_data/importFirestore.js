const admin = require('firebase-admin');
const data = require('./firestore_seed_data_final.json');

if (!admin.apps.length) {
  admin.initializeApp();
}

const firestore = admin.firestore();

function toTimestamp(ts) {
  if (!ts || typeof ts !== 'object' || !ts._seconds) return null;
  return admin.firestore.Timestamp.fromMillis(ts._seconds * 1000);
}

async function importFirestoreFinalData() {
  // USERS
  for (const user of data.users) {
    await firestore.collection('users').doc(user.userId).set({
      email: user.email,
      passwordHash: user.passwordHash,
      role: user.role,
      isApproved: user.isApproved,
      isDisabled: user.isDisabled,
      createdAt: toTimestamp(user.createdAt),
    });

    // PROFILE
    const profile = data.user_profiles[user.userId];
    if (profile) {
      await firestore.collection('users').doc(user.userId).collection('profile').doc('info').set({
        ...profile,
        birthDate: toTimestamp(profile.birthDate),
      });
    }

    // TRANSACTIONS
    const transactions = data.transactions[user.userId] || [];
    for (const tx of transactions) {
      await firestore.collection('users').doc(user.userId).collection('transactions').doc(tx.transactionId).set({
        ...tx,
        createdAt: toTimestamp(tx.createdAt),
      });
    }

    // PAYMENT REQUESTS
    const requests = data.payment_requests[user.userId] || [];
    for (const r of requests) {
      await firestore.collection('users').doc(user.userId).collection('paymentRequests').doc(r.requestId).set({
        ...r,
        createdAt: toTimestamp(r.createdAt),
        paidAt: r.paidAt ? toTimestamp(r.paidAt) : null,
      });
    }

    // FINANCE LOGS
    const logs = data.finance_logs[user.userId] || [];
    for (const log of logs) {
      await firestore.collection('users').doc(user.userId).collection('financeLogs').doc(log.logId).set({
        ...log,
        createdAt: toTimestamp(log.createdAt),
      });
    }

    // SCORE HISTORIES
    const scores = data.score_histories[user.userId] || [];
    for (const s of scores) {
      await firestore.collection('users').doc(user.userId).collection('scoreHistories').doc(s.scoreId).set({
        ...s,
        createdAt: toTimestamp(s.createdAt),
      });
    }

    // FUND REMINDERS
    const reminders = data.fund_reminders[user.userId] || [];
    for (const r of reminders) {
      await firestore.collection('users').doc(user.userId).collection('fundReminders').doc(r.reminderId).set({
        ...r,
        createdAt: toTimestamp(r.createdAt),
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

  // MATCH PARTICIPANTS
  for (const mp of data.match_participants) {
    const ref = firestore.collection('matches').doc(mp.matchId).collection('participants').doc(mp.userId);
    await ref.set({
      ...mp,
    });
  }

  // MATCH SET RESULTS
  for (const msr of data.match_set_results) {
    const docId = `set_${msr.setNumber}`;
    const ref = firestore.collection('matches').doc(msr.matchId).collection('setResults').doc(docId);
    await ref.set({
      ...msr,
    });
  }

  // EXPENSES
  for (const exp of data.expenses) {
    await firestore.collection('expenses').doc(exp.expenseId).set({
      ...exp,
      createdAt: toTimestamp(exp.createdAt),
    });
  }

  console.log('✅ Dữ liệu mẫu đã được import thành công vào Firestore!');
}

module.exports = { importFirestoreFinalData };
