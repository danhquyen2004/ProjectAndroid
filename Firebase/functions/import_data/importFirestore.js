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
  for (const user of data.users) {
    const uid = user.uid || user.userId;

    await firestore.collection("users").doc(uid).set({
      email: user.email,
      role: user.role || "member",
      isDisabled: user.isDisabled || false,
      approvalStatus: user.approvalStatus || "pending",
      createdAt: toTimestamp(user.createdAt),
    });

    const profile = data.user_profiles[uid];
    if (profile) {
      await firestore
        .collection("users")
        .doc(uid)
        .collection("profile")
        .doc("info")
        .set({
          ...profile,
          avatar: profile.avatar || "https://example.com/default-avatar.png",
          birthDate: toTimestamp(profile.birthDate),
        });
    }

    const userId = user.userId;

    const transactions = data.transactions[userId] || [];
    for (const tx of transactions) {
      await firestore.collection('users').doc(userId).collection('transactions').doc(tx.transactionId).set({
        ...tx,
        createdAt: toTimestamp(tx.createdAt),
      });
    }

    const requests = data.payment_requests[userId] || [];
    for (const r of requests) {
      await firestore.collection('users').doc(userId).collection('paymentRequests').doc(r.requestId).set({
        ...r,
        createdAt: toTimestamp(r.createdAt),
        paidAt: r.paidAt ? toTimestamp(r.paidAt) : null,
      });
    }

    const logs = data.finance_logs[userId] || [];
    for (const log of logs) {
      await firestore.collection('users').doc(userId).collection('financeLogs').doc(log.logId).set({
        ...log,
        createdAt: toTimestamp(log.createdAt),
      });
    }

    const scores = data.score_histories[userId] || [];
    for (const s of scores) {
      await firestore.collection('users').doc(userId).collection('scoreHistories').doc(s.scoreId).set({
        ...s,
        createdAt: toTimestamp(s.createdAt),
      });
    }

    const reminders = data.fund_reminders[userId] || [];
    for (const r of reminders) {
      await firestore.collection('users').doc(userId).collection('fundReminders').doc(r.reminderId).set({
        ...r,
        createdAt: toTimestamp(r.createdAt),
      });
    }
  }

  for (const match of data.matches) {
    await firestore.collection('matches').doc(match.matchId).set({
      ...match,
      startTime: toTimestamp(match.startTime),
    });
  }

  for (const mp of data.match_participants) {
    const ref = firestore.collection('matches').doc(mp.matchId).collection('participants').doc(mp.userId);
    await ref.set({ ...mp });
  }

  for (const msr of data.match_set_results) {
    const docId = `set_${msr.setNumber}`;
    const ref = firestore.collection('matches').doc(msr.matchId).collection('setResults').doc(docId);
    await ref.set({ ...msr });
  }

  for (const exp of data.expenses) {
    await firestore.collection('expenses').doc(exp.expenseId).set({
      ...exp,
      createdAt: toTimestamp(exp.createdAt),
    });
  }

  console.log('✅ Dữ liệu mẫu đã được import thành công vào Firestore!');
}

module.exports = { importFirestoreFinalData };
