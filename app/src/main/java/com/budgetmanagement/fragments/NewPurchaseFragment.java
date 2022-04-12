package com.budgetmanagement.fragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budgetmanagement.R;
import com.budgetmanagement.database.DatabaseContent;
import com.budgetmanagement.databinding.FragmentNewPurchaseBinding;
import com.budgetmanagement.entity.Account;
import com.budgetmanagement.interfaces.DrawerLocker;
import com.budgetmanagement.services.BudgetManager;
import com.google.android.material.snackbar.Snackbar;

public class NewPurchaseFragment extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private BudgetManager budgetManager;
    private Account account;
    private Account.Purchase purchase;
    private FragmentNewPurchaseBinding binding;
    private String name, category, currency;
    private double price;
    private String selectedCategory, selectedCurrency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent();
        budgetManager = new BudgetManager();
        ((DrawerLocker) requireActivity()).setDrawerClosed(true);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewPurchaseBinding.inflate(inflater, container, false);
        binding.addPurchase.setOnClickListener(this);
        databaseContent.loadAccountFromDatabase(account -> {
            this.account = account;
            setCurrency();
            fillSpinners();
        });
        return binding.getRoot();
    }

    private void fillSpinners(){
        Resources r = getResources();
        binding.spinnerEditCategory.setText(r.getText(R.string.shop));
        selectedCategory = r.getString(R.string.shop);
        String[] categoryArray = r.getStringArray(R.array.list_category);
        String[] currencyArray = r.getStringArray(R.array.list_currency);
        ArrayAdapter<String> adapterCategoryMenu = new ArrayAdapter<>(requireContext(), R.layout.list_item_spinner, categoryArray);
        ArrayAdapter<String> adapterCurrencyMenu = new ArrayAdapter<>(requireContext(), R.layout.list_item_spinner, currencyArray);
        binding.spinnerEditCategory.setAdapter(adapterCategoryMenu);
        binding.spinnerEditCategory.setOnItemClickListener(spinnerAdapter);
        binding.spinnerEditSpecialCurrency.setAdapter(adapterCurrencyMenu);
        binding.spinnerEditSpecialCurrency.setOnItemClickListener(spinnerAdapter);
    }

    private final AdapterView.OnItemClickListener spinnerAdapter = (adapterView, view, i, l) -> {
        if(view.getId()==R.id.spinnerEditCategory){
            selectedCategory = adapterView.getSelectedItem().toString();
        } else if (view.getId()==R.id.spinnerEditSpecialCurrency){
            selectedCurrency = adapterView.getSelectedItem().toString();
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addPurchase) {
            if (!TextUtils.isEmpty(binding.editName.getText().toString())
                && !TextUtils.isEmpty(binding.editPrice.getText().toString())) {
                convertPriceToDouble();
                if(price > 0) {
                    if (purchase != null) {
                        if (!binding.editName.getText().toString().equals(purchase.getName()) ||
                                !selectedCategory.equals(purchase.getCategory()) ||
                                !selectedCurrency.equals(purchase.getCurrency()) ||
                                price != purchase.getPrice()) {
                            createPurchase();
                        } else {
                            Snackbar.make(binding.getRoot(), purchase.getName(), Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.primaryColor))
                                    .setAction(R.string.confirm_same_purchase, viewConfirm -> createPurchase()).show();
                        }
                    } else {
                        createPurchase();
                    }
                }
            }
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
        category = selectedCategory;
        currency = selectedCurrency;
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
            binding.spinnerEditSpecialCurrency.setText(account.getCurrencyType());
            selectedCurrency = account.getCurrencyType();
        }
    }
}