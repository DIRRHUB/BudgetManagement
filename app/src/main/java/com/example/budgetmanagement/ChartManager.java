package com.example.budgetmanagement;

import static java.util.Calendar.*;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.example.budgetmanagement.databinding.FragmentPieChartBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
    private DatabaseContent databaseContent;
    private BudgetManager budgetManager;
    private List<Account.Purchase> purchases;
    private List<PieEntry> entries;
    private List<Integer> colors;
    private Account account;
    private Map<String, Float> processedPurchasesMap;
    private final String SHOP = "Магазин";
    private final String CAFE = "Рестораны";
    private final String ENTERTAINMENT = "Развлечения";
    private final String OTHER = "Другое";
    private final String PATTERN = "yyyy-MM-dd-hh.mm.ss";

    ChartManager (FragmentPieChartBinding binding){
        init();
        databaseContent.loadPurchaseFromDatabase(arrayList -> {
            purchases = new ArrayList<>(arrayList);
            getPieData(0, data -> {
                binding.chart.setData(data);
                binding.chart.invalidate();
            });
        });
    }



    private void init(){
        databaseContent = new DatabaseContent();
        account = new Account();
        budgetManager = new BudgetManager();
        databaseContent.loadAccountFromDatabase(account -> this.account = account);
    }

    public void getPieData(int time, ChartCallback callback){
        processedPurchasesMap = new HashMap<>();
        entries = new ArrayList<>();
        try {
            processPurchasesList(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        fillEntriesList();
        PieDataSet dataSet = new PieDataSet(entries, "");
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
        callback.PieChartCallback(pieData);
    }

    private void setColorsList(){
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
    private void processPurchasesList(int time) throws ParseException {
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

        if(purchases!=null && budgetManager.isDownloaded()) {
            for (Account.Purchase p : purchases) {
                String dateString = p.getDate();
                Date datePurchase = sdf.parse(dateString);
                switch (time) {
                    case 0:
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                    case 1:
                        calendarStart.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 1);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 1);
                        break;
                    case 2:
                        calendarStart.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH) - 7);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                    case 3:
                        calendarStart.set(MONTH, calendarNow.get(MONTH) - 1);
                        calendarEnd.set(MONTH, calendarNow.get(MONTH));
                        break;
                    case 4:
                        calendarStart.set(YEAR, 0);
                        calendarEnd.set(DAY_OF_MONTH, calendarNow.get(DAY_OF_MONTH));
                        break;
                }
                dateStart = calendarStart.getTime();
                dateEnd = calendarEnd.getTime();
                if (datePurchase!=null && dateStart.getTime() <= datePurchase.getTime()
                                       && dateEnd.getTime() > datePurchase.getTime()) {
                    processPurchase(p);
                }
            }
        }
    }

    private void processPurchase(Account.Purchase purchase){
        double price = 0;
        if(budgetManager.isDownloaded()){
            price = budgetManager.convertToSetCurrency(account.getCurrencyType(), purchase.getCurrency(), purchase.getPrice());
        }
        switch (purchase.getCategory()) {
            case SHOP:
                if(processedPurchasesMap.containsKey(SHOP)){
                    processedPurchasesMap.put(SHOP, Objects.requireNonNull(processedPurchasesMap.get(SHOP)) + (float) price);
                } else {
                    processedPurchasesMap.put(SHOP, (float) price);
                }
                break;
            case CAFE:
                if(processedPurchasesMap.containsKey(CAFE)){
                    processedPurchasesMap.put(SHOP, Objects.requireNonNull(processedPurchasesMap.get(CAFE)) + (float) price);
                } else {
                    processedPurchasesMap.put(CAFE, (float) price);
                }
                break;
            case ENTERTAINMENT:
                if(processedPurchasesMap.containsKey(ENTERTAINMENT)){
                    processedPurchasesMap.put(SHOP, Objects.requireNonNull(processedPurchasesMap.get(ENTERTAINMENT)) + (float) price);
                } else {
                    processedPurchasesMap.put(ENTERTAINMENT, (float) price);
                }
                break;
            case OTHER:
                if(processedPurchasesMap.containsKey(OTHER)){
                    processedPurchasesMap.put(SHOP, Objects.requireNonNull(processedPurchasesMap.get(OTHER)) + (float) price);
                } else {
                    processedPurchasesMap.put(OTHER, (float) price);
                }
                break;
        }
    }

    private void fillEntriesList(){
        float value;
        final String[] CATEGORIES = {SHOP, CAFE, ENTERTAINMENT, OTHER};
        for(String key : CATEGORIES) {
            if (!processedPurchasesMap.isEmpty()) {
                if (processedPurchasesMap.containsKey(key)) {
                    value = Objects.requireNonNull(processedPurchasesMap.get(key));
                    entries.add(new PieEntry(value, key));
                }
            }
        }
    }
}
