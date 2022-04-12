package com.budgetmanagement.entity;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Account {
    private double budget, budgetLeft, budgetLastMonth;
    private String id, email, personName, currencyType;

    @NonNull
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

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getBudgetLeft() {
        return budgetLeft;
    }

    public void setBudgetLeft(double budgetLeft) {
        this.budgetLeft = budgetLeft;
    }

    public double getBudgetLastMonth() {
        return budgetLastMonth;
    }

    public void setBudgetLastMonth(double budgetLastMonth) {
        this.budgetLastMonth = budgetLastMonth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public static class Purchase {
        private String name, category, date, currency, purchaseID;
        private double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public String getDate() {
            return date;
        }

        public String getCurrency() {
            return currency;
        }

        public String getPurchaseID() {
            return purchaseID;
        }

        public double getPrice() {
            return price;
        }

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
