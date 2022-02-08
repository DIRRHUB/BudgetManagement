package com.example.budgetmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.budgetmanagement.databinding.FragmentSettingsBinding;

import java.util.Objects;

public class Settings extends Fragment implements View.OnClickListener{
    private DatabaseContent databaseContent;
    private FragmentSettingsBinding binding;
    private Account account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseContent = new DatabaseContent();
        databaseContent.init();
        account = new Account();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.setName.setOnClickListener(this);
        binding.setBudget.setOnClickListener(this);
        binding.setCurrencyType.setOnClickListener(this);
        account = databaseContent.loadAccountFromDatabase(account -> {
            setAccount(account);
            setUsername();
            setBudget();
            setCurrencyType();
        });
        return binding.getRoot();
    }

    private void setUsername() {
        binding.username.setText(account.personName);
    }

    private void setBudget() {
        binding.editBudget.setText(String.valueOf(account.budget));
    }

    private void setCurrencyType() {
        selectSpinnerValue(binding.editCurrencyType, account.currencyType);
    }

    private void selectSpinnerValue(@NonNull Spinner spinner, String myString) {
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.setName:
                if (!binding.username.getText().toString().equals(account.personName)
                        && !TextUtils.isEmpty(binding.username.getText().toString())
                        && binding.username.getText().toString().length() <= 30 ) {

                    account.personName = binding.username.getText().toString();
                    databaseContent.saveToDatabase(account);
                }
                break;
            case R.id.setBudget:
                if (!TextUtils.isEmpty(binding.editBudget.getText().toString())) {
                    try {
                        account.budget = Double.parseDouble(binding.editBudget.getText().toString().replace(",", "."));
                        databaseContent.saveToDatabase(account);
                    } catch (NumberFormatException e){
                        Log.e("Error parse to double", binding.editBudget.getText().toString());
                    }
                }
            break;
            case R.id.setCurrencyType:
                account.currencyType = binding.editCurrencyType.getSelectedItem().toString();
                databaseContent.saveToDatabase(account);
                break;

        }
    }

    protected void setAccount(Account account){
        this.account = account;
    }
}