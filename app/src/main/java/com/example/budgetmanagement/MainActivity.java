package com.example.budgetmanagement;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.budgetmanagement.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseReference database;
    private final String USER_KEY = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = getInstance().getReference(USER_KEY);

    }

    public void onClickSave(View view) {
        String id = database.getKey();
        String email = binding.textEmail.getText().toString();
        String pass = binding.textPassword.getText().toString();
        String budget = binding.textPasswordConfirm.getText().toString();
        Account account = new Account(id, email, pass, budget);

        database.push().setValue(account);


    }
    public void onClickLoad(View view) {
    }


}