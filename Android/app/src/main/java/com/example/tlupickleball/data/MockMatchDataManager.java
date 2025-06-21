package com.example.tlupickleball.data;

import com.example.tlupickleball.R; // Đảm bảo import R nếu bạn dùng drawable từ đây
import com.example.tlupickleball.model.Matches;

import java.util.ArrayList;
import java.util.List;

public class MockMatchDataManager implements MatchDataManager {

    private List<Matches> mockMatches;

    public MockMatchDataManager() {
        // Khởi tạo dữ liệu ảo
        mockMatches = new ArrayList<>();
        mockMatches.add(new Matches("Nguyễn Văn A", "Trần Thị B", R.drawable.avatar_1, R.drawable.avatar_1, "21-19, 21-15", "10:00 AM", "Đã kết thúc"));
        mockMatches.add(new Matches("Lê Văn C", "Phạm Thị D", R.drawable.avatar_1, R.drawable.avatar_1, "Đang diễn ra", "11:30 AM", "Đang diễn ra"));
        mockMatches.add(new Matches("Hoàng Minh E", "Vũ Thị F", R.drawable.avatar_1, R.drawable.avatar_1, "Chưa bắt đầu", "01:00 PM", "Sắp diễn ra"));
        // Thêm các trận đấu ảo khác nếu cần
    }

    @Override
    public void getAvailableMatches(MatchDataCallback callback) {
        // Giả lập độ trễ mạng
        new android.os.Handler().postDelayed(() -> {
            if (mockMatches != null && !mockMatches.isEmpty()) {
                callback.onSuccess(mockMatches);
            } else {
                callback.onError("Không có trận đấu nào.");
            }
        }, 1000); // Giả lập 1 giây độ trễ
    }

    @Override
    public void submitNewMatch(Matches match, SubmitMatchCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            mockMatches.add(match); // Thêm trận đấu mới vào danh sách ảo
            callback.onSuccess("Trận đấu đã được ghi nhận thành công (dữ liệu ảo)!");
        }, 1000);
    }
}