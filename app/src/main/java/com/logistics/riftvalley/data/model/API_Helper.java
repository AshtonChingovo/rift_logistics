package com.logistics.riftvalley.data.model;

import android.os.AsyncTask;
import android.util.Log;

import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.CHECK_IN;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.IN_WAREHOUSE_ACTIVITY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_DISPATCH_ACTIVITY;

public class API_Helper implements _API_Helper {

    // reference to DataManagerPresenter
    _DataManager dataManager;

    // warehouses list
    List<Warehouses> warehouses = new ArrayList<>();

    // binLocationAbsEntryNumber is set to zero for endpoints that do not require it
    int binLocationAbsEntryNumber = 0;

    int systemNumberForFailedRequestAttempt = 0;

    public API_Helper(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    boolean loginResponseVariable;

    @Override
    public void login(Login login) {
        new LoginToSAP().execute(login);
    }

    @Override
    public void loginResponse(boolean loginResponse) {

    }

    @Override
    public void requestListOfWarehouses(int activitySource) {
        new RequestWarehouseList().execute();
    }

    @Override
    public void returnWarehouseList(List<Warehouses> warehousesList) {

    }

    @Override
    public void requestSerialNumberSystemNumber(String serialNumber, String warehouseCode, int source) {
        if(source == IN_WAREHOUSE_ACTIVITY || source == MOVE_TO_DISPATCH_ACTIVITY || source == CHECK_IN)
            new RequestSystemNumberAndBinLocationAbsEntry().execute(serialNumber);
        else
            new RequestSystemNumber().execute(serialNumber);
    }

    @Override
    public void returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(int systemNumber, int downloadedBinLocationAbsEntry) {

    }

    @Override
    public void transferSerialNumberSAP(StockTransfer stockTransferObject) {
        new PerformTransfer().execute(stockTransferObject);
    }

    @Override
    public void serialNumberTransferResponse(boolean isSuccessful) {

    }

    @Override
    public void transferSerialNumber() {

    }

    public class LoginToSAP extends AsyncTask<Login, Void, Void> {

        String responseVal;
        @Override
        protected Void doInBackground(Login... loginObject) {

            try {

                responseVal = RetrofitInstance.login(loginObject[0]);

                if(responseVal != null){
                    if(SharedPreferencesClass.writeCookie(responseVal)){
                        loginResponseVariable = true;
                        SharedPreferencesClass.writeCredentials(loginObject[0].getUserName(), loginObject[0].getPassword(), loginObject[0].CompanyDB);
                    }
                }

                // Callback to DataManager
                dataManager.loginResponse(loginResponseVariable);

            }
            catch (Exception e){
                dataManager.loginResponse(false);
                return null;
            }

            return null;

        }

    }

    public class RequestWarehouseList extends AsyncTask<Void, Void, Void> {

        String responseVal;

        @Override
        protected Void doInBackground(Void... voids) {

            responseVal = RetrofitInstance.getWarehouses(SharedPreferencesClass.getCookie());

            if(responseVal != null){

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(responseVal);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject warehouseObject = jsonArray.getJSONObject(i);
                        warehouses.add(new Warehouses(warehouseObject.getString("WarehouseName"), warehouseObject.getString("WarehouseCode")));
                    }

                } catch (JSONException e) {
                    Log.i("Locations", "Error: " + e.toString());
                    e.printStackTrace();
                    dataManager.returnWarehouseList(null);
                    return null;
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(warehouses != null){
                // Callback to DataManager
                dataManager.returnWarehouseList(warehouses);
            }
        }
    }

    // get system number of the passed serial number
    public class RequestSystemNumber extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;

        @Override
        protected Void doInBackground(String... values) {

            // Get scanned carton's data with system number i.e json
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){

                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("ScanningProcess", "ScanNewInventory Error: " + e.toString());
                    // return zero to show something went wrong
                    dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(0, binLocationAbsEntryNumber);
                    return null;
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(Integer.parseInt(downloadedSystemNumber) != 0){
                // Callback to DataManager
                dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(Integer.parseInt(downloadedSystemNumber), binLocationAbsEntryNumber);
            }
        }
    }

    // get system number and binLocationAbsEntry of the passed serial number
    public class RequestSystemNumberAndBinLocationAbsEntry extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;
        String downloadedBinLocationAbsEntry;

        @Override
        protected Void doInBackground(String... values) {

            // Get scanned carton's system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        Log.d("BinLocationAbsEntry", " location " + SharedPreferencesClass.getStackLocation());

                        // Get scanned carton's data with BinLocationAbsEntry
                        downloadedBinLocationAbsEntry = RetrofitInstance.getBinLocationAbsEntryNumber(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getStackLocation());

                        if(downloadedBinLocationAbsEntry == null)
                            return null;

                        jsonObject = new JSONObject(downloadedBinLocationAbsEntry);
                        jsonArray = jsonObject.getJSONArray("value");

                        if(jsonArray.length() == 0)
                            return null;

                        // Extract scanned carton's BinLocationAbsEntry
                        downloadedBinLocationAbsEntry = jsonArray.getJSONObject(0).get("AbsEntry").toString();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("ScanningProcess", "ScanNewInventory Error: " + e.toString());
                    dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(0, binLocationAbsEntryNumber);
                    return null;
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(Integer.parseInt(downloadedSystemNumber), Integer.parseInt(downloadedBinLocationAbsEntry));
        }
    }

    public class PerformTransfer extends AsyncTask<StockTransfer, Void, Void> {

        boolean stockTransferResponse = false;

        @Override
        protected Void doInBackground(StockTransfer... stockTransferObject) {

            stockTransferResponse = RetrofitInstance.stockTransfer(SharedPreferencesClass.getCookie(), stockTransferObject[0]);

            Log.d("PerformTransfer", " response -- " + stockTransferResponse);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Callback to DataManager
            dataManager.serialNumberTransferResponse(stockTransferResponse);
        }
    }

}
