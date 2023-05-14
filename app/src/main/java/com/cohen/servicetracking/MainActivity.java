package com.cohen.servicetracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton start;
    private ExtendedFloatingActionButton stop;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        info = findViewById(R.id.info);

        start.setOnClickListener(v -> startService());
        stop.setOnClickListener(v -> stopService());

        //MyReminder.startReminder(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //IntentFilter intentFilter = new IntentFilter(TapService.BROADCAST_NEW_);
        //LocalBroadcastManager.getInstance(this).registerReceiver(myRadio, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(myRadio);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            if (getIntent().getAction().equals(TapService.MAIN_ACTION)) {
                // came from notification
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startService() {
        Intent intent = new Intent(this, TapService.class);
        intent.setAction(TapService.START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            // or
            //ContextCompat.startForegroundService(this, startIntent);
        } else {
            startService(intent);
        }
    }

    private void stopService() {
        Intent intent = new Intent(this, TapService.class);
        intent.setAction(TapService.STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            // or
            //ContextCompat.startForegroundService(this, startIntent);
        } else {
            startService(intent);
        }
    }
}