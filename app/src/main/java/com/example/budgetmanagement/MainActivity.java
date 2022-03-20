package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetmanagement.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLocker {
    private ActivityMainBinding binding;
    private DatabaseContent databaseContent;
    private Account account;
    private Account.Purchase purchase;
    private ArrayList<Account.Purchase> purchasesList;
    private SortPurchasesContent sortPurchasesContent;
    private FragmentTransaction fragmentTransaction;
    private PieChartFragment pieChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.navigationView.setNavigationItemSelectedListener(this);
        account = new Account();
        purchase = new Account.Purchase();
        sortPurchasesContent = new SortPurchasesContent();
        databaseContent = new DatabaseContent();
        pieChartFragment = new PieChartFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        loadAccount();
        loadPurchasesToArrayList();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentContainerView.getId(), pieChartFragment).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            case R.id.signout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadPurchasesToArrayList() {
        databaseContent.loadPurchaseFromDatabase(unsortedArrayList -> {
            purchasesList = new ArrayList<>(unsortedArrayList);
            final int SORT_TYPE = 5;
            purchasesList = sortPurchasesContent.setArrayList(unsortedArrayList).sort(SORT_TYPE).getArrayList();
            if(purchasesList.size()!=0) {
                purchase = (Account.Purchase) purchasesList.get(0);
                updateBudgetLastMonth();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void updateBudgetLastMonth(){
        if(purchase!=null){
            Log.i("updateBudgetLastMonthPurchaseDate", purchase.getDate());
            int lastMonth = 0;
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
            Calendar lastMonthCalendar = Calendar.getInstance();
            try {
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
                Date date = sdf.parse(purchase.getDate());
                lastMonthCalendar.setTime(Objects.requireNonNull(date));
                lastMonth = lastMonthCalendar.get(Calendar.MONTH)+1;
                Log.i("updateBudgetLastMonthCurrentMonth", String.valueOf(currentMonth));
                Log.i("updateBudgetLastMonthLastMonth", String.valueOf(lastMonth));
            } catch (ParseException e) {
                Log.e("updateBudgetLastMonth", e.toString());
            }

            if(account!=null && lastMonth!=currentMonth){
                account.setBudgetLastMonth(account.getBudgetLeft());
                account.setBudgetLeft(account.getBudget());
                saveAccount();
            }
        }
    }

    public void signOut() {
        databaseContent.signOut();
        startActivity(new Intent(this, LoginActivity.class));
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
        setDrawerClosed(true);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void setDrawerClosed(boolean closed) {
        if(closed){
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
        } else{
            binding.drawerLayout.openDrawer(Gravity.LEFT);
        }
    }
}