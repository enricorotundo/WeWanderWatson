package com.group5.wewander;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

public class WatsonActivity extends AppCompatActivity {

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

}
