<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Dashboard Admin - TLU Pickleball Club</title>
  <link href="https://fonts.googleapis.com/css2?family=Montserrat&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="{{ asset('style.css') }}">
</head>
<body>
<div class="container-horizontal">
  <div class="sidebar">
    <div class="title">TLU PICKLEBALL CLUB</div>
    <img class="profile" src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" alt="Admin Avatar">
    <div class="admin-name">Admin</div>
    <div class="menu">
      <button class="active">Trang chủ</button>
      <button>Quản lý hội viên</button>
      <button>Quản lý quỹ</button>
      <button>Báo cáo</button>
      <button class="logout-btn" style="margin-top:auto;">Đăng xuất</button>
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
</div>
</body>
</html>