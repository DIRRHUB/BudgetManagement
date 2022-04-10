package com.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budgetmanagement.databinding.FragmentHomeBinding;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseContent databaseContent;
    private Account account;
    private PieChartFragment pieChartFragment;
    private BarChartFragment barChartFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseContent = new DatabaseContent();
        pieChartFragment = new PieChartFragment();
        barChartFragment = new BarChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        databaseContent.loadAccountFromDatabase(account -> {
            this.account = account;
            setBudgetInfo();
        });
        binding.buttonPie.setOnClickListener(listener);
        binding.buttonBar.setOnClickListener(listener);
        return binding.getRoot();
    }

    @SuppressLint("ResourceAsColor")
    private void setBudgetInfo(){
        int budget = (int) account.getBudget();
        int leftBudget = (int) (budget - account.getBudgetLeft());
        int x = (int) (100 * leftBudget / budget);
        binding.textBudget.setText(leftBudget + "/" + budget + " (" + x + "%)");
        new Thread(() -> {
            if(x<=50){
                binding.progressBar.getProgressDrawable().setColorFilter(requireActivity().getResources().
                        getColor(R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if(x>50 && x<=75){
                binding.progressBar.getProgressDrawable().setColorFilter(requireActivity().getResources().
                        getColor(R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.progressBar.getProgressDrawable().setColorFilter(requireActivity().getResources().
                        getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }).start();
        binding.progressBar.setProgress((int) x);
    }

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener listener = view -> {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        switch (view.getId()){
            case R.id.buttonPie:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), pieChartFragment).commit();
                break;
            case R.id.buttonBar:
                fragmentTransaction.replace(binding.fragmentContainerView.getId(), barChartFragment).commit();
                break;
        }
    };
}