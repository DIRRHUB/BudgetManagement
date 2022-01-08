package com.example.budgetmanagement;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Account {
    public int budget, budgetLeft, budgetLastMonth;
    public String id, email, personName, currencyType;

    public Account() {
    }
    public Account(int budget, int budgetLeft, int budgetLastMonth, String id, String email, String personName, String currencyType) {
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
}
