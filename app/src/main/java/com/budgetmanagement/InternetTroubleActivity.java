package com.budgetmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InternetTroubleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_trouble);
        Button buttonStart = findViewById(R.id.buttonTryStart);
        buttonStart.setOnClickListener(l -> {
            if (SpecialFunction.isNetworkAvailable())
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}