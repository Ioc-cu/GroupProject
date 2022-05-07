package com.metropolitan.internetcall.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.metropolitan.internetcall.classes.DataManager;
import com.metropolitan.internetcall.classes.SharedPrefManager;
import com.metropolitan.internetcall.classes.URLs;
import com.metropolitan.internetcall.classes.User;
import com.metropolitan.internetcall.databinding.ActivityLoginBinding;
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

public class LoginActivity extends AppCompatActivity {
    DataManager dm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityLoginBinding myBind = ActivityLoginBinding.inflate(getLayoutInflater());
        View myView = myBind.getRoot();
        setContentView(myView);


        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MenuActivity.class));
        }

        myBind.buttonLogin.setOnClickListener(view-> {
                String credentials ="&email="+myBind.editTextUsername.getText().toString()+"&password="+myBind.editTextPassword.getText().toString();
                Worker worker = new Worker();
                worker.execute(credentials);});
        myBind.textViewRegister.setOnClickListener(view-> {startActivity(new Intent(getApplicationContext(), RegisterActivity.class));});
    }

    class Worker extends AsyncTask<String, String, String> {

        protected String doInBackground(String... s) {
            StringBuilder sb = new StringBuilder();
            String params = s[0];
            URL url;
            try {
                //1. Create a connection to the api server
                url = new URL(URLs.URL_LOGIN);

                Log.d("MESSAGE", url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //Connection Packet Parameters.
                conn.setReadTimeout(1500);
                conn.setConnectTimeout(1500);
                conn.setRequestMethod("POST");
                conn.setDoInput(true); //In order to use InputStream;
                conn.setDoOutput(true); //In order to use OutputStream;

                //2.1. Send credentials
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(params);
                writer.flush();
                writer.close();
                os.close();

                // 2.2. Open InputStream to connection
                conn.connect();


                // 3. Get response
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d("MESSAGE","OK");
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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


        protected void onPostExecute(String s) { //
            // This method is executed in the UIThread
            // with access to the result of the long running task
            // DO SOMETHING WITH STRING RESPONSE

            super.onPostExecute(s);
            //hiding the progressbar after completion
            //progressBar.setVisibility(View.GONE);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(s);

                //if no error in response
                if (!obj.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    //getting the user from the response
                    JSONObject userJson = obj.getJSONObject("user");

                    //creating a new user object
                    User user = new User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email")
                    );

                    //storing the user in shared preferences
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                    //getting the patient rows from the response and saving them on SQLite
                    dm = new DataManager(LoginActivity.this);
                    JSONArray patientsJson = obj.getJSONArray("patients");
                    int length = patientsJson.length();
                    for(int i=0; i<length; i++) {
                        JSONObject jsonObj = patientsJson.getJSONObject(i);
                        Log.i("PATIENTS", jsonObj.getString("fullname"));
                        dm.insert(jsonObj.getString("fullname"), jsonObj.getString("address"), jsonObj.getString("dateofexposure"));
                    }

                    //starting the profile activity
                    finish();
                    startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
