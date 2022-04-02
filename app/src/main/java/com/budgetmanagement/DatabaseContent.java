package com.budgetmanagement;

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
    private final FirebaseAuth auth;
    private final DatabaseReference database;
    private final String USER_KEY = "Account", PURCHASES = "purchases";
    private FirebaseUser user;
    private Account account;
    private Account.Purchase purchase;
    private String lastPurchaseID;

    DatabaseContent() {
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        database = firebaseDatabase.getReference(String.format("%s/%s", USER_KEY, auth.getUid()));
    }

    public boolean checkAuth() {
        user = auth.getCurrentUser();
        if (user == null) {
            Log.d("checkAuth", "NULL");
            return false;
        } else {
            Log.d("checkAuth", "NOT NULL");
            return true;
        }
    }

    public boolean checkVerification() {
        user = auth.getCurrentUser();
        Objects.requireNonNull(user).reload();
        if (user != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return user.isEmailVerified();
        } else {
            return false;
        }
    }

    public void register(String email, String password, UpdateUILoginCallback updateUILoginCallback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Register", "Successful");
                Log.d("Register", "Need verification");
                user = auth.getCurrentUser();
                Objects.requireNonNull(user).sendEmailVerification().addOnCompleteListener(verificationTask -> {
                    if (verificationTask.isSuccessful()) {
                        Log.d("Register", "Email verification was sent.");
                    }
                });
                updateUILoginCallback.updateUILoggedIn(false);
            } else {
                Log.e("Register", "Error:  " + Objects.requireNonNull(task.getException()));
            }
        });
    }

    public void tryLogin(String email, String password, UpdateUILoginCallback updateUILoginCallback, KeyboardCallback keyboard) {
        register(email, password, updateUILoginCallback);
        if (!auth.isSignInWithEmailLink(email)) {
            login(email, password, updateUILoginCallback);
            keyboard.hide();
        }
    }

    public void login(String email, String password, UpdateUILoginCallback updateUILoginCallback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                if (checkVerification()) {
                    Log.d("Login", "Successful");
                    updateUILoginCallback.updateUILoggedIn(true);
                } else {
                    Log.d("Login", "Need verification");
                    updateUILoginCallback.updateUILoggedIn(false);
                }
            } else {
                Log.e("Login", Objects.requireNonNull(task.getException()).toString());
            }
        }));
    }

    public void signOut() {
        auth.signOut();
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
                Log.e("loadAccountFromDatabase", error.toString());
            }
        };
        database.addValueEventListener(valueEventListener);
    }

    public void loadPurchaseFromDatabase(FirebaseCallbackPurchase callbackPurchase) {
        database.child(PURCHASES).get().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                ArrayList<Account.Purchase> purchaseArrayList = new ArrayList<>();
                for (DataSnapshot purchaseItem : Objects.requireNonNull(task.getResult()).getChildren()) {
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

    public void saveToDatabase(@NonNull Account.Purchase purchase, String purchaseID) {
        database.child(PURCHASES).child(purchaseID).setValue(purchase);
    }

    public String getPurchaseID() {
        lastPurchaseID = database.child(PURCHASES).push().getKey();
        return lastPurchaseID;
    }

    public String getUID() {
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    public String getEmail() {
        return Objects.requireNonNull(auth.getCurrentUser()).getEmail();
    }
}

