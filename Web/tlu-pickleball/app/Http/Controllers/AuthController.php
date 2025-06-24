<?php
namespace App\Http\Controllers;
use Illuminate\Http\Request;

class AuthController extends Controller
{
    public function showLoginForm()
    {
        return view('login.login'); // Trả về file resources/views/login.blade.php
    }
}