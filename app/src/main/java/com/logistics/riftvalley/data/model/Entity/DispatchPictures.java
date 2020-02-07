package com.logistics.riftvalley.data.model.Entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "DispatchPictures")
public class DispatchPictures {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @NonNull
    private String uri;
    @NonNull
    private String date;
    @NonNull
    private String salesOrderCustomer;
    @NonNull
    private int uploaded;

    public DispatchPictures(@NonNull String uri, @NonNull String date, @NonNull String salesOrderCustomer, @NonNull int uploaded) {
        this.uri = uri;
        this.date = date;
        this.salesOrderCustomer = salesOrderCustomer;
        this.uploaded = uploaded;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    public void setUri(@NonNull String uri) {
        this.uri = uri;
    }

    @NonNull
    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(@NonNull int uploaded) {
        this.uploaded = uploaded;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getSalesOrderCustomer() {
        return salesOrderCustomer;
    }

    public void setSalesOrderCustomer(@NonNull String salesOrderCustomer) {
        this.salesOrderCustomer = salesOrderCustomer;
    }
}
