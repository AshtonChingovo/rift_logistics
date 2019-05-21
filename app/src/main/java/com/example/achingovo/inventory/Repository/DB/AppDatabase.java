package com.example.achingovo.inventory.Repository.DB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.achingovo.inventory.Repository.Dao.DispatchPicturesDao;
import com.example.achingovo.inventory.Repository.Entity.DispatchPictures;


@Database(entities = {DispatchPictures.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DispatchPicturesDao dispatchPicturesDao();

    public static volatile AppDatabase DATABASE;

    public static AppDatabase getDatabase(Context context){

        if(DATABASE == null){
            DATABASE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "logistics_database").build();
        }

        return DATABASE;

    }
}

