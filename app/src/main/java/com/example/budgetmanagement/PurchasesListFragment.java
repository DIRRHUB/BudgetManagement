package com.example.budgetmanagement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.budgetmanagement.databinding.FragmentPurchasesListBinding;

import java.util.ArrayList;

public class PurchasesListFragment extends Fragment {
    private FragmentPurchasesListBinding binding;
    private DatabaseContent databaseContent;
    private SortPurchasesContent sortPurchasesContent;
    private ArrayList purchasesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseContent = new DatabaseContent().init();
        purchasesList = new ArrayList<>();
        sortPurchasesContent = new SortPurchasesContent().tryGetExchangeRates();
        ((DrawerLocker) requireActivity()).setDrawerClosed(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPurchasesListBinding.inflate(inflater, container, false);
        loadPurchasesToArrayList();
        binding.sortTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                trySort(purchasesList, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return binding.getRoot();
    }

    private void loadPurchasesToArrayList() {
        databaseContent.loadPurchaseFromDatabase(unsortedArrayList -> {
            purchasesList = new ArrayList<Account.Purchase>(unsortedArrayList);
            int sortType = binding.sortTypeSpinner.getSelectedItemPosition();
            trySort(unsortedArrayList, sortType);
        });
    }

    private void trySort(ArrayList<Account.Purchase> unsortedArrayList, int sortType) {
        if(SpecialFunction.isNetworkAvailable()) {
            purchasesList = sortPurchasesContent.setArrayList(unsortedArrayList).sort(sortType).getArrayList();
            setAdapter();
        } else {
            startActivity(new Intent(this.getActivity(), InternetTroubleActivity.class));
        }
    }

    private void setAdapter() {
        ListAdapter customAdapter = new ListAdapter(this.getContext(), purchasesList);
        binding.listview.setAdapter(customAdapter);
        binding.listview.setClickable(true);
    }
}