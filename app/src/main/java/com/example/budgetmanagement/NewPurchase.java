package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetmanagement.databinding.FragmentNewPurchaseBinding;

public class NewPurchase extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private FragmentNewPurchaseBinding binding;
    private String name, category;
    private double price;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent();
        databaseContent.init();
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
        switch (view.getId()) {
            case R.id.addPurchase:
                if (!TextUtils.isEmpty(binding.editName.getText().toString()) && !TextUtils.isEmpty(binding.editPrice.getText().toString())) {
                name = binding.editName.getText().toString();
                category = binding.spinnerEditCategory.getSelectedItem().toString();
                price = Double.parseDouble(binding.editPrice.getText().toString());
                addPurchase();
                }
        }
    }
    private void addPurchase() {
        Account.Purchase purchase = new Account.Purchase();
        purchase.addPurchase(name, category, databaseContent.getPurchaseID(),price);
        databaseContent.saveToDatabase(purchase);
    }
}