package com.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budgetmanagement.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PieChartFragment pieChartFragment;
    private BarChartFragment barChartFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pieChartFragment = new PieChartFragment();
        barChartFragment = new BarChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        binding.buttonPie.setOnClickListener(listener);
        binding.buttonBar.setOnClickListener(listener);
        return binding.getRoot();
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