package com.example.budgetmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import com.example.budgetmanagement.databinding.ActivityMainBinding;

import com.google.android.material.navigation.NavigationView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ActivityMainBinding binding;
    private DatabaseContent databaseContent;
    private Account account;
    private Account.Purchase purchase;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.setNavigationItemSelectedListener(this);
        account = new Account();
        purchase = new Account.Purchase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        databaseContent = new DatabaseContent();
        databaseContent.init();
        loadAccount();
    }
    @Override
    protected void onPause() {
        super.onPause();
        databaseContent.saveToDatabase(account);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.new_purchase:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), new NewPurchase()).commit();
                return true;
            case R.id.settings:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), new Settings()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickSignOut(MenuItem item) {
        databaseContent.signOut();
        LoginActivity.updateUILoggedOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void onClickSave(MenuItem item){
        saveAccount();
    }

    private void loadAccount(){
        if(databaseContent.loadAccountFromDatabase()==null) {
            setDefaultAccount();
        } else {
            account = databaseContent.loadAccountFromDatabase();
            purchase = databaseContent.loadPurchaseFromDatabase();
        }
    }
    public void onClickErasePurchase(MenuItem item) {
       // databaseContent.erasePurchaseFromDatabase(binding.purchaseID.getText().toString());
    }
    public void onClickRandom(MenuItem item) {
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