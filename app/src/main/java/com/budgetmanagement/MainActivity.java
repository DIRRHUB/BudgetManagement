package com.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.budgetmanagement.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DrawerLocker {
    private ActivityMainBinding binding;
    private DatabaseContent databaseContent;
    private Account account;
    private Account.Purchase purchase;
    private ArrayList<Account.Purchase> purchasesList;
    private SortPurchasesContent sortPurchasesContent;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;
    private PurchasesListFragment purchasesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.navigationView.setNavigationItemSelectedListener(listenerNavigation);
        binding.topAppBar.setOnClickListener(listener);
        binding.floatingActionButton.setOnClickListener(listener);
        account = new Account();
        purchase = new Account.Purchase();
        sortPurchasesContent = new SortPurchasesContent();
        databaseContent = new DatabaseContent();
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();
        purchasesListFragment = new PurchasesListFragment();
        fragmentManager = getSupportFragmentManager();
        setHomeFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAccount();
        loadPurchasesToArrayList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("NonConstantResourceId")
    private final NavigationView.OnNavigationItemSelectedListener listenerNavigation = item -> {
        SpecialFunction.hideKeyboard(binding.navigationView);
        if(SpecialFunction.isNetworkAvailable()){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            switch (item.getItemId()) {
                case R.id.home:
                    fragmentTransaction.replace(binding.fragmentContainerView.getId(), homeFragment).commit();
                    binding.floatingActionButton.show();
                    setDrawerClosed(true);
                    return true;
                case R.id.new_purchase:
                    fragmentTransaction.replace(binding.fragmentContainerView.getId(), new NewPurchaseFragment()).commit();
                    binding.floatingActionButton.hide();
                    setDrawerClosed(true);
                    return true;
                case R.id.settings:
                    fragmentTransaction.replace(binding.fragmentContainerView.getId(), settingsFragment).commit();
                    binding.floatingActionButton.hide();
                    setDrawerClosed(true);
                    return true;
                case R.id.list_purchases:
                    fragmentTransaction.replace(binding.fragmentContainerView.getId(), purchasesListFragment).commit();
                    binding.floatingActionButton.hide();
                    setDrawerClosed(true);
                    return true;
                case R.id.signout:
                    signOut();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            startActivity(new Intent(this, InternetTroubleActivity.class));
            return true;
        }
    };

    private final View.OnClickListener listener = view -> {
        if(view.getId()==R.id.topAppBar){
            setDrawerClosed(false);
        } else if(view.getId()==R.id.floatingActionButton){
            binding.floatingActionButton.hide();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(binding.fragmentContainerView.getId(), new NewPurchaseFragment()).commit();
        }
    };

    private void loadPurchasesToArrayList() {
        databaseContent.loadPurchaseFromDatabase(unsortedArrayList -> {
            purchasesList = new ArrayList<>(unsortedArrayList);
            final int SORT_TYPE = 4;
            purchasesList = sortPurchasesContent.setArrayList(unsortedArrayList).sort(SORT_TYPE).getArrayList();
            if (purchasesList.size() != 0) {
                purchase = purchasesList.get(0);
                updateBudgetLastMonth();
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void updateBudgetLastMonth() {
        if (purchase != null) {
            int lastMonth = 0;
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            Calendar lastMonthCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
                Date date = sdf.parse(purchase.getDate());
                lastMonthCalendar.setTime(Objects.requireNonNull(date));
                lastMonth = lastMonthCalendar.get(Calendar.MONTH) + 1;
                Log.i("CurrentMonth", String.valueOf(currentMonth));
                Log.i("LastMonth", String.valueOf(lastMonth));
            } catch (ParseException e) {
                Log.e("updateBudgetLastMonth", e.toString());
            }
            if (account != null && lastMonth != currentMonth) {
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
        if (SpecialFunction.isNetworkAvailable()) {
            databaseContent.loadAccountFromDatabase(account -> this.account = account);
        } else {
            startActivity(new Intent(this, InternetTroubleActivity.class));
        }
    }

    private void saveAccount() {
        databaseContent.saveToDatabase(account);
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            setDrawerClosed(true);
        } else {
            binding.floatingActionButton.show();
            setHomeFragment();
        }
    }

    private void setHomeFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(binding.fragmentContainerView.getId(), homeFragment).commit();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void setDrawerClosed(boolean closed) {
        if (closed) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            binding.drawerLayout.openDrawer(Gravity.LEFT);
        }
    }
}