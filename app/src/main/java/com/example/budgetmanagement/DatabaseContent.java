package com.example.budgetmanagement;


import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DatabaseContent {
    private FirebaseAuth mAuth;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean checkAuth() {
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser == null) {
            Log.d("checkAuth", "NULL");
            return false;
        } else {
            Log.d("checkAuth", "NOT NULL");
            return true;
        }
    }
    public void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) Log.d("register", "Successful");
        });
    }
    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) Log.d("login", "Successful");
        }));
    }
}
