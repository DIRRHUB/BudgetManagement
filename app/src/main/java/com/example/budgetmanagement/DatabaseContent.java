package com.example.budgetmanagement;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DatabaseContent{
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database;
    private final String USER_KEY = "Account";
    private Account account;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference(USER_KEY).push();
    }

    public boolean checkAuth() {
        cUser = mAuth.getCurrentUser();
        if (cUser == null) {
            Log.d("checkAuth", "NULL");
            return false;
        } else {

            Log.d("checkAuth", "NOT NULL");
            return true;
        }
    }

    public void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("register", "Successful");
                LoginActivity.updateUILoggedIn();
            }
        });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                Log.d("login", "Successful");
                LoginActivity.updateUILoggedIn();
            }
        }));
        account = loadFromDatabase();
        if(account!=null){
            LoginActivity.setName(account.personName);
        }
    }

    public void signOut() {
        mAuth.signOut();
    }

    public Account loadFromDatabase(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                account = snapshot.getValue(Account.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        database.addValueEventListener(valueEventListener);
        return account;
    }

    public void saveToDatabase(@NonNull Account account){
        Map<String, Object> accountMap = account.toMap();
        database.updateChildren(accountMap);
    }


    public String getUID(){
        return mAuth.getCurrentUser().getUid();
    }
    public String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }
}

