package com.logistics.riftvalley.data.model.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "salesPictures")
public class PicturesDB {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @NonNull
    private int deliveryNoteDocEntry;
    @NonNull
    private int salesOrderDocEntry;
    @NonNull
    private String uri;
    @NonNull
    private String date;
    @NonNull
    private int saved;
    @NonNull
    private int uploaded;
    private String base64String;

    public PicturesDB(int deliveryNoteDocEntry, int salesOrderDocEntry, @NonNull String uri, @NonNull String date,
                      int saved, int uploaded) {
        this.deliveryNoteDocEntry = deliveryNoteDocEntry;
        this.salesOrderDocEntry = salesOrderDocEntry;
        this.uri = uri;
        this.date = date;
        this.saved = saved;
        this.uploaded = uploaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeliveryNoteDocEntry() {
        return deliveryNoteDocEntry;
    }

    public void setDeliveryNoteDocEntry(int deliveryNoteDocEntry) {
        this.deliveryNoteDocEntry = deliveryNoteDocEntry;
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    public void setUri(@NonNull String uri) {
        this.uri = uri;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }

    public int getSalesOrderDocEntry() {
        return salesOrderDocEntry;
    }

    public void setSalesOrderDocEntry(int salesOrderDocEntry) {
        this.salesOrderDocEntry = salesOrderDocEntry;
    }
}
