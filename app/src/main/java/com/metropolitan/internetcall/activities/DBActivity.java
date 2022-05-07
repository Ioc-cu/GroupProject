package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.R;
import com.metropolitan.internetcall.databinding.ActivityMain2Binding;

public class DBActivity extends AppCompatActivity {
    DataManager dm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMain2Binding mybind = ActivityMain2Binding.inflate(getLayoutInflater());
        View myview = mybind.getRoot();
        setContentView(myview);
        dm= new DataManager(DBActivity.this);
        mybind.btnInsert.setOnClickListener(view -> dm.insert(mybind.editName.getText().toString(), mybind.editAddress.getText().toString(), mybind.editAge.getText().toString()));
        mybind.btnSelect.setOnClickListener(view -> {
            Cursor s = dm.selectAll();
            if (s.getCount() > 0 ) while (s.moveToNext()) Log.i("SELECT", s.getString(1));
        });
    }
   }