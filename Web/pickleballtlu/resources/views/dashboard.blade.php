<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Dashboard Admin - TLU Pickleball Club</title>
  <link href="https://fonts.googleapis.com/css2?family=Montserrat&display=swap" rel="stylesheet" />
  <style>
    * {
      box-sizing: border-box;
      font-family: 'Montserrat', sans-serif;
    }
    body {
      margin: 0;
      background-color: #fcfcfc;
    }
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
      font-size: 16px;
      text-align: center;
      margin-bottom: 20px;
    }

    .sidebar img.profile {
      width: 100px;
      height: 100px;
      border-radius: 100%;
    }

    .sidebar .admin-name {
      color: #007bff;
      font-weight: bold;
      margin: 10px 0 20px;
    }

    .sidebar .menu {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .sidebar .menu button {
      width: 100%;
      background: none;
      border: none;
      padding: 10px;
      text-align: left;
      font-size: 14px;
      cursor: pointer;
      border-radius: 6px;
      transition: background-color 0.2s;
    }

    .sidebar .menu button:hover {
      background-color: #aad0ee;
    }

    .sidebar .menu .active {
      background-color: #0c8ce9;
      color: white;
      padding-left: 10px;
    }

    .content {
      margin-left: 160px; /* khớp với chiều rộng sidebar */
      padding: 20px;
      display: flex;
      justify-content: center; /* căn giữa ngang */
      gap: 40px; /* khoảng cách giữa các card */
      margin-top: 60px; /* tạo khoảng cách phía trên */
    }
    .card {
      background-color: #e1f3ff;
      border-radius: 8px;
      padding: 20px;
      width: 200px;
      text-align: center;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    }
    .card img {
      width: 50px;
      height: 50px;
    }
    .card .title {
      margin-top: 10px;
      color: #6c6b6b;
    }
    .card .value {
      font-size: 24px;
      font-weight: bold;
    }
    header {
      position: fixed;
      top: 0;
      left: 300px;
      width: calc(100% - 300px);
      background: white;
      height: 60px;
      box-shadow: 0 4px 15px #00000040;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 30px;
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .user-info img {
      width: 25px;
      height: 25px;
      border-radius: 50%;
    }
    .email {
      position: fixed;
      top: 20px;
      right: 10px;
      font-size: 10px;
      color: black;
    }
  </style>
</head>
<body>
  <div>
    
  </div>
  <div class="sidebar">
    <div class="title">TLU PICKLEBALL CLUB</div>
    <img class="profile" src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" alt="Admin Avatar">
    <div class="admin-name">Admin</div>
    <div class="menu">
      <button class="active">Trang chủ</button>
      <button>Quản lý hội viên</button>
      <button>Quản lý quỹ</button>
      <button>Báo cáo</button>
      <button>Cài đặt</button>
      <button>Đăng xuất</button>
    </div>
  </div>

  <div class="content">
    <div class="card">
      <img src="https://c.animaapp.com/mbunmuejMSouiM/img/user.svg" alt="User" />
      <div class="title">Số hội viên</div>
      <div class="value">50</div>
    </div>

    <div class="card">
      <img src="https://c.animaapp.com/mbunmuejMSouiM/img/usd-square-1-1.svg" alt="Quỹ" />
      <div class="title">TỔNG QUỸ</div>
      <div class="value">55,000,000 VND</div>
    </div>
  </div>
</body>
</html>
