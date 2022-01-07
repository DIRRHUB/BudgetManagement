package com.example.budgetmanagement;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseContent{
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database;
    private String email;
    private final String USER_KEY = "Account";


    public void init() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference(USER_KEY);
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
                /*Account account = new Account();
                account = loadFromDatabase();
                LoginActivity.setName(database);*/
                LoginActivity.updateUILoggedIn();
            }
        }));
    }


    public void signOut() {
        mAuth.signOut();
    }

    public List loadFromDatabase(){
        List list = new ArrayList();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if(list.size()>0) list.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Account account = ds.getValue(Account.class);
                    list.add(account);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.addValueEventListener(valueEventListener);
        return list;
    }
    public void saveToDatabase(Account account){
        database.setValue(account);
    }
    public String getUID(){
        return mAuth.getUid();
    }

}

