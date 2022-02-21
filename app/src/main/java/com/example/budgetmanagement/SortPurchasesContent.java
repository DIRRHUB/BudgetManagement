package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class SortPurchasesContent {
    private ArrayList<Account.Purchase> arrayList;
    private boolean isDownloaded = false;
    private Map<String, Double> mapContent;
    private double convertedEUR, convertedRUB, convertedUSD;
    private final String PATH = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange";

    public SortPurchasesContent setArrayList(ArrayList<Account.Purchase> arrayList) {
        this.arrayList = (ArrayList<Account.Purchase>) arrayList;
        return this;
    }

    public ArrayList<Account.Purchase> getArrayList() {
        return arrayList;
    }

    @SuppressLint("NonConstantResourceId")
    public SortPurchasesContent sort(int sortType){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (sortType) {
                case 0://name a-z
                    arrayList.sort(Comparator.comparing(Account.Purchase::getName));
                    break;
                case 1://name z-a
                    arrayList.sort((t1, t2) -> t2.getName().compareTo(t1.getName()));
                    break;
                case 2://category a-z
                    arrayList.sort(Comparator.comparing(Account.Purchase::getCategory));
                    break;
                case 3://category z-a
                    arrayList.sort((t1, t2) -> t2.getCategory().compareTo(t1.getCategory()));
                    break;
                case 4://date 0-1
                    arrayList.sort(Comparator.comparing(Account.Purchase::getDate));
                    break;
                case 5://date 1-0
                    arrayList.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
                    break;
                case 6://price 0-1
                    sortPrice(true);
                    break;
                case 7://price 1-0
                    sortPrice(false);
                    break;
            }
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortPrice(boolean increasingPrice) {
        if(isDownloaded) {
            try {
                convertedEUR = mapContent.get(R.string.eur);
                convertedRUB = mapContent.get(R.string.rub);
                convertedUSD = mapContent.get(R.string.usd);
                Log.i("SortPurchasesEUR", String.valueOf(convertedEUR));
                Log.i("SortPurchasesRUB", String.valueOf(convertedRUB));
                Log.i("SortPurchasesUSD", String.valueOf(convertedUSD));
            } catch (NullPointerException e){
                Log.e("SortPurchases", "Something is wrong with map");
                return;
            }
            arrayList.sort((purchase1, purchase2) -> {
                double price1, price2;
                switch (purchase1.getCurrency()) {
                    case "UAH":
                        price1 = purchase1.getPrice() / convertedUSD;
                        break;
                    case "RUB":
                        price1 = purchase1.getPrice() * convertedRUB / convertedUSD;
                        break;
                    case "EUR":
                        price1 = purchase1.getPrice() * convertedEUR / convertedUSD ;
                        break;
                    default:
                        price1 = purchase1.getPrice();
                        break;
                }
                switch (purchase2.getCurrency()) {
                    case "UAH":
                        price2 = purchase2.getPrice() / convertedUSD;
                        break;
                    case "RUB":
                        price2 = purchase2.getPrice() * convertedRUB / convertedUSD;
                        break;
                    case "EUR":
                        price2 = purchase2.getPrice() * convertedEUR / convertedUSD ;
                        break;
                    default:
                        price2 = purchase2.getPrice();
                        break;
                }
                if(increasingPrice) {
                    return Double.compare(price1, price2);
                } else {
                    return Double.compare(price2, price1);
                }
            });
        }
    }

    public SortPurchasesContent tryGetExchangeRates() {
        new Thread(() -> {
            try{
                mapContent = new HashMap<>();
                mapContent = download(PATH);
                isDownloaded = true;
            }
            catch (IOException ex){
                Log.e("tryGetExchangeRates", ex.toString());
            }
        }).start();
        return this;
    }

    private Map<String, Double> download(String urlPath) throws IOException {
        Map<String, Double> map = new HashMap<>();
        BufferedReader reader = null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpsURLConnection) url.openConnection();
            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String  cc = null, rate;
            double rateDouble = 0;
            while ((line = reader.readLine()) != null) {
                if(line.contains("<rate>")){
                    rate = line;
                    rate = rate.replace("<rate>", "");
                    rate = rate.replace("</rate>", "");
                    rateDouble = Double.parseDouble(rate);
                }
                if(line.contains("<cc>")){
                    cc = line;
                    cc = cc.replace("    <cc>", "");
                    cc = cc.replace("</cc>", "");
                }
                if(line.contains("</currency>")){
                    map.put(cc, rateDouble);
                }
            }
            return map;
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
