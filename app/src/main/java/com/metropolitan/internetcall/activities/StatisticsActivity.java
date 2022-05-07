package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.metropolitan.internetcall.R;
import com.metropolitan.internetcall.databinding.ActivityStatisticsBinding;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityStatisticsBinding myBind = ActivityStatisticsBinding.inflate(getLayoutInflater());
        View myView = myBind.getRoot();
        setContentView(myView);


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