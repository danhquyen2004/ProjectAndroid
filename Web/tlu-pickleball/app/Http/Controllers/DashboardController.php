<?php

// app/Http/Controllers/DashboardController.php
namespace App\Http\Controllers;

use Kreait\Laravel\Firebase\Facades\Firebase;

class DashboardController extends Controller
{
    public function index()
    {
        $firestore = Firebase::firestore();
        $database = $firestore->database();

        // Giả sử bạn có collection 'members' và 'funds' trong Firestore
        $memberCount = $database->collection('members')->documents()->size();

        // Tính tổng quỹ (ví dụ, bạn có thể cấu trúc dữ liệu phức tạp hơn)
        $totalFund = 0;
        $fundDocuments = $database->collection('funds')->documents();
        foreach ($fundDocuments as $document) {
            $totalFund += $document->data()['amount'] ?? 0;
        }

        return view('admin.dashboard', [
            'memberCount' => $memberCount,
            'totalFund' => number_format($totalFund) // Định dạng số cho đẹp
        ]);
    }
}