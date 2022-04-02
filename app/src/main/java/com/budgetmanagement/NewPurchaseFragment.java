package com.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budgetmanagement.databinding.FragmentNewPurchaseBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class NewPurchaseFragment extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private BudgetManager budgetManager;
    private Account account;
    private Account.Purchase purchase;
    private FragmentNewPurchaseBinding binding;
    private String name, category, currency;
    private double price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent();
        databaseContent.loadAccountFromDatabase(account -> {
            this.account = account;
            setCurrency();
        });
        budgetManager = new BudgetManager();
        ((DrawerLocker) requireActivity()).setDrawerClosed(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewPurchaseBinding.inflate(inflater, container, false);
        binding.addPurchase.setOnClickListener(this);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (SpecialFunction.isNetworkAvailable()) {
            if (view.getId() == R.id.addPurchase) {
                if (!TextUtils.isEmpty(binding.editName.getText().toString()) && !TextUtils.isEmpty(binding.editPrice.getText().toString())) {
                    convertPriceToDouble();
                    if (purchase != null) {
                        if (!binding.editName.getText().toString().equals(purchase.getName()) ||
                                !binding.spinnerEditCategory.getSelectedItem().toString().equals(purchase.getCategory()) ||
                                !binding.spinnerEditSpecialCurrency.getSelectedItem().toString().equals(purchase.getCurrency()) ||
                                price != purchase.getPrice()) {
                            createPurchase();
                        } else {
                            Snackbar.make(binding.getRoot(), purchase.getName(), Snackbar.LENGTH_LONG)
                                    .setAction(R.string.confirm_same_purchase, viewConfirm -> createPurchase()).show();
                        }
                    } else {
                        createPurchase();
                    }
                }
            }
        } else {
            startActivity(new Intent(this.getActivity(), InternetTroubleActivity.class));
        }
    }

    private void convertPriceToDouble() {
        try {
            price = Double.parseDouble(binding.editPrice.getText().toString().replace(",", "."));
        } catch (NumberFormatException e) {
            Snackbar.make(binding.getRoot(), getString(R.string.wrong_price_format), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void createPurchase() {
        name = binding.editName.getText().toString();
        if (name.length() > 25) {
            Snackbar.make(binding.getRoot(), getString(R.string.name_size_err), Snackbar.LENGTH_SHORT).show();
            return;
        }
        category = binding.spinnerEditCategory.getSelectedItem().toString();
        currency = binding.spinnerEditSpecialCurrency.getSelectedItem().toString();
        purchase = new Account.Purchase();
        if (!currency.equals(account.getCurrencyType())) {
            double convertedPrice = budgetManager.convertToSetCurrency(account.getCurrencyType(), currency, price);
            decreaseBudget(convertedPrice);
            purchase.addPurchase(name, category, currency, databaseContent.getPurchaseID(), price);
        } else {
            decreaseBudget(price);
            purchase.addPurchase(name, category, account.getCurrencyType(), databaseContent.getPurchaseID(), price);
        }
        databaseContent.saveToDatabase(account);
        databaseContent.saveToDatabase(purchase);
        Snackbar.make(binding.getRoot(), getString(R.string.purchase_successful), Snackbar.LENGTH_SHORT).show();
    }

    private void decreaseBudget(double price) {
        account.setBudgetLeft(account.getBudgetLeft() - price);
    }

    private void setCurrency() {
        if (purchase == null) {
            String[] currencyArray = {"USD", "EUR", "UAH", "RUB"};
            int position = Arrays.asList(currencyArray).indexOf(account.getCurrencyType());
            binding.spinnerEditSpecialCurrency.setSelection(position);
        }
    }
}