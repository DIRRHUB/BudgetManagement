package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class SortPurchasesContent {
    private ArrayList<Account.Purchase> arrayList;
    private BudgetManager budgetManager;
    private double convertedEUR, convertedRUB, convertedUSD;

    public ArrayList<Account.Purchase> getArrayList() {
        return arrayList;
    }

    public SortPurchasesContent setArrayList(ArrayList<Account.Purchase> arrayList) {
        this.arrayList = (ArrayList<Account.Purchase>) arrayList;
        return this;
    }

    public SortPurchasesContent init() {
        budgetManager = new BudgetManager();
        return this;
    }

    @SuppressLint("NonConstantResourceId")
    public SortPurchasesContent sort(int sortType) {
            switch (sortType) {
                case 0://name a-z
                    Collections.sort(arrayList, (t1, t2) -> t1.getName().compareTo(t2.getName()));
                    break;
                case 1://name z-a
                    Collections.sort(arrayList, (t1, t2) -> t2.getName().compareTo(t1.getName()));
                    break;
                case 2://category a-z
                    Collections.sort(arrayList, (t1, t2) -> t1.getCategory().compareTo(t2.getCategory()));
                    break;
                case 3://category z-a
                    Collections.sort(arrayList, (t1, t2) -> t2.getCategory().compareTo(t1.getCategory()));
                    break;
                case 4://date 0-1
                    Collections.sort(arrayList, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
                    break;
                case 5://date 1-0
                    Collections.sort(arrayList, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
                    break;
                case 6://price 0-1
                    sortPrice(true);
                    break;
                case 7://price 1-0
                    sortPrice(false);
                    break;
        }
        return this;
    }

    private void sortPrice(boolean increasingPrice) {
        if (budgetManager.isDownloaded()) {
            try {
                convertedEUR = budgetManager.getConvertedEUR();
                convertedRUB = budgetManager.getConvertedRUB();
                convertedUSD = budgetManager.getConvertedUSD();
                Log.i("SortPurchasesEUR", String.valueOf(convertedEUR));
                Log.i("SortPurchasesRUB", String.valueOf(convertedRUB));
                Log.i("SortPurchasesUSD", String.valueOf(convertedUSD));
            } catch (NullPointerException e) {
                Log.e("SortPurchases", "Something is wrong with map");
                return;
            }
            Collections.sort(arrayList, (purchase1, purchase2) -> {
                double price1, price2;
                switch (purchase1.getCurrency()) {
                    case "UAH":
                        price1 = purchase1.getPrice() / convertedUSD;
                        break;
                    case "RUB":
                        price1 = purchase1.getPrice() * convertedRUB / convertedUSD;
                        break;
                    case "EUR":
                        price1 = purchase1.getPrice() * convertedEUR / convertedUSD;
                        break;
                    default:
                        price1 = purchase1.getPrice();
                        break;
                }
                switch (purchase2.getCurrency()) {
                    case "UAH":
                        price2 = purchase2.getPrice() / convertedUSD;
                        break;
                    case "RUB":
                        price2 = purchase2.getPrice() * convertedRUB / convertedUSD;
                        break;
                    case "EUR":
                        price2 = purchase2.getPrice() * convertedEUR / convertedUSD;
                        break;
                    default:
                        price2 = purchase2.getPrice();
                        break;
                }
                if (increasingPrice) {
                    return Double.compare(price1, price2);
                } else {
                    return Double.compare(price2, price1);
                }
            });
        }
    }
}
