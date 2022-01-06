package com.example.budgetmanagement;

public class Account {
    public int personFamily, budget, budgetLeft, budgetLastMonth;
    public String id, personEmail, personName, currencyType;

    public Account(int personFamily, int budget, int budgetLeft, int budgetLastMonth, String id, String personEmail, String personName, String currencyType) {
        this.personFamily = personFamily;
        this.budget = budget;
        this.budgetLeft = budgetLeft;
        this.budgetLastMonth = budgetLastMonth;
        this.id = id;
        this.personEmail = personEmail;
        this.personName = personName;
        this.currencyType = currencyType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "personFamily=" + personFamily +
                ", budget=" + budget +
                ", budgetLeft=" + budgetLeft +
                ", budgetLastMonth=" + budgetLastMonth +
                ", id='" + id + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", personName='" + personName + '\'' +
                ", currencyType='" + currencyType + '\'' +
                '}';
    }
}
