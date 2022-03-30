package com.example.budgetmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.budgetmanagement.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseContent databaseContent;
    private final TextView.OnEditorActionListener keyboardListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_DONE) {
                Log.i("DONE", "true");
                if (!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())) {
                    databaseContent.tryLogin(binding.textEmail.getText().toString(), binding.textPassword.getText().toString(),
                            access -> updateUIAuthorized(access), () -> hideKeyboard(textView));
                    return true;
                }
            }
            return false;
        }
    };
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = new Intent(this, MainActivity.class);
        databaseContent = new DatabaseContent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (databaseContent.checkAuth()) {
            if (databaseContent.checkVerification()) {
                startActivity(intent);
            } else {
                updateUIAuthorized(false);
            }
        } else {
            updateUIDefault();
            binding.textEmail.setOnEditorActionListener(keyboardListener);
            binding.textPassword.setOnEditorActionListener(keyboardListener);
        }
    }

    public void onClickRegister(View view) {
        SpecialFunction.hideKeyboard(view);
        if (!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())) {
            databaseContent.register(binding.textEmail.getText().toString(), binding.textPassword.getText().toString(), a -> updateUIAuthorized(a));
        }
    }

    public void onClickLogin(View view) {
        SpecialFunction.hideKeyboard(view);
        if (!TextUtils.isEmpty(binding.textEmail.getText().toString()) && !TextUtils.isEmpty(binding.textPassword.getText().toString())) {
            databaseContent.login(binding.textEmail.getText().toString(), binding.textPassword.getText().toString(), a -> updateUIAuthorized(a));
        }
    }

    public void onClickNext(View view) {
        if (databaseContent.checkAuth()) {
            if (databaseContent.checkVerification()) {
                startActivity(intent);
            }
        }
    }

    public void onClickSignOut(View view) {
        databaseContent.signOut();
        updateUIDefault();
    }

    private void updateUIAuthorized(boolean access) {
        binding.loginLayout.setVisibility(View.GONE);
        binding.welcomeLayout.setVisibility(View.VISIBLE);
        binding.bNext.setVisibility(View.VISIBLE);
        binding.bSignOut.setVisibility(View.VISIBLE);
        binding.textHello.setVisibility(View.VISIBLE);
        if (!access) {
            binding.textVerification.setVisibility(View.VISIBLE);
        }
    }

    private void updateUIDefault() {
        binding.welcomeLayout.setVisibility(View.GONE);
        binding.loginLayout.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(TextView textView) {
        SpecialFunction.hideKeyboard(textView);
        Log.i("login", "keyboard");
    }
}