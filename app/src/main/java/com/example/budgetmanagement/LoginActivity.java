package com.example.budgetmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.example.budgetmanagement.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseContent databaseContent;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = new Intent(this, MainActivity.class);

        databaseContent = new DatabaseContent();
        databaseContent.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(databaseContent.checkAuth()){
            startActivity(intent);
        }
    }


    public void onClickRegister(View view) {
        if(!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())){
            databaseContent.register(binding.textEmail.getText().toString(), binding.textPassword.getText().toString());
        }
    }

    public void onClickLogin(View view) {
        if(!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())){
             databaseContent.login(binding.textEmail.getText().toString(), binding.textPassword.getText().toString());
        }
        if(databaseContent.checkAuth())
            startActivity(intent);
    }
}