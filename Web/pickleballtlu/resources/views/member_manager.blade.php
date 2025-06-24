<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý hội viên - TLU Pickleball</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('style.css') }}">
</head>
<body>
<div class="container-horizontal">
    <!-- Sidebar -->
    <div class="sidebar">
        <div class="title">TLU PICKLEBALL CLUB</div>
        <img class="profile" src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" alt="Admin Avatar">
        <div class="admin-name">Admin</div>
        <div class="menu">
            <button>Trang chủ</button>
            <button class="active">Quản lý hội viên</button>
            <button>Quản lý quỹ</button>
            <button>Báo cáo</button>
            <button class="logout-btn" style="margin-top:auto;" onclick="openLogoutPopup()">Đăng xuất</button>
        </div>
    </div>

    <!-- Main content -->
    <div class="main">
        <div class="header">
            <h2>Quản lý hội viên</h2>
            <div class="search-add">
                <input type="text" placeholder="Tìm kiếm...">
                <button onclick="openPopup()">Thêm hội viên</button>
            </div>
        </div>

        <table>
            <thead>
            <tr>
                <th>Họ tên</th>
                <th>SĐT</th>
                <th>Giới tính</th>
                <th>Ngày sinh</th>
                <th>Đầu đơn</th>
                <th>Đầu đôi</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <!-- Chỉ trích xuất phần tbody cần sửa -->
            <tbody>
            @for ($i = 0; $i < 6; $i++)
                <tr>
                    <td><img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" width="30" height="30"> KarThi</td>
                    <td>03{{ $i }}4567891</td>
                    <td>{{ $i % 2 == 0 ? 'Nam' : 'Nữ' }}</td>
                    <td>08-Dec-2021</td>
                    <td>2.5</td>
                    <td>2.5</td>
                    <td>
                        <span class="tag {{ $i < 2 ? 'role-admin' : 'role-member' }}">
                            {{ $i < 2 ? 'Quản trị viên' : 'Hội viên' }}
                        </span>
                    </td>
                    <td>
                        <span class="tag {{ $i == 5 ? 'status-inactive' : 'status-active' }}">
                            {{ $i == 5 ? 'Không hoạt động' : 'Đang hoạt động' }}
                        </span>
                    </td>
                    <td class="actions">
                        <a>✏️</a>
                        @if ($i == 5)
                            <a>✅</a> <!-- Hiển thị kích hoạt nếu đang bị khóa -->
                        @else
                            <a>❌</a> <!-- Hiển thị khóa nếu đang hoạt động -->
                        @endif
                        <a>🗑️</a>
                    </td>
                </tr>
            @endfor
            </tbody>
        </table>

        <!-- POPUP FORM -->
        <div id="popup" class="popup-overlay" style="display: none;">
        <div class="popup-form">
            <h3>Thêm hội viên</h3>
            <form>
            <label>Họ và tên</label>
            <input type="text" placeholder="Nhập họ tên" required>

            <label>Số điện thoại</label>
            <input type="text" placeholder="Nhập số điện thoại" required>

            <label>Ngày sinh</label>
            <input type="text" placeholder="DD/MM/YYYY" required>

            <label>Giới tính</label>
            <div style="margin: 10px 0;">
                <input type="radio" name="gender" id="male" value="Nam">
                <label for="male">Nam</label>
                <input type="radio" name="gender" id="female" value="Nữ" checked>
                <label for="female">Nữ</label>
            </div>

            <div style="display: flex; justify-content: space-between;">
                <button type="submit" style="background-color: #007bff; color: white;">Xác nhận</button>
                <button type="button" onclick="closePopup()" style="background-color: white; color: #007bff; border: 1px solid #007bff;">Hủy</button>
            </div>
            </form>
        </div>
        </div>

        <!-- Popup xác nhận đăng xuất -->
        <div id="logoutPopup" class="popup-overlay" style="display: none;">
        <div class="popup-box">
            <div class="popup-icon">❗</div>
            <h3>Bạn chắc chắn có muốn đăng xuất</h3>
            <p>Phiên đăng nhập của bạn sẽ kết thúc</p>
            <div class="popup-actions">
            <button class="btn-logout">Đăng xuất</button>
            <button class="btn-cancel" onclick="closeLogoutPopup()">Hủy</button>
            </div>
        </div>
        </div>

        <div class="pagination">
            <div class="active">1</div>
            <div>2</div>
            <div>3</div>
            <div>></div>
        </div>
    </div>
</div>

    <script>
    function openPopup() {
        document.getElementById("popup").style.display = "flex";
    }

    function closePopup() {
        document.getElementById("popup").style.display = "none";
    }
    </script>

    <script>
    function openLogoutPopup() {
        document.getElementById("logoutPopup").style.display = "flex";
    }

    function closeLogoutPopup() {
        document.getElementById("logoutPopup").style.display = "none";
    }

    // Gắn sự kiện cho nút Đăng xuất trong sidebar
    document.addEventListener("DOMContentLoaded", function () {
        const logoutBtn = document.querySelector(".menu button:last-child");
        logoutBtn.addEventListener("click", openLogoutPopup);
    });

    document.querySelector(".btn-logout").addEventListener("click", function () {
    window.location.href = "/login"; // hoặc thực hiện gọi API logout
    });
    </script>

</body>
</html>

