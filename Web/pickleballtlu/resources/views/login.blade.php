<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập - TLU PickleBall Club</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('style.css') }}">
</head>
<body class="login-body">
    <div class="login-box">
        <img src="{{ asset('pickleball-icon.png') }}" alt="Logo">
        <h2>TLU PickleBall Club</h2>
        <h3>ĐĂNG NHẬP</h3>

        <form id="loginForm">
    <input type="text" name="username" placeholder="Tên đăng nhập" required>
    <input type="password" name="password" placeholder="Mật khẩu" required>
    <button type="submit">Đăng nhập</button>
    </form>
    <div id="loginError" style="color:red;margin-top:10px;"></div>
    <script>
    document.getElementById('loginForm').onsubmit = async function(e) {
        e.preventDefault();
        const username = this.username.value;
        const password = this.password.value;
        const res = await fetch('https://us-central1-tlu-pickleball459716.cloudfunctions.net/api/auth/login', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username, password})
        });
        const data = await res.json();
        if(data.success) {
            // Lưu token vào localStorage (nếu cần)
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard';
        } else {
            document.getElementById('loginError').innerText = data.message || 'Đăng nhập thất bại!';
        }
    };
    </script>
    </div>
</body>
</html>
