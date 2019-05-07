package com.example.achingovo.inventory.Repository.DB;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.achingovo.inventory.Repository.Dao.WarehousesDao;
import com.example.achingovo.inventory.Repository.Entity.Warehouses;

//@Database(entities = {Warehouses.class}, version = 1, exportSchema = false)
    public abstract class AppDatabase extends RoomDatabase {
        /*public abstract WarehousesDao warehousesDao();

        public static volatile AppDatabase DATABASE;

        public static AppDatabase getDatabase(Context context){

            if(DATABASE == null){
                DATABASE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "rvc_database").build();
            }

            return DATABASE;
        }*/
}
