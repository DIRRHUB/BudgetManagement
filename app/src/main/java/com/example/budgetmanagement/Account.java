package com.example.budgetmanagement;

public class Account {
    public int budget, budgetLeft, budgetLastMonth;
    public String id, personName, currencyType;


    public Account() {
    }
    public Account(int budget, int budgetLeft, int budgetLastMonth, String id, String personName, String currencyType) {
        this.budget = budget;
        this.budgetLeft = budgetLeft;
        this.budgetLastMonth = budgetLastMonth;
        this.id = id;
        this.personName = personName;
        this.currencyType = currencyType;
    }

    @Override
    public String toString() {
        return "Account{" +
                ", budget=" + budget +
                ", budgetLeft=" + budgetLeft +
                ", budgetLastMonth=" + budgetLastMonth +
                ", id='" + id + '\'' +
                ", personName='" + personName + '\'' +
                ", currencyType='" + currencyType + '\'' +
                '}';
    }
}
