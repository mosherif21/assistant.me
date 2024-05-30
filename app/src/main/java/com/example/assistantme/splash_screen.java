package com.example.assistantme;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.siddharthks.bubbles.FloatingBubblePermissions;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_xml);
        getSupportActionBar().hide();
           Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(splash_screen.this,MainActivity.class);
                    startActivity(intent);
                }
            },2000);
    }


}