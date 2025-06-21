// src/app/api/generate/route.ts
import { NextResponse } from 'next/server';
import { faker } from '@faker-js/faker';
import { getRandomVietnameseName } from '@/utils/vietnameseNames';
import { admin } from '@/lib/firebase';

const db = admin.firestore();

type InputPayload = {
    userCount: number;
    matchCount: number;
    startDate: string;
    endDate: string;
    adminRatio?: number;
    penaltyPaidRatio?: number;
    donationRatio?: number;
};

export async function POST(req: Request) {
    const {
        userCount,
        matchCount,
        startDate,
        endDate,
        penaltyPaidRatio = 60,
        donationRatio = 50,
    }: InputPayload = await req.json();

    const start = new Date(startDate);
    const end = new Date(endDate);

    try {
        const userIds: string[] = [];

        for (let i = 0; i < userCount; i++) {
            const uid = faker.string.uuid();
            const role = 'member';
            const email = faker.internet.email({ provider: 'example.com' });
            userIds.push(uid);

            const createdAt = faker.date.between({ from: start, to: end });

            const password = '123456';

            await admin.auth().createUser({
                uid,
                email,
                emailVerified: true,
                password,
            });

            await db.collection('users').doc(uid).set({
                email,
                role,
                approvalStatus: faker.helpers.arrayElement(['approved', 'pending', 'rejected']),
                isDisabled: false,
                createdAt,
            });

            await db.collection('users').doc(uid).collection('profile').doc('info').set({
                fullName: getRandomVietnameseName(),
                gender: faker.helpers.arrayElement(['male', 'female']),
                birthDate: faker.date.birthdate(),
            });
        }

        const matchIds: string[] = [];

        // Tạo mảng thời gian trận đấu trước, sort theo thời gian
        const matchTimes = Array.from({ length: matchCount }, () =>
            faker.date.between({ from: start, to: end })
        ).sort((a, b) => a.getTime() - b.getTime());

        // Quản lý điểm cộng dồn của từng user
        const userScores: Record<string, { single: number; double: number }> = {};
        userIds.forEach(uid => {
            userScores[uid] = { single: 0, double: 0 };
        });

        for (let i = 0; i < matchCount; i++) {
            const matchId = faker.string.uuid();
            matchIds.push(matchId);

            const time = matchTimes[i];
            const type = faker.helpers.arrayElement(['single', 'double']);
            const setCount = faker.helpers.arrayElement([1, 3, 5]);

            await db.collection('matches').doc(matchId).set({
                matchId,
                startTime: time,
                type,
                setCount,
                status: 'finished',
                createdBy: faker.helpers.arrayElement(userIds),
            });

            const participants = faker.helpers.shuffle(userIds).slice(0, type === 'single' ? 2 : 4);
            // Map: userId -> team
            const teamMap: Record<string, number> = {};
            participants.forEach((uid, idx) => {
                const team = idx % 2 === 0 ? 1 : 2;
                teamMap[uid] = team;
            });

            // Tạo participants
            for (const userId of participants) {
                await db.collection('matches').doc(matchId).collection('participants').doc(userId).set({
                    matchId,
                    userId,
                    team: teamMap[userId],
                    isConfirmed: Math.random() < 0.9,
                });
            }

            // Tạo setResults + scoreHistories theo set
            for (let s = 1; s <= setCount; s++) {
                const setTime = new Date(time.getTime() + s * 5 * 60 * 1000); // mỗi set +5 phút

                const winningTeam = faker.helpers.arrayElement([1, 2]);
                let winnerScore = 11 + faker.number.int({ min: 0, max: 5 });
                let loserScore = winnerScore - faker.number.int({ min: 2, max: Math.min(5, winnerScore) });

                const team1Score = winningTeam === 1 ? winnerScore : loserScore;
                const team2Score = winningTeam === 2 ? winnerScore : loserScore;

                await db.collection('matches').doc(matchId).collection('setResults').add({
                    matchId,
                    setNumber: s,
                    team1Score,
                    team2Score,
                });

                // Cộng điểm cho từng user trong set
                for (const userId of participants) {
                    const team = teamMap[userId];
                    const isWinner = team === winningTeam;
                    const pointDelta = isWinner ? 0.1 : -0.1;

                    // Cộng vào tổng điểm
                    userScores[userId][type] += pointDelta;

                    await db.collection('users').doc(userId).collection('scoreHistories').add({
                        scoreId: faker.string.uuid(),
                        matchId,
                        scoreType: type,
                        scoreChange: pointDelta,
                        newTotalScore: userScores[userId][type],
                        createdAt: setTime,
                    });
                }
            }
        }

        const months = getMonthsBetween(start, end);
        for (const uid of userIds) {
            for (const month of months) {
                const requestId = faker.string.uuid();
                const createdAt = faker.date.between({ from: new Date(month + '-01'), to: new Date(month + '-25') });
                const isPaid = Math.random() > 0.3;



                await db.collection('users').doc(uid).collection('paymentRequests').doc(requestId).set({
                    requestId,
                    userId: uid,
                    amount: 100000,
                    type: 'fixed',
                    status: isPaid ? 'paid' : 'unpaid',
                    forMonth: new Date(month + '-01'),
                    createdAt,
                    paidAt: isPaid ? faker.date.soon({ days: 5, refDate: createdAt }) : null,
                });

                if (isPaid) {
                    const tid = faker.string.uuid();
                    await db.collection('users').doc(uid).collection('transactions').doc(tid).set({
                        transactionId: tid,
                        userId: uid,
                        type: 'fixed',
                        amount: 100000,
                        createdAt,
                    });

                    await db.collection('users').doc(uid).collection('financeLogs').doc(faker.string.uuid()).set({
                        logId: faker.string.uuid(),
                        userId: uid,
                        type: 'fixed_paid',
                        amount: 100000,
                        relatedRequestId: requestId,
                        transactionId: tid,
                        description: 'Đóng quỹ tháng ' + month,
                        createdAt,
                    });
                }

                const reminderId = faker.string.uuid();
                await db.collection('users').doc(uid).collection('fundReminders').doc(reminderId).set({
                    reminderId,
                    userId: uid,
                    type: 'fixed',
                    dueMonth: new Date(month + '-01'),
                    isSent: Math.random() > 0.2,
                    createdAt,
                });
            }

            if (Math.random() * 100 < donationRatio) {
                const donationAmount = faker.number.int({ min: 10000, max: 300000 });
                const createdAt = faker.date.between({ from: start, to: end });
                const tid = faker.string.uuid();

                await db.collection('users').doc(uid).collection('transactions').doc(tid).set({
                    transactionId: tid,
                    userId: uid,
                    type: 'donation',
                    amount: donationAmount,
                    createdAt,
                });

                await db.collection('users').doc(uid).collection('financeLogs').doc(faker.string.uuid()).set({
                    logId: faker.string.uuid(),
                    userId: uid,
                    type: 'donation',
                    amount: donationAmount,
                    transactionId: tid,
                    description: 'Ủng hộ tự nguyện',
                    createdAt,
                });
            }
        }

        for (const matchId of matchIds) {
            const matchDoc = await db.collection('matches').doc(matchId).get();
            const matchData = matchDoc.data();
            if (!matchData) continue;

            const participantsSnap = await db.collection('matches').doc(matchId).collection('participants').get();
            for (const doc of participantsSnap.docs) {
                const p = doc.data();
                const userId = p.userId;

                const isWinner = p.team === 1;
                if (isWinner) continue;

                const requestId = faker.string.uuid();
                const penaltyAmount = 10000;
                const createdAt = matchData.startTime.toDate?.() ?? new Date(matchData.startTime);
                const isPaid = Math.random() * 100 < penaltyPaidRatio;

                const paidAt = isPaid ? faker.date.soon({ days: 5, refDate: createdAt.toISOString() }) : null;

                await db.collection('users').doc(userId).collection('paymentRequests').doc(requestId).set({
                    requestId,
                    userId,
                    amount: penaltyAmount,
                    type: 'penalty',
                    status: isPaid ? 'paid' : 'unpaid',
                    matchId,
                    createdAt,
                    paidAt: isPaid ? faker.date.soon({ days: 5, refDate: createdAt }) : null,
                });

                let transactionId = null;
                if (isPaid) {
                    transactionId = faker.string.uuid();
                    await db.collection('users').doc(userId).collection('transactions').doc(transactionId).set({
                        transactionId,
                        userId,
                        type: 'penalty',
                        amount: penaltyAmount,
                        createdAt,
                    });
                }

                await db.collection('users').doc(userId).collection('financeLogs').doc(faker.string.uuid()).set({
                    logId: faker.string.uuid(),
                    userId,
                    type: 'penalty_incurred',
                    amount: penaltyAmount,
                    matchId,
                    relatedRequestId: requestId,
                    transactionId,
                    description: 'Phạt do thua trận',
                    createdAt,
                });
            }
        }

        const adminUser = userIds[0];
        for (let i = 0; i < 20; i++) {
            const createdAt = faker.date.between({ from: start, to: end });
            await db.collection('expenses').add({
                expenseId: faker.string.uuid(),
                createdBy: adminUser,
                reason: faker.helpers.arrayElement(['Mua vợt', 'Thuê sân', 'Mua bóng', 'Sửa lưới']),
                amount: faker.number.int({ min: 50000, max: 300000 }),
                createdAt,
            });
        }

        return NextResponse.json({ ok: true });
    } catch (err) {
        console.error(err);
        return NextResponse.json({ ok: false, error: (err as Error).message }, { status: 500 });
    }
}

function getMonthsBetween(start: Date, end: Date): string[] {
    const months: string[] = [];
    const current = new Date(start.getFullYear(), start.getMonth(), 1);
    const last = new Date(end.getFullYear(), end.getMonth(), 1);

    while (current <= last) {
        const m = `${current.getFullYear()}-${(current.getMonth() + 1).toString().padStart(2, '0')}`;
        months.push(m);
        current.setMonth(current.getMonth() + 1);
    }
    return months;
}
