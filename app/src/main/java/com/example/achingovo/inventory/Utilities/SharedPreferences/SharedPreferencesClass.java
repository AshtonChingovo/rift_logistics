package com.example.achingovo.inventory.Utilities.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.achingovo.inventory.R;

public class SharedPreferencesClass {

    public static Context context;
    public static SharedPreferences sharedPref;
    public static Editor editor;

    public static void setSharePreference(){
        sharedPref = context.getSharedPreferences(context.getString(R.string.sharedPrefsName), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public static void writeCredentials(String username, String password, String companyDB){

        editor.putString(context.getString(R.string.userNameSharedPreferences), username);
        editor.putString(context.getString(R.string.passwordSharedPreferences), password);
        editor.putString(context.getString(R.string.companyDBSharedPreferences), companyDB);
        editor.apply();

    }

    public static String getCookie(){

        return sharedPref.getString(context.getString(R.string.cookieSharedPreferences), null);

    }

    public static boolean writeCookie(String responseVal){

        if(responseVal != null){
            editor.putString(context.getString(R.string.cookieSharedPreferences), responseVal);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    // Active warehouse * written to Shared Preference when selected in any warehouse locations list *
    public static boolean writeWarehouseCode(String warehouseLocation){

        if(warehouseLocation != null){
            editor.putString(context.getString(R.string.warehouseLocationSharedPreferences), warehouseLocation);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    public static String getWarehouseCode(){

        return sharedPref.getString(context.getString(R.string.warehouseLocationSharedPreferences), null);

    }

    // Active stack location * written to Shared Preference when entered by user * i.e InWarehouse
    public static boolean writeStackLocation(String stackLocation){

        if(stackLocation != null){
            editor.putString(context.getString(R.string.stackLocationSharedPreferences), stackLocation);
            editor.apply();
            return true;
        }
        else
            return false;

    }

    public static String getStackLocation(){

        return sharedPref.getString(context.getString(R.string.stackLocationSharedPreferences), null);

    }

    // Active Sales Order Data
    public static void writeSalesOrderData(String cardCode, int salesOrderQuantity, String itemCode, int docEntry, String customerName){

        editor.putString(context.getString(R.string.salesOrderCardCodeSharedPreferences), cardCode);
        editor.putInt(context.getString(R.string.salesOrderQuantitySharedPreferences), salesOrderQuantity);
        editor.putString(context.getString(R.string.salesOrderItemCodeSharedPreferences), itemCode);
        editor.putInt(context.getString(R.string.salesOrderDocEntrySharedPreferences), docEntry);
        editor.putString(context.getString(R.string.salesOrderCustomerNameSharedPreferences), customerName);
        editor.apply();

    }

    public static int getSalesOrderQuantity(){
        return sharedPref.getInt(context.getString(R.string.salesOrderQuantitySharedPreferences), 0);
    }

    public static String getSalesOrderItemCode(){
        return sharedPref.getString(context.getString(R.string.salesOrderItemCodeSharedPreferences), null);
    }

    public static String getSalesOrderCardCode(){
        return String.valueOf(sharedPref.getString(context.getString(R.string.salesOrderCardCodeSharedPreferences), null));
    }

    public static int getSalesOrderDocEntry(){
        return sharedPref.getInt(context.getString(R.string.salesOrderDocEntrySharedPreferences), 0);
    }

    public static String getSalesOrderCustomerName(){
        return sharedPref.getString(context.getString(R.string.salesOrderCustomerNameSharedPreferences), null);
    }

}
