package com.budgetmanagement;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class BudgetManager {
    private final String PATH = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange";
    private Map<String, Double> mapContent;
    private boolean isDownloaded = false;
    private double convertedEUR, convertedRUB, convertedUSD;

    BudgetManager() {
        new Thread(() -> {
            try {
                mapContent = new HashMap<>();
                download();
                finishDownload();
            } catch (IOException ex) {
                Log.e("init", ex.toString());
            }
        }).start();
    }

    private void download() throws IOException {
        BufferedReader reader = null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(PATH);
            connection = (HttpsURLConnection) url.openConnection();
            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String cc = null, rate;
            double rateDouble = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<rate>")) {
                    rate = line;
                    rate = rate.replace("    <rate>", "");
                    rate = rate.replace("</rate>", "");
                    rateDouble = Double.parseDouble(rate);
                }
                if (line.contains("<cc>")) {
                    cc = line;
                    cc = cc.replace("    <cc>", "");
                    cc = cc.replace("</cc>", "");
                }
                if (line.contains("</currency>")) {
                    mapContent.put(cc, rateDouble);
                }
            }
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

    private void finishDownload() {
        if (!mapContent.isEmpty()) {
            convertedEUR = mapContent.get("EUR");
            convertedRUB = mapContent.get("RUB");
            convertedUSD = mapContent.get("USD");
            isDownloaded = true;
        }
    }

    public double convertToSetCurrency(String accType, String purchaseType, double price) {
        double purchasePrice;
        double convertedPrice;
        switch (purchaseType) {
            case "RUB":
                purchasePrice = price * convertedRUB;
                break;
            case "USD":
                purchasePrice = price * convertedUSD;
                break;
            case "EUR":
                purchasePrice = price * convertedEUR;
                break;
            default:
                purchasePrice = price;
        }
        switch (accType) {
            case "RUB":
                convertedPrice = purchasePrice / convertedRUB;
                break;
            case "USD":
                convertedPrice = purchasePrice / convertedUSD;
                break;
            case "EUR":
                convertedPrice = purchasePrice / convertedEUR;
                break;
            default:
                convertedPrice = purchasePrice;
        }

        return convertedPrice;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public double getConvertedEUR() {
        return convertedEUR;
    }

    public double getConvertedRUB() {
        return convertedRUB;
    }

    public double getConvertedUSD() {
        return convertedUSD;
    }
}
