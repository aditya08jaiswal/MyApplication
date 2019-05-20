package com.iam844.adityajaiswal.token;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    EditText authKeyText;
    EditText authSecretText;
    EditText kioskIdText;
    Button enterIdBtn;

    String AuthKey;
    String AuthSecret;
    String KioskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authKeyText = findViewById(R.id.auth_key_text);
        authSecretText = findViewById(R.id.auth_secret_text);
        kioskIdText = findViewById(R.id.kiosk_id_text);
        enterIdBtn = findViewById(R.id.enter_id_button);

        AuthKey = "yolotokenapp";
        AuthSecret =  "yyix2TO2sz";
        KioskId = kioskIdText.getText().toString();

        checkNetworkConnection();

        enterIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    send(v);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),"Exception : " + ex, Toast.LENGTH_SHORT).show();
                }

                Intent openTokenActivity = new Intent(MainActivity.this, TokenActivity.class);
                startActivity(openTokenActivity);
            }
        });


    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(),"Connected to Internet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Not Connected to Internet", Toast.LENGTH_SHORT).show();
        }

        return isConnected;
    }

    private String httpPost(String myURL) throws IOException, JSONException {

        String result ="";

        URL url = new URL(myURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        JSONObject jsonObject = buidJsonObject();

        // Add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // Make POST request to the given URL
        conn.connect();

        // Return response message
        return conn.getResponseMessage()+"";


    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    return httpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Main","Result : " + result);
        }
    }

    public void send(View view) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        // perform HTTP POST request
        if(checkNetworkConnection())
            new HTTPAsyncTask().execute("https://healthatm.in/api/Utils/getPatients");
        else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate("authkey",  "yolotokenapp");
        jsonObject.accumulate("authsecret",   "yyix2TO2sz");
        jsonObject.accumulate("kioskid", kioskIdText.getText().toString());

        Log.e("JSONOBJECT CALLED",jsonObject.toString());


        return jsonObject;
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
