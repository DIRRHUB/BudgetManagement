package com.example.budgetmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetmanagement.databinding.FragmentPurchasesListBinding;

import java.util.ArrayList;

public class PurchasesListFragment extends Fragment {
    private FragmentPurchasesListBinding binding;
    private DatabaseContent databaseContent;
    private Account.Purchase purchase;
    private ArrayList<Account.Purchase> purchasesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseContent = new DatabaseContent().init();
        purchase = new Account.Purchase();
        purchasesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  FragmentPurchasesListBinding.inflate(inflater, container, false);
        loadPurchasesToArrayList();
        return binding.getRoot();
    }

    private void loadPurchasesToArrayList() {
        databaseContent.loadPurchaseFromDatabase(unsortedArrayList -> {
            purchasesList.addAll(unsortedArrayList);
            sortPurchases();
        });
    }

    private void sortPurchases(){
        //get context of spinner to sort by args
        ListAdapter customAdapter = new ListAdapter(this.getContext(), purchasesList);
        binding.listview.setAdapter(customAdapter);
        binding.listview.setClickable(true);
    }

}