package com.budgetmanagement.interfaces;

import com.github.mikephil.charting.data.BarData;

import java.util.Map;

public interface BarChartCallback {
    void barChartCallback(Map<Integer, String> labels, BarData data);
}
