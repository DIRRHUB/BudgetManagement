package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Account.Purchase> purchasesArrayList;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<Account.Purchase> purchasesArrayList) {
        this.context = context;
        this.purchasesArrayList = purchasesArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Account.Purchase purchase = purchasesArrayList.get(position);

        NumberFormat formatDouble = NumberFormat.getInstance(Locale.ENGLISH);
        formatDouble.setMaximumFractionDigits(2);
        viewHolder.textName.setText(purchase.getName());
        viewHolder.textCategory.setText(purchase.getCategory());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss").parse(purchase.getDate());
            viewHolder.textDate.setText(String.format("%1$td %1$tB %1$tY %1$tH:%1$tM:%1$tS", date));
        } catch (ParseException e) {
            viewHolder.textDate.setText("");
            e.printStackTrace();
        }
        viewHolder.textPrice.setText(formatDouble.format(purchase.getPrice()));
        viewHolder.textCurrency.setText(purchase.getCurrency());

    }

    @Override
    public int getItemCount() {
        return this.purchasesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName;
        private TextView textCategory;
        private TextView textDate;
        private TextView textPrice;
        private TextView textCurrency;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.textName = view.findViewById(R.id.textName);
            this.textCategory = view.findViewById(R.id.textCategory);
            this.textDate = view.findViewById(R.id.textDate);
            this.textPrice = view.findViewById(R.id.textPrice);
            this.textCurrency = view.findViewById(R.id.textCurrency);
        }
    }

}