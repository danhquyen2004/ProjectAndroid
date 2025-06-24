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
            <input type="text" name="email" placeholder="Tên đăng nhập">
            <input type="password" name="password" placeholder="Mật khẩu">
            <button type="submit">Đăng nhập</button>
        </form>
        
        {{-- SCRIPT ĐÃ ĐƯỢC CẬP NHẬT --}}
        <script>
        document.getElementById('loginForm').onsubmit = async function(e) {
            e.preventDefault();
            const email = this.email.value.trim();
            const password = this.password.value.trim();

            if (!email || !password) {
                // Không làm gì nếu trường rỗng
                return;
            }

            try {
                const res = await fetch('https://us-central1-tlu-pickleball-459716.cloudfunctions.net/api/auth/login', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({email, password})
                });

                const data = await res.json().catch(() => null);

                // Nếu request thành công (mã 2xx) VÀ server báo success = true
                if (res.ok && data?.success) {
                    // HIỂN THỊ THÔNG BÁO THÀNH CÔNG
                    alert('Đăng nhập thành công!'); 
                    
                    localStorage.setItem('token', data.token);
                    window.location.href = '/dashboard';
                }
                // Không hiển thị thông báo nếu đăng nhập thất bại

            } catch (error) {
                // Không hiển thị thông báo nếu có lỗi kết nối
                console.error('Lỗi kết nối:', error);
            }
        };
        </script>
        
    </div>
</body>
</html>