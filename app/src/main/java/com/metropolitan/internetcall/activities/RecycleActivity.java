package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.classes.MyListAdapter;
import com.metropolitan.internetcall.classes.Patients;
import com.metropolitan.internetcall.R;

import java.util.ArrayList;

public class RecycleActivity extends AppCompatActivity {
    DataManager dm;
    ArrayList<Patients> myPatients;
    Patients p;
    Cursor d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lalalalla

        dm= new DataManager(RecycleActivity.this);
        p = new Patients();
        d = dm.selectAll();
        myPatients = new ArrayList<Patients>();
        if (d.getCount() > 0) {

            while (d.moveToNext()) {
                Log.i("SELECT", d.getString(1));
                p.id=d.getInt(0);
                p.fullname=d.getString(1);
                p.address=d.getString(2);
                p.date=d.getString(3);
                myPatients.add(new Patients(p.id, p.fullname, p.address, p.date));
            }
        }



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.MyRecycler);
        MyListAdapter adapter = new MyListAdapter(myPatients);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}