package com.example.budgetmanagement;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.budgetmanagement.databinding.FragmentPieChartBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;

public class PieChartFragment extends Fragment {
    private FragmentPieChartBinding binding;
    private ChartManager chartManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPieChartBinding.inflate(getLayoutInflater());
        chartManager = new ChartManager(binding);
        setParameters();
        binding.sortTypeSpinner.setOnItemSelectedListener(spinnerListener);
        return binding.getRoot();
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int time = binding.sortTypeSpinner.getSelectedItemPosition();
            chartManager.getPieData(time, data -> {
                binding.chart.setData(data);
                binding.chart.invalidate();
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
     };

    private void setParameters(){
        binding.chart.setUsePercentValues(true);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setHoleRadius(50f);
        binding.chart.setTransparentCircleRadius(55f);
        binding.chart.setExtraOffsets(5, 5, 5, 5);
        binding.chart.animateY(1000, Easing.EaseInCubic);
        binding.chart.setDragDecelerationFrictionCoef(0.95f);

        binding.chart.setEntryLabelColor(Color.BLACK);
        binding.chart.setEntryLabelTextSize(15f);
        binding.chart.getLegend().setEnabled(true);
        Legend l = binding.chart.getLegend();
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setDrawInside(true);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }
}