<?php

// app/Http/Controllers/MemberController.php
namespace App\Http\Controllers;

use Kreait\Laravel\Firebase\Facades\Firebase;

class MemberController extends Controller
{
    public function index()
    {
        $firestore = Firebase::firestore();
        $database = $firestore->database();
        $memberDocuments = $database->collection('members')->documents();

        $members = [];
        foreach ($memberDocuments as $document) {
            if ($document->exists()) {
                $members[] = $document->data();
            }
        }

        // Truyền biến $members cho view
        return view('admin.member_manager', compact('members'));
    }
}