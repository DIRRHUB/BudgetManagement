package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListAdapter extends ArrayAdapter<Account.Purchase> {

    public ListAdapter(Context context, ArrayList<Account.Purchase> purchaseArrayList) {
        super(context, R.layout.list_item, purchaseArrayList);
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        Account.Purchase purchase = getItem(position);

        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        NumberFormat formatDouble = NumberFormat.getInstance(Locale.ENGLISH);
        formatDouble.setMaximumFractionDigits(2);

        TextView textName = convertView.findViewById(R.id.textName);
        TextView textCategory = convertView.findViewById(R.id.textCategory);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textPrice = convertView.findViewById(R.id.textPrice);
        TextView textCurrency = convertView.findViewById(R.id.textCurrency);

        textName.setText(purchase.getName());
        textCategory.setText(purchase.getCategory());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(purchase.getDate());
            textDate.setText(String.format("%1$td %1$tB %1$tY %1$tH:%1$tM:%1$tS",date));
        } catch (ParseException e) {
            textDate.setText("");
            e.printStackTrace();
        }
        textPrice.setText(formatDouble.format(purchase.getPrice()));
        textCurrency.setText(purchase.getCurrency());

        return convertView;
    }
}
