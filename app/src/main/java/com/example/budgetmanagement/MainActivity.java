package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetmanagement.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
        binding.navigationView.setNavigationItemSelectedListener(this);
        account = new Account();
        purchase = new Account.Purchase();
        databaseContent = new DatabaseContent().init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        loadAccount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //databaseContent.saveToDatabase(account);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SpecialFunction.hideKeyboard(binding.navigationView);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.new_purchase:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), new NewPurchaseFragment()).commit();
                return true;
            case R.id.settings:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), new SettingsFragment()).commit();
                return true;
            case R.id.list_purchases:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), new PurchasesListFragment()).commit();
                return true;
            case R.id.save:
                saveAccount();
                return true;
            case R.id.load:
                loadAccount();
                purchasesToArrayList();
                binding.textView3.setText(account.toString());
                return true;
            case R.id.random:
                onClickRandom();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void purchasesToArrayList() {
        ArrayList<Account.Purchase> purchasesList = new ArrayList<>();
        databaseContent.loadPurchaseFromDatabase(arrayList -> {
            purchasesList.addAll(arrayList);
            for(int i=0; i<purchasesList.size(); i++){
                purchase = purchasesList.get(i);
            }
        });
    }

    public void onClickSignOut(MenuItem item) {
        databaseContent.signOut();
        LoginActivity.updateUILoggedOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClickSave(MenuItem item) {
        saveAccount();
    }

    private void loadAccount() {
        if(SpecialFunction.isNetworkAvailable()) {
            databaseContent.loadAccountFromDatabase(account -> {
                this.account = account;
                binding.textView3.setText(account.toString());
            });
        } else {
            startActivity(new Intent(this, InternetTroubleActivity.class));
        }
    }

    public void onClickErasePurchase(MenuItem item) {
        // databaseContent.erasePurchaseFromDatabase(binding.purchaseID.getText().toString());
    }

    public void onClickRandom() {
        setAccount();
    }

    private void saveAccount() {
        databaseContent.saveToDatabase(account);
    }

    protected void setAccount() {
        account.setEmail(databaseContent.getEmail());
        account.setCurrencyType("USD");
        account.setId(databaseContent.getUID());
        account.setBudget(0);
        account.setBudgetLeft(0);
        account.setBudgetLeft(0);
    }

    @Override
    public void onBackPressed() {
    }
}