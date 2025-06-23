<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Quản lý quỹ - TLU Pickleball</title>
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
      <button>Quản lý hội viên</button>
      <button class="active">Quản lý quỹ</button>
      <button>Báo cáo</button>
      <button class="logout-btn" style="margin-top:auto;">Đăng xuất</button>
    </div>
  </div>

  <!-- Main content -->
  <div class="main">
    <div class="header">
      <h2>Quản lý quỹ</h2>
      <div class="search-add">
                <input type="text" placeholder="Tìm kiếm...">
            </div>
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
          <td>
            <img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" width="30" height="30" style="border-radius:50%;vertical-align:middle;margin-right:6px;">
            KarThi
          </td>
          <td>100.000</td>
          <td>50.000</td>
          <td class="status-paid">Đã đóng</td>
          <td>100.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>
            <img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" width="30" height="30" style="border-radius:50%;vertical-align:middle;margin-right:6px;">
            KarThi
          </td>
          <td>100.000</td>
          <td>10.000</td>
          <td class="status-unpaid">Chưa đóng</td>
          <td>10.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>
            <img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" width="30" height="30" style="border-radius:50%;vertical-align:middle;margin-right:6px;">
            KarThi
          </td>
          <td>100.000</td>
          <td>20.000</td>
          <td class="status-paid">Đã đóng</td>
          <td>500.000</td>
          <td><span class="action-eye">👁️</span></td>
        </tr>
        <tr>
          <td>
            <img src="https://c.animaapp.com/mbunmuejMSouiM/img/pexels-photo-2379004-1.png" width="30" height="30" style="border-radius:50%;vertical-align:middle;margin-right:6px;">
            KarThi
          </td>
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