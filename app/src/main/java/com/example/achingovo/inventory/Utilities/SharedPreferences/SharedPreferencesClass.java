package com.example.achingovo.inventory.Utilities.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.achingovo.inventory.R;

public class SharedPreferencesClass {

    public static Context context;
    public static SharedPreferences sharedPref;
    public static Editor editor;

    public static void setSharePreference(){
        sharedPref = context.getSharedPreferences(context.getString(R.string.sharedPrefsName), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public static void writeCredentials(){

        editor.putString(context.getString(R.string.cookie), "");
        editor.putString(context.getString(R.string.cookie), "");
        editor.putString(context.getString(R.string.cookie), "");

    }

    public static String getCookie(){

        return sharedPref.getString(context.getString(R.string.cookie), null);

    }

    public static boolean writeCookie(String responseVal){

        if(responseVal != null){
            editor.putString(context.getString(R.string.cookie), responseVal);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    // Active warehouse * written to Shared Preference when selected in any warehouse locations list *
    public static boolean writeWarehouseCode(String warehouseLocation){

        if(warehouseLocation != null){
            editor.putString(context.getString(R.string.warehouseLocation), warehouseLocation);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    public static String getWarehouseCode(){

        return sharedPref.getString(context.getString(R.string.warehouseLocation), null);

    }

    // Active stack location * written to Shared Preference when entered by user *
    public static boolean writeStackLocation(String stackLocation){

        if(stackLocation != null){
            editor.putString(context.getString(R.string.stackLocation), stackLocation);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    public static String getStackLocation(){

        return sharedPref.getString(context.getString(R.string.stackLocation), null);

    }

}
