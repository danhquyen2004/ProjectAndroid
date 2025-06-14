// src/app/api/test-firebase/route.ts
import { db } from '@/lib/firebase';
import { NextResponse } from 'next/server';

export async function GET() {
  try {
    const testDocRef = db.collection('test').doc('connection-check');
    await testDocRef.set({
      message: 'Connection successful!',
      timestamp: new Date().toISOString(),
    });

    const doc = await testDocRef.get();

    return NextResponse.json({
      ok: true,
      message: doc.data()?.message,
    });
  } catch (error) {
    return NextResponse.json({ ok: false, error: (error as Error).message }, { status: 500 });
  }
}
