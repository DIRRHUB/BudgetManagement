package com.example.budgetmanagement;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetmanagement.databinding.FragmentPurchasesListBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class PurchasesListFragment extends Fragment {
    private FragmentPurchasesListBinding binding;
    private DatabaseContent databaseContent;
    private SortPurchasesContent sortPurchasesContent;
    private ArrayList<Account.Purchase> purchasesList;
    private Account.Purchase purchase;
    private RecyclerAdapter recyclerAdapter;

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
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(250);
                loadPurchasesToArrayList();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        return binding.getRoot();
    }

    private void loadPurchasesToArrayList() {
        databaseContent.loadPurchaseFromDatabase(unsortedArrayList -> {
            purchasesList = new ArrayList<>(unsortedArrayList);
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
        recyclerAdapter = new RecyclerAdapter(this.getContext(), purchasesList);
        binding.recyclerView.setAdapter(recyclerAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAbsoluteAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                purchase = (Account.Purchase) purchasesList.get(position);
                purchasesList.remove(position);
                recyclerAdapter.notifyItemRemoved(position);
                databaseContent.erasePurchaseFromDatabase(purchase.getPurchaseID());
                Snackbar.make(binding.recyclerView, purchase.getName(), Snackbar.LENGTH_LONG).setAction(R.string.cancel, view -> {
                    purchasesList.add(position, purchase);
                    recyclerAdapter.notifyItemInserted(position);
                    databaseContent.saveToDatabase(purchase, purchase.getPurchaseID());
                }).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(requireContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.delete_icon)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}