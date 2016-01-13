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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WatsonActivity extends AppCompatActivity {

    // Watson headers
    private String url = "https://dal09-gateway.watsonplatform.net/instance/568/deepqa/v1/question";
    public RequestQueue queue;

    private String queryString = "try";
    private String answer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(this);

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

                // TODO: trigger the query
                new WatsonQuery().execute(url);

            }
        });

    }

    class WatsonQuery extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... arg0) {

            try {

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // print response
                                Log.d("Response", "HERE YOU ARE:");
                                Log.d("Response", response.toString());

                                // parse the answer to JSON
                                try {
                                    JSONObject mainObject = new JSONObject(response);
                                    JSONObject question = mainObject.getJSONObject("question");
                                    JSONArray uniObject = question.getJSONArray("evidencelist");
                                    JSONObject obj = uniObject.getJSONObject(0);
//                                    JSONObject uniName = obj.getString("text");
//                                    response = uniName.toString();
                                    response = obj.getString("text");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                TextView answerTextView = (TextView) findViewById(R.id.textView);
                                answerTextView.setText(response.toString());
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        EditText text = (EditText) findViewById(R.id.editText);
                        String requestBody = "{\"question\": {\"questionText\": \"" + text.getText().toString() + "\"}}";
                        return requestBody.getBytes();
                    }

                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("domain", "https://dal09-gateway.watsonplatform.net/instance/568/deepqa/v1/question");

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        HashMap<String, String> headers = new HashMap<String, String>();

                        String auth = "dnVhX3N0dWRlbnQzMTpCYkNkSGdqRQ==";

                        // Watson specific HTTP headers
                        headers.put("X-SyncTimeout", "30");
                        headers.put("Accept", "application/json");
                        headers.put("Authorization", "Basic " + auth);
                        headers.put("Content-Type", "application/json");
                        headers.put("Cache-Control", "no-cache");

                        return headers;
                    }
                };
                System.out.println("StringRequest body and headers--------------------------------------------------------------->");
                System.out.println(postRequest.getBody().toString());
                System.out.println(postRequest.getHeaders());
                System.out.println("<---------------------------------------------------------------------------------------------");
                queue.add(postRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

    }
}







