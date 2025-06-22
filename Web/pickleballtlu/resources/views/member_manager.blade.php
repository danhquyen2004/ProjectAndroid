<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý hội viên - TLU Pickleball</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Montserrat', sans-serif;
            margin: 0;
            background: #fcfcfc;
        }
        .container {
            display: flex;
            height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 160px;
            background-color: #bad6eb;
            padding: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .sidebar .title {
            font-weight: bold;
            font-size: 18px;
            margin-bottom: 20px;
        }
        .sidebar img.profile {
            width: 100px;
            height: 100px;
            border-radius: 100%;
        }
        .sidebar .admin-name {
            color: #007bff;
            margin: 10px 0 20px;
        }
        .sidebar .menu button {
            width: 100%;
            background: none;
            border: none;
            padding: 10px 0;
            text-align: left;
            font-size: 14px;
            cursor: pointer;
        }
        .sidebar .menu .active {
            background-color: #0c8ce9;
            color: white;
            border-radius: 6px;
            padding-left: 10px;
        }

        /* Main */
        .main {
            flex: 1;
            padding: 15px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h2 {
            margin: 0;
        }
        .header .search-add {
            display: flex;
            gap: 10px;
        }
        .search-add input[type="text"] {
            padding: 8px;
            width: 200px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        .search-add button {
            padding: 8px 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }

        /* Table */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: white;
            border-radius: 8px;
            overflow: hidden;
        }
        th, td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
        }
        th {
            background-color: #f3f3f3;
        }

        .tag {
            padding: 4px 8px;
            border-radius: 10px;
            font-size: 12px;
            display: inline-block;
        }
        .role-admin { background-color: #f0d6fd; color: #7a42a6; }
        .role-member { background-color: #d8eafd; color: #007bff; }

        .status-active { background-color: #d4fcd4; color: #1f8732; }
        .status-inactive { background-color: #fcd4d4; color: #c62828; }

        .actions i {
            cursor: pointer;
            margin: 0 4px;
        }

        /* Pagination */
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
            gap: 10px;
        }

        .pagination div {
            padding: 6px 12px;
            border: 1px solid #ccc;
            border-radius: 50%;
            cursor: pointer;
        }

        .pagination .active {
            background-color: #007bff;
            color: white;
            border: none;
        }

        .popup-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(128, 128, 128, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 999;
        }

        .popup-form {
            background-color: white;
            padding: 30px;
            border-radius: 12px;
            width: 400px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
        }

        .popup-form h3 {
            text-align: center;
            margin-bottom: 20px;
        }

        .popup-form label {
            display: block;
            margin-top: 10px;
        }

        .popup-form input[type="text"] {
            width: 100%;
            padding: 8px;
            margin-top: 4px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .popup-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.3);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 999;
        }

        .popup-box {
            background-color: white;
            padding: 30px 40px;
            border-radius: 12px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            max-width: 400px;
        }

        .popup-box h3 {
            font-size: 18px;
            margin-bottom: 10px;
            font-weight: bold;
        }

        .popup-box p {
            font-size: 14px;
            color: #666;
            margin-bottom: 20px;
        }

        .popup-icon {
            font-size: 30px;
            color: red;
            margin-bottom: 10px;
        }

        .popup-actions {
            display: flex;
            justify-content: center;
            gap: 20px;
        }

        .btn-logout {
            background-color: red;
            color: white;
            border: none;
            padding: 10px 18px;
            border-radius: 8px;
            cursor: pointer;
        }

        .btn-cancel {
            background-color: white;
            color: #333;
            border: 1px solid #ccc;
            padding: 10px 18px;
            border-radius: 8px;
            cursor: pointer;
        }

    </style>
</head>
<body>
<div class="container">
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
            <button onclick="openLogoutPopup()">Đăng xuất</button>
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

