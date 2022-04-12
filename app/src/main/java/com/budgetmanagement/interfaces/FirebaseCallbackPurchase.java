package com.budgetmanagement.interfaces;

import com.budgetmanagement.entity.Account;

import java.util.ArrayList;

public interface FirebaseCallbackPurchase {
    void onCallback(ArrayList<Account.Purchase> arrayList);
}
