package com.example.budgetmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.example.budgetmanagement.databinding.ActivityMainBinding;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseContent databaseContent;
    private Account account;
    private Account.Purchase purchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        account = new Account();
        purchase = new Account.Purchase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseContent = new DatabaseContent();
        databaseContent.init();
        loadAccount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseContent.saveToDatabase(account);
    }

    public void onClickSignOut(View view) {
        databaseContent.signOut();
        LoginActivity.updateUILoggedOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void onClickSave(View view){
        saveAccount();
    }

    public void onClickLoad(View view) {
        loadAccount();
        binding.editList.setText(account.toString());
    }
    private void loadAccount(){
        if(databaseContent.loadAccountFromDatabase()==null) {
            setDefaultAccount();
        } else {
            account = databaseContent.loadAccountFromDatabase();
            purchase = databaseContent.loadPurchaseFromDatabase();
        }
    }
    public void onClickAddPurchase(View view) {
        Account.Purchase purchase = new Account.Purchase();
        purchase.addPurchase(String.valueOf(random.nextInt()), String.valueOf(random.nextInt()), databaseContent.getPurchaseID(),random.nextDouble());
        databaseContent.saveToDatabase(account, purchase);
    }
    public void onClickErasePurchase(View view) {
        databaseContent.erasePurchaseFromDatabase(binding.purchaseID.getText().toString());
    }
    public void onClickRandom(View view) {
        setAccount();
    }

    private void saveAccount(){
        databaseContent.saveToDatabase(account);
    }

    protected void setDefaultAccount(){
        account.setEmail(databaseContent.getEmail());
        account.setCurrencyType("USD");
        account.setId(databaseContent.getUID());
        account.setPersonName(getString(R.string.default_name));
        account.setBudget(100);
        account.setBudgetLastMonth(0);
        account.setBudgetLeft(100);
    }
    Random random = new Random(); //temp Random
    protected void setAccount(){
        account.setEmail(databaseContent.getEmail());
        account.setCurrencyType("USD");
        account.setId(databaseContent.getUID());
        account.setPersonName(getString(R.string.default_name));
        account.setBudget(random.nextInt());
        account.setBudgetLastMonth(random.nextInt());
        account.setBudgetLeft(random.nextInt());
    }
    @Override
    public void onBackPressed() {}


}