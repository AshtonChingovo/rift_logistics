package com.logistics.riftvalley.Utilities.UploadPictures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class InternetBroadcastReceiver {

    public static Intent serviceIntent = null;

    public static void StartReportUploading(Context context) {

        Log.i("InternetBroadcast", "IN HERE");

        if(checkConnectivity(context)){

            // Create a Constraints object that defines when the task should run
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(Uploads.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager workManager = WorkManager.getInstance();

            workManager.beginUniqueWork(
                    "TAG",
                    ExistingWorkPolicy.REPLACE,
                    uploadWorkRequest
            ).enqueue();
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
