package com.example.budgetmanagement;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class DatabaseContent{
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference database;
    private final String USER_KEY = "Account", PURCHASES = "purchases";
    private Account account;
    private Account.Purchase purchase;
    private String lastPurchaseID;

    public void init() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference(String.format("%s/%s", USER_KEY, mAuth.getUid()));
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
            } else {
            Log.e("register", "Error:  " + Objects.requireNonNull(task.getException()).toString());
        }
        });
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                Log.d("login", "Successful");
                LoginActivity.updateUILoggedIn();
            } else {
                Log.e("login", "Error:  " +  Objects.requireNonNull(task.getException()).toString());
            }
        }));

        account = loadAccountFromDatabase();
        purchase = loadPurchaseFromDatabase();
        if(account!=null){
            LoginActivity.setName(account.personName);
        }
    }

    public void signOut() {
        mAuth.signOut();
    }

    public Account loadAccountFromDatabase(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                account = snapshot.getValue(Account.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadAccountFromDatabase", "Error:  " + error.toString());
            }
        };
        database.addValueEventListener(valueEventListener);
        return account;
    }
    public Account.Purchase loadPurchaseFromDatabase(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                purchase = snapshot.getValue(Account.Purchase.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadPurchaseFromDatabase", "Error:  " + error.toString());
            }
        };
        database.addValueEventListener(valueEventListener);
        return purchase;
    }
    public void erasePurchaseFromDatabase(String purchaseID){ // If future you can get purchaseID from Activity.Purchase OBJECT (String PurchaseID)
        database.child(PURCHASES).child(purchaseID).removeValue();
    }

    public void saveToDatabase(@NonNull Account account){
        Map<String, Object> accountMap = account.toMap();
        database.updateChildren(accountMap);
    }

    public void saveToDatabase(@NonNull Account account, Account.Purchase purchase){
        saveToDatabase(account);
        database.child(PURCHASES).child(lastPurchaseID).setValue(purchase);
    }
    public String getPurchaseID() {
        lastPurchaseID = database.child(PURCHASES).push().getKey();
        return lastPurchaseID;
    }
    public String getUID(){
        return mAuth.getCurrentUser().getUid();
    }
    public String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }


}

