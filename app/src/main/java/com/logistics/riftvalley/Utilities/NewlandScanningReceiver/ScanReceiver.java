package com.logistics.riftvalley.Utilities.NewlandScanningReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScanReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Scan", "Got something: " + intent.getStringExtra("SCAN_BARCODE1"));

    }
}
