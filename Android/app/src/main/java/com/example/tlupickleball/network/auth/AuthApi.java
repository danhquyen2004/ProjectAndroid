package com.example.tlupickleball.network.auth;

import android.content.Context;

import com.example.tlupickleball.activities.base.ApiCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthApi {

    public static void login(Context context, String email, String password, FirebaseAuth mAuth, ApiCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("Email not verified");
                    }
                    callback.onComplete();
                })
                .addOnFailureListener(e -> {
                        callback.onFailure("Login failed: " + e.getMessage());
                        callback.onComplete();
                    }
                );
    }

    public static void register(Context context, String email, String password, FirebaseAuth mAuth,ApiCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnSuccessListener(v -> {
                            callback.onSuccess();
                        });
                    }
                    callback.onComplete();
                })
                .addOnFailureListener(e -> {
                        callback.onFailure("Register failed: "  + e.getMessage());
                        callback.onComplete();
                    }
                );
    }


}