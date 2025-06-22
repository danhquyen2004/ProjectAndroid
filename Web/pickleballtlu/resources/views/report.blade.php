<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Báo cáo tổng hợp - TLU Pickleball</title>
  <link href="https://fonts.googleapis.com/css2?family=Montserrat&display=swap" rel="stylesheet">
  <style>
    body {
      margin: 0;
      font-family: 'Montserrat', sans-serif;
      background-color: #fcfcfc;
    }

    .container {
      display: flex;
      height: 100vh;
    }

    /* Sidebar */
    .sidebar {
      width: 160px;
      background-color: #bad6eb;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-top: 20px;
    }

    .sidebar .title {
      font-weight: bold;
      font-size: 18px;
      margin-bottom: 10px;
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

    .sidebar .menu {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 10px;
      padding: 0 20px;
    }

    .sidebar .menu button {
      background: none;
      border: none;
      padding: 10px;
      text-align: left;
      cursor: pointer;
      font-size: 14px;
      border-radius: 6px;
    }

    .sidebar .menu .active {
      background-color: #0c8ce9;
      color: white;
    }

    /* Main */
    .main {
      flex: 1;
      padding: 30px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .header h2 {
      margin: 0;
    }

    .header .actions {
      display: flex;
      gap: 10px;
    }

    .header .actions i {
      cursor: pointer;
      font-size: 20px;
    }

    .report-box {
      background-color: white;
      border: 1px solid #aaa;
      width: 500px;
      margin: 40px auto;
      padding: 30px;
      box-sizing: border-box;
    }

    .report-box h3,
    .report-box p,
    .report-box table {
      text-align: center;
    }

    .report-box h3 {
      margin-bottom: 10px;
    }

    .report-box .header-line {
      margin: 0;
      font-size: 14px;
      font-weight: bold;
    }

    .report-box .slogan {
      font-size: 13px;
      margin-bottom: 20px;
    }

    .report-box .date-line {
      text-align: right;
      margin: 20px 0 10px;
      font-size: 13px;
    }

    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 10px;
    }

    th, td {
      border: 1px solid black;
      padding: 6px;
      font-size: 14px;
    }

    th {
      background-color: #f8f8f8;
    }

    .sign {
      margin-top: 40px;
      text-align: right;
      font-size: 14px;
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
      <button>DS Hội viên</button>
      <button>Quỹ</button>
      <button class="active">Báo cáo</button>
      <button>Đăng xuất</button>
    </div>
  </div>

  <!-- Main -->
  <div class="main">
    <div class="header">
      <h2>Báo cáo tổng hợp</h2>
      <div class="actions">
        <a onclick="window.print()">🖨️</a>
      </div>
    </div>

    <div class="report-box">
      <p class="header-line">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
      <p class="slogan">Độc lập - Tự do - Hạnh phúc</p>

      <p class="date-line">..........., ngày ...... tháng ...... năm 20....</p>

      <h3>BÁO CÁO TỔNG HỢP TPC</h3>

      <table>
        <thead>
        <tr>
          <th>STT</th>
          <th>Chi tiêu</th>
          <th>Giá tiền</th>
          <th>Người mua</th>
        </tr>
        </thead>
        <tbody>
        <tr><td>1</td><td></td><td></td><td></td></tr>
        <tr><td>2</td><td></td><td></td><td></td></tr>
        <tr><td>3</td><td></td><td></td><td></td></tr>
        <tr><td>4</td><td></td><td></td><td></td></tr>
        <tr><td>5</td><td></td><td></td><td></td></tr>
        </tbody>
      </table>

      <div class="sign">
        Người báo cáo
      </div>
    </div>
  </div>
</div>

</body>
</html>
