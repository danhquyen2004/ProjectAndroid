<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\MemberController;
use App\Http\Controllers\FundController;
use App\Http\Controllers\ReportController;

// Route cho trang đăng nhập
Route::get('/login', [AuthController::class, 'showLoginForm'])->name('login');
Route::POST('/login', [AuthController::class, 'showLoginForm'])->name('login');

// Nhóm các route cần đăng nhập (sẽ thêm middleware sau)
Route::middleware('auth.firebase')->group(function () {
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');
    Route::get('/members', [MemberController::class, 'index'])->name('members.index');
    Route::get('/funds', [FundController::class, 'index'])->name('funds.index');
    Route::get('/reports', [ReportController::class, 'index'])->name('reports.index');
});

// Route mặc định, chuyển hướng đến trang đăng nhập hoặc dashboard
Route::get('/', function () {
    // Tạm thời chuyển về login
    return redirect()->route('login');
});