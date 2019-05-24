package com.example.achingovo.inventory.Utilities.UploadPictures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetBroadcastReceiver extends BroadcastReceiver {

    public static Intent serviceIntent = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("InternetBroadcast", "IN HERE");

        if(checkConnectivity(context)){

            Log.i("InternetBroadcast", "IN HERE");
            serviceIntent = new Intent(context, Uploads.class);
            context.startService(serviceIntent);

        }
    }

    public static void StartReportUploading(Context context){

        if(checkConnectivity(context)){
            Log.i("InternetBroadcast", "IN HERE");
            serviceIntent = new Intent(context, Uploads.class);
            context.startService(serviceIntent);
        }

    }

    public static boolean checkConnectivity(Context context){

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected == true)
            return true;
        else{
            serviceIntent = null;
            return false;
        }

    }

}
