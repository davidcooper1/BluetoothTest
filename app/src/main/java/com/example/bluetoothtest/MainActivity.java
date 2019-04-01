package com.example.bluetoothtest;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.Manifest;

import android.util.Log;
import android.view.View;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    //private TextView temp;
    private TemperatureFetcher fetcher;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView temp = (TextView) findViewById(R.id.temp);
        temp.setText("Searching...");
        final ReentrantLock inputLock = new ReentrantLock();

        fetcher = new TemperatureFetcher();

        fetcher.setCallback(fetcher.CALLBACK_DATA_RECEIVED, new Runnable() {
            public void run() {
                temp.post(new Runnable() {
                    public void run() {
                        temp.setText(fetcher.getData());
                    }
                });
            }
        });

        fetcher.setCallback(fetcher.CALLBACK_DISCONNECT, new Runnable() {
            public void run() {
                temp.post(new Runnable() {
                    public void run() {
                        temp.setText("Device disconnected");
                    }
                });
            }
        });

        Button button = (Button)findViewById(R.id.reconnectBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread clickHandle = new Thread(new Runnable() {
                    public void run() {
                        boolean hasLock = inputLock.tryLock();
                        if (hasLock) {
                            try {
                                fetcher.connect();
                            } finally {
                                inputLock.unlock();
                            }
                        }
                    }
                });
                clickHandle.start();
            }
        });


        obtainPermissions();

    }

    private void obtainPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST
                );
            }*/
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    receiver = fetcher.getReceiver();
                    filter = fetcher.getFilter();

                    registerReceiver(receiver, filter);

                    fetcher.setCallbacksEnabled(true);
                    fetcher.connect();
                } else {
                    ((TextView) findViewById(R.id.temp)).setText("Cannot function without bluetooth privileges.");
                }
                break;
        }
    }
}
