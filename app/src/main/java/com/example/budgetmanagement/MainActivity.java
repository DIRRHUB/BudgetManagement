package com.example.budgetmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.budgetmanagement.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseContent databaseContent;
    private List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        list = new ArrayList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseContent = new DatabaseContent();
        databaseContent.init();
    }

    public void onClickSignOut(View view) {
        databaseContent.signOut();
        LoginActivity.updateUILoggedOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void onClickSave(View view){
        databaseContent.saveToDatabase(new Account(100, 100, 20, databaseContent.getUID(), "NAME", "smth"));
    }

    public void onClickLoad(View view) {
        list = databaseContent.loadFromDatabase();
        Log.i("List", list.toString());
       // binding.editList.setText(Arrays.toString(list.toArray()));
    }


}