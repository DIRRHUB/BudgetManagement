package com.budgetmanagement.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budgetmanagement.R;
import com.budgetmanagement.services.SpecialFunction;
import com.budgetmanagement.activities.InternetTroubleActivity;
import com.budgetmanagement.database.DatabaseContent;
import com.budgetmanagement.databinding.FragmentSettingsBinding;
import com.budgetmanagement.entity.Account;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private DatabaseContent databaseContent;
    private FragmentSettingsBinding binding;
    private Account account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseContent = new DatabaseContent();
        account = new Account();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.setName.setOnClickListener(this);
        binding.setBudget.setOnClickListener(this);
        if (SpecialFunction.isNetworkAvailable()) {
            databaseContent.loadAccountFromDatabase(account -> {
                this.account = account;
                setUsername();
                setBudget();
                setCurrencyType();
                fillSpinners();
            });
        } else {
            startActivity(new Intent(getActivity(), InternetTroubleActivity.class));
        }
        return binding.getRoot();
    }

    private void setUsername() {
        binding.username.setText(account.getPersonName());
    }

    private void fillSpinners(){
        Resources r = getResources();
        String[] currencyArray = r.getStringArray(R.array.list_currency);
        ArrayAdapter<String> adapterCurrencyMenu = new ArrayAdapter<>(requireContext(), R.layout.list_item_spinner, currencyArray);
        binding.spinnerEditCurrency.setAdapter(adapterCurrencyMenu);
        binding.spinnerEditCurrency.setOnItemClickListener((adapterView, view, i, l) -> {
            if(view.getId()==R.id.spinnerEditCurrency) {
                setCurrencyConfirmation(adapterView.getSelectedItem().toString());
            }
        });
    }

    private void setBudget() {
        try {
            double newBudget = (Double.parseDouble(binding.editBudget.getText().toString().replace(",", ".")));
            //TODO Update budgetLeft
            account.setBudget(newBudget);
            databaseContent.saveToDatabase(account);
        } catch (NumberFormatException e) {
            Log.e("Error parse to double", binding.editBudget.getText().toString());
        }
        binding.editBudget.setText(String.valueOf(account.getBudget()));
    }

    private void setBudgetConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                setBudget();
                builder.setMessage(R.string.update_budget_alert)
                        .setPositiveButton("Да", (dialog2, which2) -> {
                            if(which2 == DialogInterface.BUTTON_POSITIVE){
                                account.setBudgetLeft(account.getBudget());
                                databaseContent.saveToDatabase(account);
                            }
                        })
                        .setNegativeButton("Нет", null).show();
            }
        };
        builder.setMessage(R.string.change_budget_alert)
                .setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();

    }

    private void setCurrencyConfirmation(String currency){
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                account.setCurrencyType(currency);
                databaseContent.saveToDatabase(account);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.change_currency_alert)
                .setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }


    private void setCurrencyType() {
        binding.spinnerEditCurrency.setText(account.getCurrencyType());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        if (SpecialFunction.isNetworkAvailable()) {
            databaseContent.loadAccountFromDatabase(account -> this.account = account);
        }
        switch (view.getId()) {
            case R.id.setName:
                if (!binding.username.getText().toString().equals(account.getPersonName())
                        && !TextUtils.isEmpty(binding.username.getText().toString())
                        && binding.username.getText().toString().length() <= 30) {
                    account.setPersonName(binding.username.getText().toString());
                    databaseContent.saveToDatabase(account);
                }
                break;
            case R.id.setBudget:
                if (!TextUtils.isEmpty(binding.editBudget.getText().toString()) && Double.parseDouble(binding.editBudget.getText().toString()) != account.getBudget()) {
                    setBudgetConfirmation();
                    databaseContent.saveToDatabase(account);
                }
                break;
        }
    }
}