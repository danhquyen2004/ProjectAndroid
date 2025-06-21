'use client';

import { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

export default function HomePage() {
  const [userCount, setUserCount] = useState(5);
  const [matchCount, setMatchCount] = useState(10);
  const [startDate, setStartDate] = useState(new Date('2025-04-01'));
  const [endDate, setEndDate] = useState(new Date('2025-06-20'));
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [penaltyPaidRatio, setPenaltyPaidRatio] = useState(60);
  const [donationRatio, setDonationRatio] = useState(50);

  const handleSubmit = async () => {
    setLoading(true);
    setMessage('');
    try {
      const res = await fetch('/api/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userCount,
          matchCount,
          startDate: startDate.toISOString().slice(0, 10),
          endDate: endDate.toISOString().slice(0, 10),
          penaltyPaidRatio,
          donationRatio,
        }),
      });
      const result = await res.json();
      if (res.ok) {
        setMessage(result.message || '✅ Đã tạo dữ liệu thành công!');
      } else {
        setMessage(`❌ Thất bại: ${result.error || 'Không rõ lỗi'}`);
      }
    } catch (err) {
      setMessage('❌ Lỗi khi gửi dữ liệu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="max-w-xl mx-auto p-6 space-y-6">
      <h1 className="text-2xl font-bold">🧪 Tạo dữ liệu giả cho Firestore</h1>

      <div className="space-y-3">
        <div>
          <label>Số lượng User</label>
          <input
            type="number"
            value={userCount}
            onChange={e => setUserCount(+e.target.value)}
            className="border p-2 w-full"
          />
        </div>
        <div>
          <label>Số lượng Match</label>
          <input
            type="number"
            value={matchCount}
            onChange={e => setMatchCount(+e.target.value)}
            className="border p-2 w-full"
          />
        </div>
        <div>
          <label>Ngày bắt đầu</label>
          <DatePicker
            selected={startDate}
            onChange={(date: Date | null) => {
              if (date) setStartDate(date);
            }}
            dateFormat="yyyy-MM-dd"
            className="border p-2 w-full"
          />
        </div>
        <div>
          <label>Ngày kết thúc</label>
          <DatePicker
            selected={endDate}
            onChange={(date: Date | null) => {
              if (date) setEndDate(date);
            }}
            dateFormat="yyyy-MM-dd"
            className="border p-2 w-full"
          />
        </div>
      </div>
      <div>
        <label>% penalty đã được trả</label>
        <input
          type="number"
          value={penaltyPaidRatio}
          onChange={e => setPenaltyPaidRatio(+e.target.value)}
          className="border p-2 w-full"
        />
      </div>
      <div>
        <label>% user có donation</label>
        <input
          type="number"
          value={donationRatio}
          onChange={e => setDonationRatio(+e.target.value)}
          className="border p-2 w-full"
        />
      </div>

      <button
        onClick={handleSubmit}
        disabled={loading}
        className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 disabled:opacity-50"
      >
        {loading ? 'Đang xử lý...' : '🚀 Sinh dữ liệu'}
      </button>

      {message && <p className="text-green-600 font-semibold">{message}</p>}
    </main>
  );
}
