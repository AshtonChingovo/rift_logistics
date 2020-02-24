package com.logistics.riftvalley.data.model.DB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.logistics.riftvalley.data.model.Dao.PicturesDao;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;


@Database(entities = {PicturesDB.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PicturesDao picturesDao();

    public static volatile AppDatabase DATABASE;

    public static AppDatabase getDatabase(Context context){

        if(DATABASE == null){
            DATABASE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "logistics_database").build();
        }

        return DATABASE;

    }
}

