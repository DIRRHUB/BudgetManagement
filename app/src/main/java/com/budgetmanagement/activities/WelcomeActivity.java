package com.budgetmanagement.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.budgetmanagement.R;
import com.budgetmanagement.activities.LoginActivity;
import com.budgetmanagement.activities.MainActivity;
import com.budgetmanagement.database.DatabaseContent;
import com.budgetmanagement.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private DatabaseContent databaseContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseContent = new DatabaseContent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.bNext.setOnClickListener(clickListener);
        binding.bSignOut.setOnClickListener(clickListener);
    }

    private final View.OnClickListener clickListener = view -> {
        if (view.getId()== R.id.bNext){
            if(databaseContent.checkVerification()){
                startActivity(new Intent(this, MainActivity.class));
            }
        } else if(view.getId()==R.id.bSignOut){
            databaseContent.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}