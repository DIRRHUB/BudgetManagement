package com.example.budgetmanagement;

public class Account {
   // private int personFamily, budget, budgetLeft, budgetLastMonth;
    public String id, personEmail, personName, currencyType;

    Account(){
    }

    public Account(String id, String personEmail, String personName, String currencyType) {
        this.id = id;
        this.personEmail = personEmail;
        this.personName = personName;
        this.currencyType = currencyType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", personName='" + personName + '\'' +
                ", currencyType='" + currencyType + '\'' +
                '}';
    }
}
