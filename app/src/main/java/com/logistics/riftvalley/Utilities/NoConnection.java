package com.logistics.riftvalley.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.logistics.riftvalley.R;

public class NoConnection extends AppCompatActivity {

    IntentFilter internetFilter;
    BroadcastReceiver internetReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection);

        // Internet receiver
        internetReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(isConnected){
                    finish();
                }
            }
        };

        internetFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(internetReceiver, internetFilter);

    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

}
