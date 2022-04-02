package com.budgetmanagement;

import java.util.ArrayList;

public interface FirebaseCallbackPurchase {
    void onCallback(ArrayList<Account.Purchase> arrayList);
}
