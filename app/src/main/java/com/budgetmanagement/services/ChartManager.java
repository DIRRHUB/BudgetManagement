package com.budgetmanagement.services;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.budgetmanagement.entity.Account;
import com.budgetmanagement.database.DatabaseContent;
import com.budgetmanagement.interfaces.PieChartCallback;
import com.budgetmanagement.databinding.FragmentBarChartBinding;
import com.budgetmanagement.databinding.FragmentPieChartBinding;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChartManager {
    private final String SHOP = "Покупки";
    private final String CAFE = "Рестораны";
    private final String ENTERTAINMENT = "Развлечения";
    private final String BILLS = "Счета";
    private final String OTHER = "Другое";
    private final String PATTERN = "yyyy-MM-dd-hh.mm.ss";
    private DatabaseContent databaseContent;
    private BudgetManager budgetManager;
    private List<Account.Purchase> purchases;
    private List<PieEntry> pieEntries;
    private List<BarEntry> barEntries;
    private List<Integer> colors;
    private Account account;
    private Map<String, Float> processedPiePurchasesMap;
    private Map<Integer, BarEntry> processedBarPurchasesMap;
    private Map<Integer, String> labelsXBarMap;
    private int categoryType;

    public ChartManager(FragmentPieChartBinding binding) {
        init();
        databaseContent.loadPurchaseFromDatabase(arrayList -> {
            purchases = new ArrayList<>(arrayList);
            getPieData(0, data -> {
                binding.chart.setData(data);
                binding.chart.invalidate();
            });
        });
    }

    public ChartManager(FragmentBarChartBinding binding) {
        init();
        databaseContent.loadPurchaseFromDatabase(arrayList -> {
            purchases = new ArrayList<>(arrayList);
            getBarData(0, 0, (l, data) -> {
                binding.chart.setData(data);
                labelsXBarMap = l;
                XAxis xAxis = binding.chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(7);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if (labelsXBarMap.containsKey((int) value)) {
                            return labelsXBarMap.get((int) value);
                        }
                        return "";
                    }
                });
                binding.chart.invalidate();
            });
        });
    }

    private void init() {
        databaseContent = new DatabaseContent();
        account = new Account();
        budgetManager = new BudgetManager();
        databaseContent.loadAccountFromDatabase(account -> this.account = account);
    }

    public void getPieData(int time, PieChartCallback callback) {
        processedPiePurchasesMap = new HashMap<>();
        pieEntries = new ArrayList<>();
        try {
            processPurchasesList(time, "pie");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        fillPieEntriesList();
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        if (colors == null) {
            setColorsList();
        }
        dataSet.setColors(colors);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.BLACK);
        callback.pieChartCallback(pieData);
    }

    public void getBarData(int time, int type, BarChartCallback callback) {
        barEntries = new ArrayList<>();
        labelsXBarMap = new HashMap<>();
        processedBarPurchasesMap = new HashMap<>();
        try {
            categoryType = type;
            processPurchasesList(time + 2, "bar");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        fillBarEntriesList();
        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setDrawIcons(false);
        if (colors == null) {
            setColorsList();
        }
        dataSet.setColors(colors);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        callback.barChartCallback(labelsXBarMap, data);
    }

    private void setColorsList() {
        colors = new ArrayList<>();
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
    }

    @SuppressLint("SimpleDateFormat")
    private void processPurchasesList(int time, String chartType) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        Calendar calendarStart = getInstance();
        Calendar calendarEnd = getInstance();
        Calendar calendarNow = getInstance();
        calendarNow.setTime(new Date());
        Date dateStart, dateEnd;

        calendarStart.set(HOUR_OF_DAY, 0);
        calendarStart.set(MINUTE, 0);
        calendarStart.set(SECOND, 0);
        calendarStart.set(MILLISECOND, 0);

        calendarEnd.set(HOUR_OF_DAY, 23);
        calendarEnd.set(MINUTE, 59);
        calendarEnd.set(SECOND, 59);
        calendarEnd.set(MILLISECOND, 59);

        if (purchases != null && budgetManager.isDownloaded()) {
            for (Account.Purchase p : purchases) {
                String dateString = p.getDate();
                Date datePurchase = sdf.parse(dateString);
                switch (time) {
                    case 0://today
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                    case 1://yesterday
                        calendarStart.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 1);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 1);
                        break;
                    case 2://week
                        calendarStart.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 6);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                    case 3://month
                        calendarStart.set(MONTH, calendarNow.get(MONTH) - 1);
                        calendarEnd.set(MONTH, calendarNow.get(MONTH));
                        break;
                    case 4://all time
                        calendarStart.set(YEAR, 0);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                }
                dateStart = calendarStart.getTime();
                dateEnd = calendarEnd.getTime();
                if (datePurchase != null && dateStart.getTime() <= datePurchase.getTime()
                                         && dateEnd.getTime() > datePurchase.getTime()) {
                    if (chartType.equals("pie")) {
                        processPiePurchase(p);
                    } else if (chartType.equals("bar")) {
                        processBarPurchase(p);
                    }
                }
            }
        }
    }

    private void processPiePurchase(Account.Purchase purchase) {
        double price = 0;
        if (budgetManager.isDownloaded()) {
            price = budgetManager.convertToSetCurrency(account.getCurrencyType(), purchase.getCurrency(), purchase.getPrice());
        }
        switch (purchase.getCategory()) {
            case SHOP:
                if (processedPiePurchasesMap.containsKey(SHOP)) {
                    processedPiePurchasesMap.put(SHOP, Objects.requireNonNull(processedPiePurchasesMap.get(SHOP)) + (float) price);
                } else {
                    processedPiePurchasesMap.put(SHOP, (float) price);
                }
                break;
            case CAFE:
                if (processedPiePurchasesMap.containsKey(CAFE)) {
                    processedPiePurchasesMap.put(CAFE, Objects.requireNonNull(processedPiePurchasesMap.get(CAFE)) + (float) price);
                } else {
                    processedPiePurchasesMap.put(CAFE, (float) price);
                }
                break;
            case ENTERTAINMENT:
                if (processedPiePurchasesMap.containsKey(ENTERTAINMENT)) {
                    processedPiePurchasesMap.put(ENTERTAINMENT, Objects.requireNonNull(processedPiePurchasesMap.get(ENTERTAINMENT)) + (float) price);
                } else {
                    processedPiePurchasesMap.put(ENTERTAINMENT, (float) price);
                }
                break;
            case BILLS:
                if (processedPiePurchasesMap.containsKey(BILLS)) {
                    processedPiePurchasesMap.put(BILLS, Objects.requireNonNull(processedPiePurchasesMap.get(BILLS)) + (float) price);
                } else {
                    processedPiePurchasesMap.put(BILLS, (float) price);
                }
                break;
            case OTHER:
                if (processedPiePurchasesMap.containsKey(OTHER)) {
                    processedPiePurchasesMap.put(OTHER, Objects.requireNonNull(processedPiePurchasesMap.get(OTHER)) + (float) price);
                } else {
                    processedPiePurchasesMap.put(OTHER, (float) price);
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void processBarPurchase(Account.Purchase purchase) {
        float price = 0;
        if (budgetManager.isDownloaded()) {
            price = (float) budgetManager.convertToSetCurrency(account.getCurrencyType(), purchase.getCurrency(), purchase.getPrice());
        }
        switch (categoryType) {
            case 0:
                fillBarEntriesMap(purchase.getDate(), price);
                break;
            case 1:
                if (purchase.getCategory().equals(SHOP)) {
                    fillBarEntriesMap(purchase.getDate(), price);
                }
                break;
            case 2:
                if (purchase.getCategory().equals(CAFE)) {
                    fillBarEntriesMap(purchase.getDate(), price);
                }
                break;
            case 3:
                if (purchase.getCategory().equals(ENTERTAINMENT)) {
                    fillBarEntriesMap(purchase.getDate(), price);
                }
                break;
            case 4:
                if (purchase.getCategory().equals(BILLS)) {
                    fillBarEntriesMap(purchase.getDate(), price);
                }
                break;
            case 5:
                if (purchase.getCategory().equals(OTHER)) {
                    fillBarEntriesMap(purchase.getDate(), price);
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void fillBarEntriesMap(String date, float price) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        Calendar calendarPurchase = getInstance();
        try {
            calendarPurchase.setTime(Objects.requireNonNull(sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendarNow = getInstance();
        calendarNow.setTime(new Date());
        int today = calendarNow.get(DAY_OF_MONTH);
        int last = calendarPurchase.get(DAY_OF_MONTH);
        int diff = last - today;
        if (!processedBarPurchasesMap.containsKey(0) && diff != 0) {
            processedBarPurchasesMap.put(0, new BarEntry(0, 0));
        }
        if (processedBarPurchasesMap.containsKey(diff)) {
            BarEntry oldEntry = processedBarPurchasesMap.get(diff);
            processedBarPurchasesMap.remove(diff);
            BarEntry entry = new BarEntry(diff, Objects.requireNonNull(oldEntry).getY() + price);
            processedBarPurchasesMap.put(diff, entry);
        } else {
            processedBarPurchasesMap.put(diff, new BarEntry(diff, price));
        }
        fillLabelXBarMap(calendarPurchase, diff);
    }

    @SuppressLint("SimpleDateFormat")
    private void fillLabelXBarMap(Calendar calendarPurchase, int value) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String formatted = format.format(calendarPurchase.getTime());
        labelsXBarMap.put(value, formatted);
    }

    private void fillBarEntriesList() {
        barEntries = new ArrayList<>(processedBarPurchasesMap.values());
    }

    private void fillPieEntriesList() {
        float value;
        final String[] CATEGORIES = {SHOP, CAFE, ENTERTAINMENT, BILLS, OTHER};
        for (String key : CATEGORIES) {
            if (!processedPiePurchasesMap.isEmpty()) {
                if (processedPiePurchasesMap.containsKey(key)) {
                    value = Objects.requireNonNull(processedPiePurchasesMap.get(key));
                    pieEntries.add(new PieEntry(value, key));
                }
            }
        }
    }
}
