<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Báo cáo tổng hợp - TLU Pickleball</title>
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
      <button>DS Hội viên</button>
      <button>Quỹ</button>
      <button class="active">Báo cáo</button>
      <button class="logout-btn" style="margin-top:auto;">Đăng xuất</button>
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
