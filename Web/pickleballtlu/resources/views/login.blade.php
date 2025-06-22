<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập - TLU PickleBall Club</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <style>
        body {
            margin: 0;
            font-family: 'Roboto', sans-serif;
            background: linear-gradient(to top right, #007bff, #cbe7ff);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .login-box {
            background: white;
            padding: 40px 30px;
            border-radius: 12px;
            width: 100%;
            max-width: 400px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .login-box img {
            width: 60px;
            margin-bottom: 10px;
        }
        .login-box h2 {
            margin-bottom: 5px;
            color: #00aaff;
            font-weight: bold;
        }
        .login-box h3 {
            margin-bottom: 20px;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-top: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        button {
            width: 100%;
            padding: 12px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
        }
        button:hover {
            background: #0069d9;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <img src="{{ asset('pickleball-icon.png') }}" alt="Logo">
        <h2>TLU PickleBall Club</h2>
        <h3>ĐĂNG NHẬP</h3>

        <form method="POST" action="{{ route('dashboard') }}">
            @csrf
            <input type="text" name="username" placeholder="Tên đăng nhập" required>
            <input type="password" name="password" placeholder="Mật khẩu" required>
            <button type="submit">Đăng nhập</button>
        </form>
    </div>
</body>
</html>
