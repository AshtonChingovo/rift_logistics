package com.logistics.riftvalley.data.model.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.logistics.riftvalley.data.model.Entity.PicturesDB;

import java.util.List;

@Dao
public interface PicturesDao {

    @Insert
    long[] insertPictures(List<PicturesDB> pictureList);

    @Insert
    long insertOnePicture(PicturesDB picture);

    @Update
    int updatePicture(PicturesDB picture);

    @Query("UPDATE salesPictures SET deliveryNoteDocEntry = :deliveryNoteDocEntry WHERE salesOrderDocEntry = :salesDocEntry")
    int updatePictureDeliveryNoteNumber(int salesDocEntry, int deliveryNoteDocEntry);

    @Query("SELECT * FROM salesPictures WHERE salesOrderDocEntry = :docEntry ORDER BY id DESC")
    List<PicturesDB> getPicturesTakenAlreadyUsingDeliveryNoteDocEntry(int docEntry);

    @Query("SELECT * FROM salesPictures WHERE deliveryNoteDocEntry = :docEntry AND salesOrderDocEntry = :salesOrderDocEntry ORDER BY id DESC")
    List<PicturesDB> getPicturesTakenAlreadyUsingDeliveryNoteAndSalesOrderDocEntry(int docEntry, int salesOrderDocEntry);

    @Delete
    void deletePicture(PicturesDB picture);

    @Query("SELECT * FROM salesPictures WHERE uploaded = 0 AND saved = 1 ORDER BY id DESC")
    List<PicturesDB> getPicturesNotUploaded();

    @Query("SELECT COUNT(uploaded) FROM salesPictures WHERE deliveryNoteDocEntry = :docEntry AND uploaded = 1")
    int getPicturesUploadedCount(int docEntry);

    @Query("SELECT COUNT(uploaded) FROM salesPictures WHERE deliveryNoteDocEntry = :docEntry AND saved = 1")
    int getPicturesSavedTotal(int docEntry);

    @Query("SELECT COUNT(saved) FROM salesPictures WHERE salesOrderDocEntry = :docEntry AND saved = 1")
    int getDispatchPicturesCount(int docEntry);

}
