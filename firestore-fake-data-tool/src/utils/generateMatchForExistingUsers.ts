// src/app/api/generate/utils/generateMatchForExistingUsers.ts
import { db } from '@/lib/firebase';
import { faker } from '@faker-js/faker';

type MatchGenerationOptions = {
    userIds: string[];
    matchCount: number;
    start: Date;
    end: Date;
    penaltyPaidRatio: number;
    donationRatio: number;
};

export async function generateMatchesForUsers({
    userIds,
    matchCount,
    start,
    end,
    penaltyPaidRatio,
    donationRatio,
}: MatchGenerationOptions) {
    for (let i = 0; i < matchCount; i++) {
        const matchId = faker.string.uuid();
        const time = faker.date.between({ from: start, to: end });
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
        for (let j = 0; j < participants.length; j++) {
            const userId = participants[j];

            await db.collection('matches').doc(matchId).collection('participants').doc(userId).set({
                matchId,
                userId,
                team: j % 2 === 0 ? 1 : 2,
                isConfirmed: Math.random() < 0.9,
            });

            const isWinner = j % 2 === 0;
            const pointDelta = isWinner ? 0.1 : -0.1;
            const newScore = faker.number.float({ min: 0, max: 5, fractionDigits: 1 });

            await db.collection('users').doc(userId).collection('scoreHistories').add({
                scoreId: faker.string.uuid(),
                matchId,
                scoreChange: pointDelta,
                newTotalScore: newScore,
                createdAt: time,
            });
        }

        for (let s = 1; s <= setCount; s++) {
            let team1Score = 0;
            let team2Score = 0;

            const winningTeam = faker.helpers.arrayElement([1, 2]);
            let winnerScore = 11 + faker.number.int({ min: 0, max: 5 });
            let loserScore = winnerScore - faker.number.int({ min: 2, max: Math.min(5, winnerScore) });

            if (winningTeam === 1) {
                team1Score = winnerScore;
                team2Score = loserScore;
            } else {
                team1Score = loserScore;
                team2Score = winnerScore;
            }

            await db.collection('matches').doc(matchId).collection('setResults').add({
                matchId,
                setNumber: s,
                team1Score,
                team2Score,
            });
        }

        const participantsSnap = await db.collection('matches').doc(matchId).collection('participants').get();
        for (const doc of participantsSnap.docs) {
            const p = doc.data();
            const userId = p.userId;

            const isWinner = p.team === 1;
            if (isWinner) continue;

            const requestId = faker.string.uuid();
            const penaltyAmount = 10000;
            const createdAt = new Date(time);
            const isPaid = Math.random() * 100 < penaltyPaidRatio;

            const paidAt = isPaid ? faker.date.soon({ days: 5, refDate: createdAt }) : null;

            await db.collection('users').doc(userId).collection('paymentRequests').doc(requestId).set({
                requestId,
                userId,
                amount: penaltyAmount,
                type: 'penalty',
                status: isPaid ? 'paid' : 'unpaid',
                matchId,
                createdAt,
                paidAt,
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
}
