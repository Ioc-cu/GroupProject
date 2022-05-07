package com.metropolitan.internetcall.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.classes.Patients;
import com.metropolitan.internetcall.classes.URLs;
import com.metropolitan.internetcall.databinding.ActivitySendBinding;
import com.metropolitan.internetcall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendActivity extends AppCompatActivity {
    DataManager dm ;
    //ActivityMainBinding mybind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivitySendBinding mybind = ActivitySendBinding.inflate(getLayoutInflater());
        View myview = mybind.getRoot();
        setContentView(myview);





        //if the user is already logged in we will directly start the profile activity
    /*    if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MenuActivity.class));
        }*/

        mybind.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //Read from database

                String mydata="";
                dm = new DataManager(SendActivity.this);
                Patients patients = new Patients();
                Cursor d = dm.selectAll();

                try {
                    if (d.getCount() > 0) {
                        JSONObject data = new JSONObject();


                        JSONArray jsonArray = new JSONArray();
                        while (d.moveToNext()) {
                            JSONObject record = new JSONObject();
                            patients.id = d.getInt(0);
                            record.put("pid", ""+patients.id);
                            patients.fullname = d.getString(1);
                            record.put("fullname", ""+patients.fullname);
                            patients.address = d.getString(2);
                            record.put("address", ""+patients.address);
                            patients.date = d.getString(2);
                            record.put("date", ""+patients.date);
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


                }
                Worker worker = new Worker();
                worker.execute(mydata);
            }
        });
    }


    class Worker extends AsyncTask<String, String, String> {
        TextView tv;

        protected String doInBackground(String... s) {
            StringBuilder sb =  new StringBuilder();
            String params = s[0];
            URL url;
            try {
                url = new URL(URLs.URL_SEND);
                // url = new URL(URLs.URL_LOGIN+params);

                Log.d("MESSAGE", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();


                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //Send credentials


                //Send data

                OutputStream os2 = conn.getOutputStream();

                BufferedWriter writer2 = new BufferedWriter(
                        new OutputStreamWriter(os2, "UTF-8"));
                writer2.write(params);
                //writer.write(getPostDataString(postDataParams));
                writer2.flush();
                writer2.close();
                os2.close();


             /*
                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params);
                //writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();*/
                // 2. Open InputStream to connection
                conn.connect();
                InputStream in = conn.getInputStream();


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
            // tv = textViews[0];
            //  nestedCall();
            //  return null;
        }


        protected void onProgressUpdate(String... values) {
            //tv.setText(values[0]);

        }

        protected void onPostExecute(String s) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // DO SOMETHING WITH STRING RESPONSE

            super.onPostExecute(s);
            //hiding the progressbar after completion
            //progressBar.setVisibility(View.GONE);

        }
    }

}
