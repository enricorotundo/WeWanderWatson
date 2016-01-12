package com.group5.wewander;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

public class WatsonActivity extends AppCompatActivity {

    private WatsonQuery query;
    private String queryS = "";
    private String answer = "";
    private boolean haveQuestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String placeName = getIntent().getExtras().getString("placeName");
        if (placeName != null) {
            Log.d(this.toString(), placeName);
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText("Ask something about " + placeName + "...");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

        public void onClick (View v){
            if (!haveQuestion) {
                haveQuestion = true;
                EditText question = (EditText) getActivity().findViewById(R.id.editText);
                if (question.getText() != null) {
                    queryS = question.getText().toString();
                }
                query = new WatsonQuery();
                query.execute();
            }

        }

    private class WatsonQuery extends AsyncTask<Void, Integer, String> {

        private SSLContext context;
        private HttpsURLConnection connection;
        private String jsonData;

        @Override
        protected String openConnection(Void... ignore) {

            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (java.security.KeyManagementException e) {
                e.printStackTrace();
            } catch (java.security.NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {

                URL watsonURL = new URL(getString(R.string.user_watson_server_instance));
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
                connection.setRequestProperty("Authorization", "Basic " + getEncodedValues(getString(R.string.user_id), getString(R.string.user_password)));
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Cache-Control", "no-cache");

                String query = "{\"question\": {\"questionText\": \"" + queryS + "\"}}";


            } catch (IOException e) {
                e.printStackTrace();
            }

            int responseCode;
            try {
                if (connection != null) {
                    responseCode = connection.getResponseCode();
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

                            break;
                        default:

                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(jsonData != null) {
                return jsonData;
            }

            return null;
        }

    }


}

