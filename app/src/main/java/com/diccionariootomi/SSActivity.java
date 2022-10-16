package com.diccionariootomi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssactivity);

        int timeSplash = 500;
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SSActivity.this, MainActivity.class));
            finish();
        }, timeSplash);
    }
}