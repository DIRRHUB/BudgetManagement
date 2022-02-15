package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class SortPurchasesContent {
    private ArrayList<Account.Purchase> arrayList;
    private int sortType;

    public SortPurchasesContent(ArrayList arrayList, int sortType) {
        this.arrayList = arrayList;
        this.sortType = sortType;
    }

    public SortPurchasesContent setSortType(int sortType) {
        this.sortType = sortType;
        return this;
    }

    public ArrayList getArrayList() {
        return arrayList;
    }

    @SuppressLint("NonConstantResourceId")
    public SortPurchasesContent sort(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (sortType) {
                case 0:
                    arrayList.sort((t1, t2) -> {
                        if (t1 == t2) {
                            return 0;
                        } else {
                            return t1.name.compareTo(t2.name);
                        }
                    });
                    break;
                case 1:
                    arrayList.sort((t1, t2) -> {
                        if (t1 == t2) {
                            return 0;
                        } else {
                            return t2.name.compareTo(t1.name);
                        }
                    });
                    break;
            }
        }
        return this;
    }
}
