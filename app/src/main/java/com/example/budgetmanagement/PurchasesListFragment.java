package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.budgetmanagement.databinding.FragmentPurchasesListBinding;

import java.util.ArrayList;

public class PurchasesListFragment extends Fragment {
    private FragmentPurchasesListBinding binding;
    private DatabaseContent databaseContent;
    private SortPurchasesContent sortPurchasesContent;
    private ArrayList purchasesList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseContent = new DatabaseContent().init();
        purchasesList = new ArrayList<>();

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
            Log.i("sortType", String.valueOf(sortType));
            trySort(unsortedArrayList, sortType);
        });
    }

    private void trySort(ArrayList<Account.Purchase> unsortedArrayList, int sortType) {
        if(SpecialFunction.isNetworkAvailable()) {
            if (sortPurchasesContent == null) {
                sortPurchasesContent = new SortPurchasesContent(unsortedArrayList, sortType);
            } else {
                sortPurchasesContent.setSortType(sortType);
            }
            if ((sortType == 6 || sortType == 7) && !sortPurchasesContent.isDownloaded()) {
                sortPurchasesContent.tryGetExchangeRates();
            }
            purchasesList = sortPurchasesContent.setSortType(sortType).sort().getArrayList();
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