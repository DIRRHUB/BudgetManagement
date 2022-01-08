package com.example.budgetmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;

import com.example.budgetmanagement.databinding.ActivityLoginBinding;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity{
    private static ActivityLoginBinding binding;
    private DatabaseContent databaseContent;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent = new Intent(LoginActivity.this, MainActivity.class);

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
        if (!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())) {
            databaseContent.login(binding.textEmail.getText().toString(), binding.textPassword.getText().toString());
        }
    }

    public void onClickNext (View view) {
        if (databaseContent.checkAuth()) {
            startActivity(intent);
        }
    }
    public static void setName(String name){
        binding.textHelloUser.setText(name);
    }
    public static void updateUILoggedIn(){
        binding.bNext.setVisibility(View.VISIBLE);
        binding.textHello.setVisibility(View.VISIBLE);
        binding.textHelloUser.setVisibility(View.VISIBLE);
        binding.constraintLayout.setVisibility(View.GONE);
        binding.textRegistration4.setVisibility(View.GONE);
    }
    public static void updateUILoggedOut(){
        binding.bNext.setVisibility(View.GONE);
        binding.textHello.setVisibility(View.GONE);
        binding.textHelloUser.setVisibility(View.GONE);
        binding.constraintLayout.setVisibility(View.VISIBLE);
        binding.textRegistration4.setVisibility(View.VISIBLE);
    }
}