package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.budgetmanagement.databinding.FragmentNewPurchaseBinding;

public class NewPurchaseFragment extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private Account account;
    private Account.Purchase purchase;
    private FragmentNewPurchaseBinding binding;
    private String name, category;
    private double price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent().init();
        databaseContent.loadAccountFromDatabase(account -> {
            this.account = account;
        });

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
        if(SpecialFunction.isNetworkAvailable()) {
            if (view.getId() == R.id.addPurchase) {
                if (!TextUtils.isEmpty(binding.editName.getText().toString()) && !TextUtils.isEmpty(binding.editPrice.getText().toString())) {
                    name = binding.editName.getText().toString();
                    if (name.length() > 25) {
                        Toast.makeText(getActivity().getApplicationContext(), "Слишком длинное имя", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    category = binding.spinnerEditCategory.getSelectedItem().toString();
                    try {
                        price = Double.parseDouble(binding.editPrice.getText().toString().replace(",", "."));
                    } catch (NumberFormatException e){
                        Toast.makeText(getActivity().getApplicationContext(), "Неверный формат цены", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    decreaseBudget(price);
                    addPurchase();
                    databaseContent.saveToDatabase(account);
                    databaseContent.saveToDatabase(purchase);
                    Toast.makeText(getActivity().getApplicationContext(), "Покупка добавлена!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            startActivity(new Intent(this.getActivity(), InternetTroubleActivity.class));
        }
    }

    private void addPurchase() {
        purchase = new Account.Purchase();
        purchase.addPurchase(name, category, account.currencyType, databaseContent.getPurchaseID(), price);
    }

    private void decreaseBudget(double price) {
        account.budgetLeft-=price;
    }
}