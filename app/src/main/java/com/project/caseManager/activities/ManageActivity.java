package com.project.caseManager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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

import com.project.caseManager.R;
import com.project.caseManager.classes.DataManager;
import com.project.caseManager.classes.MyListAdapter;
import com.project.caseManager.classes.Patients;
import com.project.caseManager.classes.URLs;
import com.project.caseManager.databinding.ActivityManageBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

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
            try {
                //adapter.notifyItemChanged(adapter.getSelectedPos());
                dm.delete(adapter.getId());
                adapter.removeItem();
                Toast.makeText(view.getContext(), "Record has been deleted!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(view.getContext(), "Please select a record!", Toast.LENGTH_SHORT).show();
            }
        });

        myBind.btnUpdate.setOnClickListener(view -> { //Same thing is done here, but just updates db.
            try{
                if (adapter.getSelectedPos() == -1 ) Toast.makeText(view.getContext(), "Please select a record!", Toast.LENGTH_SHORT).show();
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
                        try{
                            adapter.getPatients().set(adapter.getSelectedPos(), new Patients(adapter.getId(), fullName, address, date)); //THIS IS UPDATE
                            dm.update(adapter.getId(), fullName, address, date);
                            adapter.notifyItemChanged(adapter.getSelectedPos());
                        } catch (Exception e){
                            Toast.makeText(view.getContext(), "Selected one too many!", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }catch (Exception e) {
                Toast.makeText(view.getContext(), "Exception Thrown: " + e.toString(), Toast.LENGTH_SHORT).show(); System.out.println("ID: " + adapter.getId());
            }
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

                if (!(fullName.isEmpty() || address.isEmpty())){
                    adapter.getPatients().add(new Patients(fullName, address, date));
                    dm.insert(fullName, address, date); //Insert to SQLite Database
                    adapter.notifyDataSetChanged();
                } else {
                    if (fullName.isEmpty()) popUpFullName.get().setError("Field cannot be left blank.");
                    else popUpAddress.get().setError("Field cannot be left blank.");
                }
            });
        });

        myBind.btnCommit.setOnClickListener(view -> {
            //Read from database
            String mydata="";
            Patients patients = new Patients();
            Cursor d = dm.selectAll();

            try {
                if (d.getCount() > 0) {
                    JSONObject data = new JSONObject(); //This is the Primary JSON file we will be sent as String mydata to server.
                    JSONArray jsonArray = new JSONArray(); //the table saved as an array named Data below.
                    while (d.moveToNext()) {
                        JSONObject record = new JSONObject();
                        patients.id = d.getInt(0);
                        record.put("id", ""+patients.id);
                        patients.fullname = d.getString(1);
                        record.put("fullname", ""+patients.fullname);
                        patients.address = d.getString(2);
                        record.put("address", ""+patients.address);
                        patients.date = d.getString(3);
                        record.put("dateofexposure", ""+patients.date);
                        jsonArray.put(record);
                        Log.i("MESSAGE",record.toString());
                    }
                    data.put("Data",jsonArray);
                    Log.i("MESSAGE",data.toString());
                    mydata= data.toString();
                }
            }
            catch(JSONException e)
            {
                Toast.makeText(view.getContext(), "JSON file failed to instantiate", Toast.LENGTH_SHORT).show();
            }
            Worker worker = new Worker();
            worker.execute(mydata);
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


    class Worker extends AsyncTask<String, String, String> {
        TextView tv;

        protected String doInBackground(String... s) {
            StringBuilder sb =  new StringBuilder();
            String params = s[0];
            URL url;
            try {
                url = new URL(URLs.URL_SEND);

                Log.d("MESSAGE", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Send data
                OutputStream os2 = conn.getOutputStream();
                BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(os2, "UTF-8"));
                writer2.write(params);
                writer2.flush();
                writer2.close();
                os2.close();

                // 2. Open connection
                conn.connect();
                // 3. Get response
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d("MESSAGE","OK");
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuilder();
                    String response;
                    while ((response = br.readLine()) != null) {
                        sb.append(response);
                    }
                    Log.d("MESSAGE",sb.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return (sb.toString());
        }


        protected void onProgressUpdate(String... values) {
            //tv.setText(values[0]);

        }

        protected void onPostExecute(String s) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // DO SOMETHING WITH STRING RESPONSE
            super.onPostExecute(s);
            Log.i("Response: ", s);
            Toast.makeText(getApplicationContext(), "Response: " + s, Toast.LENGTH_SHORT).show();
        }
    }


}