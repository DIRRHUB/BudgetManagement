package com.example.budgetmanagement;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetmanagement.databinding.FragmentPieChartBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class PieChartFragment extends Fragment {
    private FragmentPieChartBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPieChartBinding.inflate(getLayoutInflater());
        setParameters();
        addData();
        return binding.getRoot();
    }

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

    private void addData(){
        List<PieEntry> entries = new ArrayList<>();

        /*for (Data data : dataObjects) {
            entries.add(new PieEntry(100, "test"));
        }*/
        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);
        binding.chart.setData(data);

        binding.chart.invalidate();
    }
}