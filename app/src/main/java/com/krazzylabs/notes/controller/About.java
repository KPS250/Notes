package com.krazzylabs.notes.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.krazzylabs.notes.BuildConfig;
import com.krazzylabs.notes.R;

public class About extends AppCompatActivity {

    TextView textView_companyName, textView_appVersion,textView_companyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textView_companyName = (TextView) findViewById(R.id.textView_companyName);
        textView_companyEmail = (TextView) findViewById(R.id.textView_companyEmail);
        textView_appVersion = (TextView) findViewById(R.id.textView_appVersion);

        textView_companyName.setText(getString(R.string.companyName));
        textView_companyEmail.setText(getString(R.string.email)+ getString(R.string.companyEmail));
        textView_appVersion.setText(getString(R.string.version)+ BuildConfig.VERSION_NAME);
    }
}
