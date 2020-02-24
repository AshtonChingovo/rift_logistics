package com.logistics.riftvalley.Repository.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;

import java.util.List;

public class PicturesViewModel extends AndroidViewModel {

    // set from the splash screen page
    public static Integer agentId;
    String growerType;
    private Application application;
    private LiveData<List<PicturesDB>> dispatchPicture;
    private LiveData<List<PicturesDB>> allDispatchPicture;

    public PicturesViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<PicturesDB>> getDispatchPicturesLiveData(String customerName) {

        // dispatchPicture = AppDatabase.getDatabase(application).picturesDao().getDispatchPicturesLive(customerName);

        return dispatchPicture;

    }

    public LiveData<List<PicturesDB>> getAllDispatchPicturesLiveData() {

        // allDispatchPicture = AppDatabase.getDatabase(application).picturesDao().getAllDispatchPicturesLive();

        return dispatchPicture;

    }
}
