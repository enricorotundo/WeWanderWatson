package com.group5.wewander;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WatsonActivity extends AppCompatActivity {

    private WatsonQuery query;
    private String queryString = "";
    private String answer = "";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // handle the text input
        String placeName = getIntent().getExtras().getString("placeName");
        if (placeName != null) {
            Log.d(this.toString(), placeName);
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText("Ask something about " + placeName + "...");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // handle the "Ask" button
        final Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText question = (EditText) findViewById(R.id.editText);
                if (question.getText().toString() == "") {
                    queryString = question.getText().toString();
                }
                query = new WatsonQuery();
                query.execute();
            }
        });


        queue = Volley.newRequestQueue(this);

        String user = "vua_student31";
        String p = "BbCdHgjE";
        String auth = getEncodedValues(user, p); // dnVhX3N0dWRlbnQzMTpCYkNkSGdqRQ==
        Log.d(this.toString(), "auth: " + auth);
    }

    public class WatsonQuery extends AsyncTask<Void, Integer, String> {

        private SSLContext context;
        private HttpsURLConnection connection;
        private String jsonData;


        @Override
        protected String doInBackground(Void... params) {

            // Accepts all HTTPS certs. Do NOT use in production!!!
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }};

            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (java.security.KeyManagementException e) {
                e.printStackTrace();
            } catch (java.security.NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {

                URL watsonURL = new URL("https://dal09-gateway.watsonplatform.net/instance/568/deepqa/v1/question");
                int timeoutConnection = 30000;
                connection = (HttpsURLConnection) watsonURL.openConnection();
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(timeoutConnection);
                connection.setReadTimeout(timeoutConnection);

                // Watson specific HTTP headers
                connection.setRequestProperty("X-SyncTimeout", "30");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic dnVhX3N0dWRlbnQzMTpCYkNkSGdqRQ==");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Cache-Control", "no-cache");

                OutputStream out = connection.getOutputStream();
                String query = "{\"question\": {\"questionText\": \"" + queryString + "\"}}";
                out.write(query.getBytes());
                out.close();

                Log.d(this.toString(), "CONNECTION: " + connection.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int responseCode;
            try {
                if (connection != null) {
                    responseCode = connection.getResponseCode();
                    Log.d(this.toString(), "ResponseCode: " + String.valueOf(responseCode));
                    switch(responseCode) {
                        case 200:
                            // successful HTTP response state
                            InputStream input = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                            String line;
                            StringBuilder response = new StringBuilder();
                            while((line = reader.readLine()) != null) {
                                response.append(line);
                                response.append('\r');
                            }
                            reader.close();

                            jsonData = response.toString();


                            Log.d(this.toString(), jsonData.toString());

                            break;
                        default:
                            Log.d(this.toString(), "Some error occurred...");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(jsonData != null) {
                return jsonData;
            }

//            Log.d(this.toString(), "try to send the request to Watson");
//            try {
//
//
//                String url = "https://dal09-gateway.watsonplatform.net/instance/568/deepqa/v1/question";
//                String requestBody = "{\"question\": {\"questionText\": \"" + queryString + "\"}}";
//
//                Log.d(this.toString(), "Building the JSON request");
//
//                JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                        (Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
//
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.d(this.toString(), response.toString());
//                                try {
//                                    Log.d(this.toString(), "DOODDOOD");
//                                    Log.d(this.toString(), response.toString());
////                                    JSONArray resArray = response.getJSONArray("results");
////                                    // the first is the most important within the radius
////                                    JSONObject topPlace = resArray.getJSONObject(0);
////                                    String placeName = topPlace.getString("name");
////                                    Log.d(this.toString(), placeName);
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        }, new Response.ErrorListener() {
//
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // TODO Auto-generated method stub
//
//                            }
//                        }) {
//
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//
//                        HashMap<String, String> headers = new HashMap<String, String>();
//
//                        String user = "vua_student31";
//                        String p = "BbCdHgjE";
//
//                        String auth = getEncodedValues(user, p);
//                        Log.d(this.toString(), auth);
//
//                        // Watson specific HTTP headers
//                        headers.put("X-SyncTimeout", "30");
//                        headers.put("Accept", "application/json");
//                        headers.put("Authorization", "Basic " + auth);
//                        headers.put("Content-Type", "application/json");
//                        headers.put("Cache-Control", "no-cache");
//
//                        return headers;
//                    }
//                };
//                // Add the request to the RequestQueue.
//                queue.add(jsObjRequest);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                TextView answerTextView = (TextView) findViewById(R.id.editText);
                answerTextView.setText(result.toString());
                Log.d(this.toString(), result.toString());
            }
        }
    }

    private String getEncodedValues(String user_id, String user_password) {
        String textToEncode = user_id + ":" + user_password;
        byte[] data = null;
        try {
            data = textToEncode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return base64;
    }
}







