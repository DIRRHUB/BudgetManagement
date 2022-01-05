package com.example.budgetmanagement;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.example.budgetmanagement.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseReference database;
    private final String USER_KEY = "Account";
    private List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        list = new ArrayList();

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
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list.size()>0) list.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Account account = ds.getValue(Account.class);
                    assert account!=null;
                    list.add(account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.addValueEventListener(valueEventListener);

        Log.i("List", list.toString());
    }


}