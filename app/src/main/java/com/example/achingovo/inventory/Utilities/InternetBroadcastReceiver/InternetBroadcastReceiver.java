package com.example.achingovo.inventory.Utilities.InternetBroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.achingovo.inventory.App.StockMovements.Sale.DispatchDialog;
import com.example.achingovo.inventory.Utilities.NoConnection;

public class InternetBroadcastReceiver extends BroadcastReceiver {

    public static Intent serviceIntent = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!checkConnectivity(context)){
            Intent connectionIntent = new Intent(context, NoConnection.class);
            connectionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(connectionIntent);
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

    public static void uploadPictures(){

    }

}
