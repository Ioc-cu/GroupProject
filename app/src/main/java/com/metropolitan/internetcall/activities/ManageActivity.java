package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.metropolitan.internetcall.R;
import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.classes.MyListAdapter;
import com.metropolitan.internetcall.classes.Patients;
import com.metropolitan.internetcall.databinding.ActivityManageBinding;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {

    DataManager dm;
    ArrayList<Patients> myPatients;
    Patients p;
    Cursor d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityManageBinding myBind = ActivityManageBinding.inflate(getLayoutInflater());
        View myView = myBind.getRoot();
        setContentView(myView);

        dm= new DataManager(ManageActivity.this);
        p = new Patients();
        d = dm.selectAll();
        myPatients = new ArrayList<Patients>();
        dm.printLocation();

        //Instantiates myPatients ArrayList with SQLite rows
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

        //Populates RecyclerView
        MyListAdapter adapter = new MyListAdapter(myPatients);
        myBind.recyclerView.setHasFixedSize(true);
        myBind.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myBind.recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Manage":
                System.out.println("You clicked Manage!");
                startActivity(new Intent(this, ManageActivity.class));
                break;
            case "Statistics":
                System.out.println("You clicked Statistics!");
                startActivity(new Intent(this, StatisticsActivity.class));
                break;
            case "Menu":
                System.out.println("You clicked Menu!");
                startActivity(new Intent(this, MenuActivity.class));
                break;
            default:
                System.out.println("Error");
                break;
        }
        return true;
    }



}