package com.example.budgetmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Account.Purchase> {

    public ListAdapter(Context context, ArrayList<Account.Purchase> purchaseArrayList) {
        super(context, R.layout.list_item, purchaseArrayList);
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        Account.Purchase purchase = getItem(position);

        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView textName = convertView.findViewById(R.id.textName);
        TextView textCategory = convertView.findViewById(R.id.textCategory);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textPrice = convertView.findViewById(R.id.textPrice);
        TextView textCurrency = convertView.findViewById(R.id.textCurrency);

        textName.setText(purchase.name);
        textCategory.setText(purchase.category);
        textDate.setText(purchase.date);
        textPrice.setText(String.valueOf(purchase.price));
        textCurrency.setText(purchase.currency);

        return convertView;
    }
}
