// Script: aggregateClubFinanceSummaryByMonth.js
// Chạy script này 1 lần để tổng hợp dữ liệu thu/chi từng tháng và ghi vào Firestore: clubStats/financeSummaryByMonth/{YYYY-MM}

const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

function safeToDate(val, context) {
  // Firestore Timestamp detection (firebase-admin or web)
  if (val && typeof val === 'object') {
    // firebase-admin Timestamp
    if (typeof val.toDate === 'function') {
      try {
        const d = val.toDate();
        if (isNaN(d.getTime())) throw new Error('Invalid date from Timestamp');
        return d;
      } catch (e) {
        console.warn(`[WARN] Lỗi ngày tháng (Timestamp toDate fail):`, { context, value: val });
        return null;
      }
    }
    // Firestore web Timestamp (seconds, nanoseconds)
    if ('_seconds' in val && '_nanoseconds' in val) {
      try {
        const d = new Date(val._seconds * 1000 + Math.floor(val._nanoseconds / 1e6));
        if (isNaN(d.getTime())) throw new Error('Invalid date from _seconds/_nanoseconds');
        return d;
      } catch (e) {
        console.warn(`[WARN] Lỗi ngày tháng (Timestamp _seconds/_nanoseconds fail):`, { context, value: val });
        return null;
      }
    }
  }
  let d = val instanceof Date ? val : new Date(val);
  if (isNaN(d.getTime())) {
    console.warn(`[WARN] Lỗi ngày tháng:`, { context, value: val });
    return null;
  }
  return d;
}

async function aggregateClubFinanceSummaryByMonth() {
  const usersSnap = await admin.firestore().collection("users").get();
  const allMonths = new Set();
  const monthKey = (date) => {
    const d = safeToDate(date, "monthKey");
    if (!d) return null;
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;
  };
  // 1. Quét toàn bộ paymentRequests và transactions để lấy các tháng có phát sinh thu
  for (const userDoc of usersSnap.docs) {
    const userId = userDoc.id;
    // paymentRequests (fixed, penalty)
    const prSnap = await admin.firestore().collection("users").doc(userId).collection("paymentRequests").get();
    prSnap.docs.forEach(doc => {
      const d = doc.data();
      if (d.status === "paid") {
        if (d.type === "fixed" && d.forMonth) {
          const dt = safeToDate(d.forMonth, { userId, docId: doc.id, collection: "paymentRequests", field: "forMonth" });
          if (dt) allMonths.add(monthKey(dt));
        }
        if (d.type === "penalty" && d.createdAt) {
          const dt = safeToDate(d.createdAt, { userId, docId: doc.id, collection: "paymentRequests", field: "createdAt" });
          if (dt) allMonths.add(monthKey(dt));
        }
      }
    });
    // transactions (donation)
    const trSnap = await admin.firestore().collection("users").doc(userId).collection("transactions").get();
    trSnap.docs.forEach(doc => {
      const d = doc.data();
      if (d.type === "donation" && d.createdAt) {
        const dt = safeToDate(d.createdAt, { userId, docId: doc.id, collection: "transactions", field: "createdAt" });
        if (dt) allMonths.add(monthKey(dt));
      }
    });
  }
  // 2. Quét toàn bộ expenses để lấy các tháng có phát sinh chi
  const expensesSnap = await admin.firestore().collection("expenses").get();
  expensesSnap.docs.forEach(doc => {
    const d = doc.data();
    if (d.createdAt) {
      const dt = safeToDate(d.createdAt, { docId: doc.id, collection: "expenses", field: "createdAt" });
      if (dt) allMonths.add(monthKey(dt));
    }
  });
  // 3. Tổng hợp từng tháng
  for (const m of allMonths) {
    const [year, month] = m.split("-").map(Number);
    const startDate = new Date(year, month - 1, 1, 0, 0, 0);
    const endDate = new Date(year, month, 0, 23, 59, 59, 999);
    let totalIncome = 0;
    let totalExpense = 0;
    // Thu: paymentRequests (fixed, penalty, status=paid)
    for (const userDoc of usersSnap.docs) {
      const userId = userDoc.id;
      // fixed
      const fixedSnap = await admin.firestore().collection("users").doc(userId).collection("paymentRequests")
        .where("type", "==", "fixed")
        .where("status", "==", "paid")
        .where("forMonth", ">=", new Date(year, month - 1, 1))
        .where("forMonth", "<=", new Date(year, month - 1, 31))
        .get();
      fixedSnap.docs.forEach(doc => { totalIncome += doc.data().amount || 0; });
      // penalty
      const penaltySnap = await admin.firestore().collection("users").doc(userId).collection("paymentRequests")
        .where("type", "==", "penalty")
        .where("status", "==", "paid")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .get();
      penaltySnap.docs.forEach(doc => { totalIncome += doc.data().amount || 0; });
      // donation
      const donationSnap = await admin.firestore().collection("users").doc(userId).collection("transactions")
        .where("type", "==", "donation")
        .where("createdAt", ">=", startDate)
        .where("createdAt", "<=", endDate)
        .get();
      donationSnap.docs.forEach(doc => { totalIncome += doc.data().amount || 0; });
    }
    // Chi: expenses
    const expSnap = await admin.firestore().collection("expenses")
      .where("createdAt", ">=", startDate)
      .where("createdAt", "<=", endDate)
      .get();
    expSnap.docs.forEach(doc => { totalExpense += doc.data().amount || 0; });
    // Ghi vào Firestore
    await admin.firestore().collection("clubStats").doc("monthlySummary").collection("financeSummaryByMonth").doc(m).set({
      month,
      year,
      totalIncome,
      totalExpense,
      updatedAt: new Date()
    });
    console.log(`Đã ghi tổng hợp tháng ${m}: income=${totalIncome}, expense=${totalExpense}`);
  }
  console.log("Hoàn thành tổng hợp dữ liệu tài chính theo tháng.");
}

// Chạy script
aggregateClubFinanceSummaryByMonth().then(() => process.exit(0)).catch(e => { console.error(e); process.exit(1); });
