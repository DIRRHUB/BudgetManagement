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
                case 0://name a-z
                    arrayList.sort((t1, t2) -> t1.name.compareTo(t2.name));
                    break;
                case 1://name z-a
                    arrayList.sort((t1, t2) -> t2.name.compareTo(t1.name));
                    break;
                case 2://category a-z
                    arrayList.sort((t1, t2) -> t1.category.compareTo(t2.category));
                    break;
                case 3://category z-a
                    arrayList.sort((t1, t2) -> t2.category.compareTo(t1.category));
                    break;
                case 4://date 0-1
                    arrayList.sort((t1, t2) -> t1.date.compareTo(t2.date));
                    break;
                case 5://date 1-0
                    arrayList.sort((t1, t2) -> t2.date.compareTo(t1.date));
                    break;
                case 6://price 0-1
                    break;
                case 7://price 1-0
                    break;
            }
        }
        return this;
    }

    private void convertCurrencyToUSD() {

    }
}
