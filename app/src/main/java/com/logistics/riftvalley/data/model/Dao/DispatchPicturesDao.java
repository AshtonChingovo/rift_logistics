package com.logistics.riftvalley.data.model.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.logistics.riftvalley.data.model.Entity.DispatchPictures;

import java.util.List;

@Dao
public interface DispatchPicturesDao {

    @Insert
    long[] insertReportPictures(List<DispatchPictures> DispatchPictures);

    @Query("SELECT * FROM DispatchPictures WHERE uploaded = 0 AND salesOrderCustomer = :salesOrderCustomer ORDER BY id DESC")
    List<DispatchPictures> getDispatchPictures(String salesOrderCustomer);

    @Query("SELECT * FROM DispatchPictures WHERE uploaded = 0 AND salesOrderCustomer = :salesOrderCustomer ORDER BY id DESC")
    LiveData<List<DispatchPictures>> getDispatchPicturesLive(String salesOrderCustomer);

    @Query("SELECT * FROM DispatchPictures WHERE uploaded = 0 ORDER BY id DESC")
    LiveData<List<DispatchPictures>> getAllDispatchPicturesLive();

    @Query("SELECT * FROM DispatchPictures WHERE uploaded = 0 ORDER BY id DESC")
    List<DispatchPictures> getAllDispatchPictures();

}
