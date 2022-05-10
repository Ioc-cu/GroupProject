package com.project.caseManager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.project.caseManager.classes.DataManager;
import com.project.caseManager.classes.SharedPrefManager;
import com.project.caseManager.classes.User;
import com.project.caseManager.R;
import com.project.caseManager.databinding.ActivityMainMenuBinding;

//Shows the Logged in User's  SharedPrefferences and gives the user the ability to navigate to ManageActivity and logout(which deletes SQLiteDatabase in the Process).
public class MenuActivity extends AppCompatActivity { //Session Activity

    DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainMenuBinding myBind = ActivityMainMenuBinding.inflate(getLayoutInflater());
        View myView = myBind.getRoot();
        setContentView(myView);

        dm= new DataManager(MenuActivity.this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        myBind.textViewId.setText(String.valueOf(user.getId()));
        myBind.textViewUsername.setText(user.getUsername());
        myBind.textViewEmail.setText(user.getEmail());

        //when the user presses logout button
        //calling the logout method
        myBind.buttonLogout.setOnClickListener(view -> {
            dm.deleteDb();
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
        });
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
                finish();
                startActivity(new Intent(this, ManageActivity.class));
                break;
            case "Menu":
                System.out.println("You clicked Menu!");
                finish();
                startActivity(new Intent(this, MenuActivity.class));
                break;
            default:
                System.out.println("Error");
                break;
        }
        return true;
    }

}