package com.example.budgetmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetmanagement.databinding.FragmentNewPurchaseBinding;

public class NewPurchase extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private FragmentNewPurchaseBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent();
        databaseContent.init();
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewPurchaseBinding.inflate(getLayoutInflater());
        binding.addPurchase.setOnClickListener(this);
        return inflater.inflate(R.layout.fragment_new_purchase, container, false);
    }


    String name, category;
    double price;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPurchase:
                if (binding.editName.getText().toString() != null && binding.spinnerEditCategory.getSelectedItem().toString() != null && binding.editPrice.getText().toString() != null) {
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