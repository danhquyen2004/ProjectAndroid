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
      position: fixed;
      width: 300px;
      height: 100vh;
      background-color: #bad6eb;
      padding: 20px;
    }
    .sidebar .logo {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 30px;
    }
    .sidebar .profile {
      text-align: center;
    }
    .sidebar .profile img {
      width: 142px;
      height: 173px;
    }
    .sidebar .menu {
      margin-top: 30px;
      display: flex;
      flex-direction: column;
      gap: 20px;
      padding-left: 40px;
    }
    .sidebar .menu a {
      text-decoration: none;
      color: black;
      padding: 10px;
      border-radius: 5px;
    }
    .sidebar .menu a.active {
      background-color: #0c8ce9;
      color: white;
    }
    .content {
      margin-left: 300px;
      padding: 90px 40px 40px;
    }
    .card {
      background-color: #e1f3ff;
      border-radius: 8px;
      padding: 20px;
      width: fit-content;
      margin-right: 40px;
      display: inline-block;
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
  <div class="sidebar">
    <div class="logo">
      <img src="https://c.animaapp.com/mbunmuejMSouiM/img/line-1.svg" width="4" height="23" alt="Line" />
      <strong>TLU PICKLEBALL CLUB</strong>
    </div>
    <div class="profile">
      <img src="https://c.animaapp.com/mbunmuejMSouiM/img/profile.png" alt="Profile" />
      <div class="text-blue-600">Admin</div>
    </div>
    <div class="menu">
      <a href="#" class="active">Trang chủ</a>
      <a href="#">Quản lý hội viên</a>
      <a href="#">Quản lý quỹ</a>
      <a href="#">Báo cáo</a>
      <a href="#">Cài đặt</a>
      <a href="#">Đăng xuất</a>
    </div>
  </div>

  <header>
    <div>&#9660;</div>
    <div class="user-info">
      <img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" alt="Profile" />
      <div>Admin</div>
    </div>
  </header>

  <div class="email">admin@gmail.com</div>

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
