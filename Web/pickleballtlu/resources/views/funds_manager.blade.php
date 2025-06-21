<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Quản lý quỹ - TLU Pickleball</title>
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

    /* Main content */
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

    .header .search-bar {
      padding: 8px 12px;
      border: 1px solid #ccc;
      border-radius: 6px;
      width: 200px;
    }

    table {
      margin-top: 20px;
      width: 100%;
      border-collapse: collapse;
      background-color: white;
      border-radius: 8px;
      overflow: hidden;
    }

    th, td {
      padding: 12px;
      border-bottom: 1px solid #eee;
      text-align: center;
    }

    th {
      background-color: #f8f8f8;
    }

    tr:nth-child(even) {
      background-color: #f9f9f9;
    }

    .status-paid {
      color: green;
    }

    .status-unpaid {
      color: red;
    }

    .action-eye {
      cursor: pointer;
      color: #007bff;
      font-size: 16px;
    }
  </style>
</head>
<body>

<div class="container">
  <!-- Sidebar -->
  <div class="sidebar">
    <div class="title">TLU PICKLEBALL CLUB</div>
    <img class="profile" src="https://c.animaapp.com/mbunmuejMSouiM/img/line-1.svg" alt="Admin">
    <div class="admin-name">Admin</div>
    <div class="menu">
      <button>Trang chủ</button>
      <button>Quản lý hội viên</button>
      <button class="active">Quản lý quỹ</button>
      <button>Báo cáo</button>
      <button>Đăng xuất</button>
    </div>
  </div>

  <!-- Main content -->
  <div class="main">
    <div class="header">
      <h2>Quản lý quỹ</h2>
      <input type="text" class="search-bar" placeholder="Search">
    </div>

    <table>
      <thead>
        <tr>
          <th>Họ tên</th>
          <th>Tiền quỹ</th>
          <th>Tiền phạt</th>
          <th>Trạng thái đóng quỹ</th>
          <th>Tiền ủng hộ</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>KarThi</td>
          <td>100.000</td>
          <td>50.000</td>
          <td class="status-paid">Đã đóng</td>
          <td>100.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>KarThi</td>
          <td>100.000</td>
          <td>10.000</td>
          <td class="status-unpaid">Chưa đóng</td>
          <td>10.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>KarThi</td>
          <td>100.000</td>
          <td>20.000</td>
          <td class="status-paid">Đã đóng</td>
          <td>500.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>KarThi</td>
          <td>100.000</td>
          <td>60.000</td>
          <td class="status-unpaid">Chưa đóng</td>
          <td>1.000.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

</body>
</html>
