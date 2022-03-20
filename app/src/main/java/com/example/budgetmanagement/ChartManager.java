package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private Account account;
    private Map<String, Float> processedPurchasesMap;
    final String SHOP = "Магазин";
    final String CAFE = "Ресторан";
    final String ENTERTAINMENT = "Развлечения";
    final String OTHER = "Другое";
    final String PATTERN = "yyyy-MM-dd-hh.mm.ss";
    final long DAY = 86400;
    final long WEEK = 604800;
    final long MONTH = 2592000;

    ChartManager (){
        databaseContent = new DatabaseContent();
        account = new Account();
        databaseContent.loadAccountFromDatabase(account -> this.account = account);
        databaseContent.loadPurchaseFromDatabase(arrayList -> purchases = new ArrayList<>(arrayList));
        budgetManager = new BudgetManager();
        processedPurchasesMap = new HashMap<>();
        entries = new ArrayList<>();
    }

    public PieData getPieData(int time, String label){
        processedPurchasesMap.clear();
        entries.clear();
        try {
            processPurchasesList(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return new PieData();
        }

        fillEntriesList();
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
        return data;
    }

    @SuppressLint("SimpleDateFormat")
    private void processPurchasesList(int time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        Date datePurchase;
        Date dateNow;
        dateNow = sdf.parse(new SimpleDateFormat(PATTERN).format(new Date()));

        if(purchases!=null) {
            for (Account.Purchase p : purchases) {
                String dateString = p.getDate();
                datePurchase = sdf.parse(dateString);
                long diff;
                if (dateNow != null && datePurchase != null) {
                    diff = dateNow.getTime() - datePurchase.getTime();
                    diff = (long) diff / 1000;
                } else {
                    return;
                }
                //REWRITE CONDITION
                if (0 < diff && diff <= DAY && time == 0) {
                    processPurchase(p);
                } else if (DAY < diff && diff <= 2 * DAY && time == 1) {
                    processPurchase(p);
                } else if (0 < diff && diff <= WEEK && time == 2) {
                    processPurchase(p);
                } else if (0 < diff && diff <= MONTH && time == 3) {
                    processPurchase(p);
                } else if (time == 4){
                    processPurchase(p);
                } else {
                    Log.e("Unknown purchase", p.getPurchaseID());
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
                    processedPurchasesMap.put(SHOP, processedPurchasesMap.get(SHOP) + (float) price);
                } else {
                    processedPurchasesMap.put(SHOP, (float) price);
                }
                break;
            case CAFE:
                if(processedPurchasesMap.containsKey(CAFE)){
                    processedPurchasesMap.put(SHOP, processedPurchasesMap.get(CAFE) + (float) price);
                } else {
                    processedPurchasesMap.put(CAFE, (float) price);
                }
                break;
            case ENTERTAINMENT:
                if(processedPurchasesMap.containsKey(ENTERTAINMENT)){
                    processedPurchasesMap.put(SHOP, processedPurchasesMap.get(ENTERTAINMENT) + (float) price);
                } else {
                    processedPurchasesMap.put(ENTERTAINMENT, (float) price);
                }
                break;
            case OTHER:
                if(processedPurchasesMap.containsKey(OTHER)){
                    processedPurchasesMap.put(SHOP, processedPurchasesMap.get(OTHER) + (float) price);
                } else {
                    processedPurchasesMap.put(OTHER, (float) price);
                }
                break;
        }
    }

    private void fillEntriesList(){
        float value;
        if(!processedPurchasesMap.isEmpty()) {
            if(processedPurchasesMap.containsKey(SHOP)) {
                value = Objects.requireNonNull(processedPurchasesMap.get(SHOP));
                entries.add(new PieEntry(value, SHOP));
            }
            if(processedPurchasesMap.containsKey(CAFE)) {
                value = Objects.requireNonNull(processedPurchasesMap.get(CAFE));
                entries.add(new PieEntry(value, CAFE));
            }
            if(processedPurchasesMap.containsKey(ENTERTAINMENT)) {
                value = Objects.requireNonNull(processedPurchasesMap.get(ENTERTAINMENT));
                entries.add(new PieEntry(value, ENTERTAINMENT));
            }
            if(processedPurchasesMap.containsKey(OTHER)) {
                value = Objects.requireNonNull(processedPurchasesMap.get(OTHER));
                entries.add(new PieEntry(value, OTHER));
            }
        }
    }
}
