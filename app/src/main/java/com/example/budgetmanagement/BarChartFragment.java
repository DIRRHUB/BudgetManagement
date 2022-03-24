package com.example.budgetmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.budgetmanagement.databinding.FragmentBarChartBinding;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class BarChartFragment extends Fragment {
    private FragmentBarChartBinding binding;
    private ChartManager chartManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBarChartBinding.inflate(getLayoutInflater());
        chartManager = new ChartManager(binding);
        setParameters();
        binding.sortTypeSpinner.setOnItemSelectedListener(spinnerListener);
        return binding.getRoot();
    }

    private final AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int type = binding.sortTypeSpinner.getSelectedItemPosition();
            int time = 0; //need to implement listener for other spinner which gets time
            chartManager.getBarData(time, type, data -> {
                binding.chart.setData(data);
                binding.chart.invalidate();
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void setParameters(){
        binding.chart.setDrawBarShadow(false);
        binding.chart.setDrawValueAboveBar(true);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setMaxVisibleValueCount(100);
        binding.chart.setPinchZoom(false);
        binding.chart.setDrawGridBackground(false);

        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        YAxis leftAxis = binding.chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        Legend l = binding.chart.getLegend();
        l.setEnabled(false);
    }
}