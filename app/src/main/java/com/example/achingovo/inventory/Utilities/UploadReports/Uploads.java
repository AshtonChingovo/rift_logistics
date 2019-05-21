package com.example.achingovo.inventory.Utilities.UploadReports;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.achingovo.inventory.Repository.DB.AppDatabase;
import com.example.achingovo.inventory.Repository.Entity.DispatchPictures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Uploads extends IntentService {

    String userType;
    AppDatabase database;
    public static Map<String, String> apiHeaders = new HashMap<>();
    public static Map<String, String> picturesAPIHeaders = new HashMap<>();

    List<DispatchPictures> pictures;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.beeching.rvc.Utilities.InternetBroadcastReceiver.action.FOO";
    private static final String ACTION_BAZ = "com.example.beeching.rvc.Utilities.InternetBroadcastReceiver.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.beeching.rvc.Utilities.InternetBroadcastReceiver.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.beeching.rvc.Utilities.InternetBroadcastReceiver.extra.PARAM2";

    public Uploads() {
        super("ReportUploadService");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i("ARRAYLIST", " Service started |-|-|-| ");
        database = AppDatabase.getDatabase(getApplicationContext());
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @see IntentService
     */
    // TODO: Customize helper method2
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Uploads.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, Uploads.class);

        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        database = AppDatabase.getDatabase(getApplicationContext());



    }

    // Get visit Id for uploaded reports i.e where status == 1
    public void getValidSchedules() {



    }

    public boolean uploadImages(int visitId){

        return true;

    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
