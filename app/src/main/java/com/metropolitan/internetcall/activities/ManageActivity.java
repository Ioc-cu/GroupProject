package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.metropolitan.internetcall.R;
import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.classes.MyListAdapter;
import com.metropolitan.internetcall.classes.Patients;
import com.metropolitan.internetcall.databinding.ActivityManageBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ManageActivity extends AppCompatActivity {

    DataManager dm;
    ArrayList<Patients> myPatients;
    Patients p;
    Cursor d;

    android.app.DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener onDateSetListener;

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

        AtomicReference<EditText> popUpFullName = new AtomicReference<>(findViewById(R.id.popupfullname));
        AtomicReference<EditText> popUpAddress = new AtomicReference<>(findViewById(R.id.popupaddress));
        AtomicReference<TextView> popUpDate = new AtomicReference<>(findViewById(R.id.popupdate));
        AtomicReference<Button> btnSubmit = new AtomicReference<>(findViewById(R.id.btnSubmit));

        onDateSetListener = (view, year, month, dayOfMonth) -> {
            String date = String.format("%d-%d-%d", year, month, dayOfMonth);
            popUpDate.get().setText(date);
        };


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


        myBind.btnDelete.setOnClickListener(view -> {
            try{
                dm.delete(adapter.getId());
                adapter.removeItem();
                Toast.makeText(view.getContext(), "Record has been deleted!", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(view.getContext(), "Please select a record!", Toast.LENGTH_SHORT).show();
            }
        });

        myBind.btnUpdate.setOnClickListener(view -> { //Same thing is done here, but just updates db.
            try{
                if (adapter.getId() == 0 ) Toast.makeText(view.getContext(), "Please select a record!", Toast.LENGTH_SHORT).show();
                else {

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup_row, null);
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.MATCH_PARENT;
                    boolean focusable = true;
                    PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    //Instantiate here

                    popUpFullName.set(popupView.findViewById(R.id.popupfullname));
                    popUpAddress.set(popupView.findViewById(R.id.popupaddress));
                    popUpDate.set(popupView.findViewById(R.id.popupdate));
                    btnSubmit.set(findViewById(R.id.btnSubmit));

                    popupView.findViewById(R.id.popupdate).setOnClickListener(view2 -> {
                        datePickerDialog = new DatePickerDialog(this);
                        datePickerDialog.show();
                        datePickerDialog.setOnDateSetListener(onDateSetListener);
                    });

                    popupView.findViewById(R.id.btnSubmit).setOnClickListener(view2 -> {
                        String fullName = popUpFullName.get().getText().toString();
                        String address = popUpAddress.get().getText().toString();
                        String date = popUpDate.get().getText().toString();

                        //Update Segment
                        adapter.getPatients().set(adapter.getSelectedPos(), new Patients(adapter.getId(), fullName, address, date)); //THIS IS UPDATE
                        dm.update(adapter.getId(), fullName, address, date);
                        adapter.notifyItemChanged(adapter.getSelectedPos());
                    });
                }
            }catch (Exception e) {Toast.makeText(view.getContext(), "Please select a record!", Toast.LENGTH_SHORT).show();}

        });

        //Insert Button
        myBind.btnInsert.setOnClickListener(view -> {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_row, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true;
            PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            //Instantiate here

            popUpFullName.set(popupView.findViewById(R.id.popupfullname));
            popUpAddress.set(popupView.findViewById(R.id.popupaddress));
            popUpDate.set(popupView.findViewById(R.id.popupdate));
            btnSubmit.set(findViewById(R.id.btnSubmit));

            popupView.findViewById(R.id.popupdate).setOnClickListener(view2 -> {
                datePickerDialog = new DatePickerDialog(this);
                datePickerDialog.show();
                datePickerDialog.setOnDateSetListener(onDateSetListener);
            });

            popupView.findViewById(R.id.btnSubmit).setOnClickListener(view2 -> {
                String fullName = popUpFullName.get().getText().toString();
                String address = popUpAddress.get().getText().toString();
                String date = popUpDate.get().getText().toString();
                adapter.getPatients().add(new Patients(adapter.getId(), fullName, address, date));
                dm.insert(fullName, address, date); //Insert to SQLite Database
                adapter.notifyItemChanged(adapter.getSelectedPos());
            });
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