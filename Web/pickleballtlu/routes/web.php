<?php

use Illuminate\Support\Facades\Route;

Route::get('/login', function () {
    return view('login');
})->name('login');

Route::get('/dashboard', function () {
    return view('dashboard');
})->name('dashboard');

Route::get('/member_manager', function () {
    return view('member_manager');
})->name('member_manager');

Route::get('/funds_manager', function () {
    return view('funds_manager');
})->name('funds_manager');

Route::get('/report', function () {
    return view('report');
})->name('report');

// Route::post('/login', function () {
//     // xử lý login giả lập
//     return redirect()->route('dashboard');
// });