package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Account {
    public double budget, budgetLeft, budgetLastMonth;
    public String id, email, personName, currencyType;

    public Account() {
    }

    public Account(double budget, double budgetLeft, double budgetLastMonth, String id, String email, String personName, String currencyType) {
        this.budget = budget;
        this.budgetLeft = budgetLeft;
        this.budgetLastMonth = budgetLastMonth;
        this.id = id;
        this.email = email;
        this.personName = personName;
        this.currencyType = currencyType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "budget=" + budget +
                ", budgetLeft=" + budgetLeft +
                ", budgetLastMonth=" + budgetLastMonth +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", personName='" + personName + '\'' +
                ", currencyType='" + currencyType + '\'' +
                '}';
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setBudgetLeft(int budgetLeft) {
        this.budgetLeft = budgetLeft;
    }

    public void setBudgetLastMonth(int budgetLastMonth) {
        this.budgetLastMonth = budgetLastMonth;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("budget", budget);
        result.put("budgetLeft", budgetLeft);
        result.put("budgetLastMonth", budgetLastMonth);
        result.put("id", id);
        result.put("email", email);
        result.put("personName", personName);
        result.put("currencyType", currencyType);

        return result;
    }

    static class Purchase {
        public String name, category, date, currency, purchaseID;
        public double price;

        @SuppressLint("SimpleDateFormat")
        public void addPurchase(String name, String category, String currency, String purchaseID, double price) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.purchaseID = purchaseID;
            this.currency = currency;
            date = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").format(new Date());
        }

    }
}
