package com.example.bluetoothtest;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.bluetooth.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.util.Log;
import android.view.View;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    //private TextView temp;
    private TemperatureFetcher fetcher;
    private BroadcastReceiver receiver;
    private IntentFilter filter;

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

        receiver = fetcher.getReceiver();
        filter = fetcher.getFilter();

        registerReceiver(receiver, filter);

        fetcher.setCallbacksEnabled(true);
        fetcher.connect();

    }



    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
