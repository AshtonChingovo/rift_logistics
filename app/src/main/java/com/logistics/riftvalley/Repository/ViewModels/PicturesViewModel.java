package com.logistics.riftvalley.Repository.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Entity.DispatchPictures;

import java.util.List;

public class PicturesViewModel extends AndroidViewModel {

    // set from the splash screen page
    public static Integer agentId;
    String growerType;
    private Application application;
    private LiveData<List<DispatchPictures>> dispatchPicture;
    private LiveData<List<DispatchPictures>> allDispatchPicture;

    public PicturesViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<DispatchPictures>> getDispatchPicturesLiveData(String customerName) {

        dispatchPicture = AppDatabase.getDatabase(application).dispatchPicturesDao().getDispatchPicturesLive(customerName);

        return dispatchPicture;

    }

    public LiveData<List<DispatchPictures>> getAllDispatchPicturesLiveData() {

        allDispatchPicture = AppDatabase.getDatabase(application).dispatchPicturesDao().getAllDispatchPicturesLive();

        return dispatchPicture;

    }
}
