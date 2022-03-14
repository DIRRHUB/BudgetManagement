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
import java.util.Map;
import java.util.Objects;

public class DatabaseContent {
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private Account account;
    private Account.Purchase purchase;
    private String lastPurchaseID;
    private final String USER_KEY = "Account", PURCHASES = "purchases";

    public DatabaseContent init() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference(String.format("%s/%s", USER_KEY, mAuth.getUid()));
        return this;
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

    public void register(String email, String password, UpdateUILoginCallback updateUILoginCallback) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("register", "Successful");
                updateUILoginCallback.updateUILoggedIn();
            } else {
                Log.e("register", "Error:  " + Objects.requireNonNull(task.getException()).toString());
            }
        });
    }

    public void login(String email, String password, UpdateUILoginCallback updateUILoginCallback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                Log.d("login", "Successful");
                updateUILoginCallback.updateUILoggedIn();
            } else {
                Log.e("login", Objects.requireNonNull(task.getException()).toString());
            }
        }));
    }

    public void signOut() {
        mAuth.signOut();
    }

    public void loadAccountFromDatabase(FirebaseCallbackAccount accountFirebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    account = snapshot.getValue(Account.class);
                } else {
                    account = new Account();
                    setDefaultAccount();
                }
                accountFirebaseCallback.onCallback(account);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadAccountFromDatabase", "Error:  " + error.toString());
            }
        };
        database.addValueEventListener(valueEventListener);
    }

    public void loadPurchaseFromDatabase(FirebaseCallbackPurchase callbackPurchase) {
        database.child(PURCHASES).get().addOnCompleteListener(task -> {
            if(task.isComplete()){
                ArrayList<Account.Purchase> purchaseArrayList = new ArrayList<>();
                for(DataSnapshot purchaseItem : Objects.requireNonNull(task.getResult()).getChildren()){
                    purchase = purchaseItem.getValue(Account.Purchase.class);
                    purchaseArrayList.add(purchase);
                }
                callbackPurchase.onCallback(purchaseArrayList);
            }
        });
    }

    public void erasePurchaseFromDatabase(String purchaseID) {
        database.child(PURCHASES).child(purchaseID).removeValue();
    }

    protected void setDefaultAccount() {
        account.setEmail(getEmail());
        account.setCurrencyType("USD");
        account.setId(getUID());
        account.setPersonName("новый пользователь");
        account.setBudget(100);
        account.setBudgetLastMonth(0);
        account.setBudgetLeft(100);
    }

    public void saveToDatabase(@NonNull Account account) {
        Map<String, Object> accountMap = account.toMap();
        database.updateChildren(accountMap);
    }

    public void saveToDatabase(@NonNull Account.Purchase purchase) {
        database.child(PURCHASES).child(lastPurchaseID).setValue(purchase);
    }

    public void saveToDatabase(@NonNull Account.Purchase purchase, String purchaseID){
        database.child(PURCHASES).child(purchaseID).setValue(purchase);
    }

    public String getPurchaseID() {
        lastPurchaseID = database.child(PURCHASES).push().getKey();
        return lastPurchaseID;
    }

    public String getUID() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    public String getEmail() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }


}

