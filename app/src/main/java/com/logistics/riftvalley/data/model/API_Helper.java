package com.logistics.riftvalley.data.model;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryDocument;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.StockDisposalsEntity;
import com.logistics.riftvalley.ui.StockMovements.Sale.Dispatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class API_Helper implements _API_Helper {

    // reference to DataManagerPresenter
    _DataManager dataManager;

    // warehouses list
    List<Warehouses> warehouses = new ArrayList<>();

    // StockDisposalsEntity List
    List<StockDisposalsEntity> DocumentLines = new ArrayList<>();

    // binLocationAbsEntryNumber is set to zero for endpoints that do not require it
    int binLocationAbsEntryNumber = 0;

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
        if(source == SCAN_NEW_INVENTORY || source == IN_WAREHOUSE_ACTIVITY || source == MOVE_TO_DISPATCH_ACTIVITY || source == CHECK_IN || source == MOVE_TO_FUMIGATION_SALES || source == MOVE_TO_DISPATCH_SALES)
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

    @Override
    public void stockDisposal(DocumentLines documentLines) {
        new StockDisposal().execute(documentLines);
    }

    @Override
    public void stockDisposalResponse(boolean isSuccessful) {

    }

    @Override
    public void requestSalesOrderList() {
        new GetSalesOrdersList().execute();
    }

    @Override
    public void salesOrderListResponse(String salesOrdersJsonString) {

    }

    @Override
    public void doesSerialNumberExistInSAP(String serialNumber) {
        new FindSerialNumberSAP().execute(serialNumber);
    }

    @Override
    public void setShippingCaseNumberToSerialNumber(String serialNumber, String shippingCaseNumber) {
        new AssignShippingCaseNumber().execute(serialNumber, shippingCaseNumber);
    }

    @Override
    public void salesOrderDispatch(boolean isSuccessful) {

    }

    @Override
    public void dispatchProcesses(boolean isSuccessful, String message) {

    }

    @Override
    public void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray) {

    }

    @Override
    public void dispatchGoods(List<SalesOrdersDocumentLines> documentLines) {
        new CreateDeliveryNote().execute(documentLines);
    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {

    }

    @Override
    public void getLotNumbers() {
        new RequestLotNumbers().execute();
    }

    @Override
    public void returnLotNumbers(String lotNumberJson) {

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
                Log.d("loginError", " ERROR :: " + e.toString());
                loginResponseVariable = false;
                return null;
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            dataManager.loginResponse(loginResponseVariable);

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
                    warehouses = null;
                    return null;
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.returnWarehouseList(warehouses);
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
            else {
                // return zero to show something went wrong
                dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(0, binLocationAbsEntryNumber);
            }
        }
    }

    // get system number and binLocationAbsEntry of the passed serial number
    public class RequestSystemNumberAndBinLocationAbsEntry extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;
        String downloadedBinLocationAbsEntry;

        int systemNumber = 0;
        int binLocationAbsEntryNumber = 0;

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

                        systemNumber = Integer.parseInt(downloadedSystemNumber);
                        binLocationAbsEntryNumber = Integer.parseInt(downloadedBinLocationAbsEntry);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    systemNumber = 0;
                    binLocationAbsEntryNumber = 0;
                    Log.i("ScanningProcess", "ScanNewInventory Error: " + e.toString());
                    return null;
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(Integer.parseInt(downloadedSystemNumber) > 0)
                dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(systemNumber, binLocationAbsEntryNumber);
            else
                dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(0, binLocationAbsEntryNumber);

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

    public class StockDisposal extends AsyncTask<DocumentLines, Void, Void> {

        boolean stockDisposed = false;

        @Override
        protected Void doInBackground(DocumentLines... documentLines) {

            stockDisposed = RetrofitInstance.stockDisposal(SharedPreferencesClass.getCookie(), documentLines[0]);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.stockDisposalResponse(stockDisposed);
        }
    }

    public class GetSalesOrdersList extends AsyncTask<Void, Void, Void> {

        String responseVal;

        @Override
        protected Void doInBackground(Void... voids) {

            responseVal = RetrofitInstance.getSalesOrders(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getWarehouseCode().trim().toUpperCase());

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.salesOrderListResponse(responseVal);
        }
    }

    public class FindSerialNumberSAP extends AsyncTask<String, Void, Void> {

        String OKAY = "OKAY";
        String EXISTS = "Serial Number not found in the system";

        JSONArray serialNumberResponse;
        JSONObject jsonResponse;

        @Override
        protected Void doInBackground(String... values) {

            try {

                // value[0] = serialNumber
                jsonResponse = new JSONObject(RetrofitInstance.getSerialNumber(SharedPreferencesClass.getCookie(), values[0]));
                serialNumberResponse = jsonResponse.getJSONArray("value");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.dispatchProcesses(serialNumberResponse.length() > 0, serialNumberResponse.length() > 0 ? OKAY : EXISTS);
        }
    }

    public class AssignShippingCaseNumber extends AsyncTask<String, Void, Void>{

        JSONArray serialNumberResponse = null;
        JSONObject jsonResponse = null;

        boolean isSuccessful;

        @Override
        protected Void doInBackground(String... data) {

            isSuccessful = RetrofitInstance.setShippingCaseNumber(SharedPreferencesClass.getCookie(), data[0], Integer.valueOf(data[1].trim()));

            if(isSuccessful){

                try {

                    // value[0] = serialNumber
                    jsonResponse = new JSONObject(RetrofitInstance.getSerialNumber(SharedPreferencesClass.getCookie(), data[0]));
                    serialNumberResponse = jsonResponse.getJSONArray("value");

                } catch (JSONException e) {
                    e.printStackTrace();
                    isSuccessful = false;
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("Shipping", isSuccessful + "");
            dataManager.shippingCaseNumber(isSuccessful, "", serialNumberResponse);
        }
    }

    // create DeliveryNote and close Sales order
    public class CreateDeliveryNote extends AsyncTask<List<SalesOrdersDocumentLines>, Void, Void> {

        boolean deliveryCreated;

        @Override
        protected Void doInBackground(List<SalesOrdersDocumentLines>... salesOrdersDocumentLines) {

            deliveryCreated = RetrofitInstance.createDeliveryNote(SharedPreferencesClass.getCookie(), new DeliveryDocument(SharedPreferencesClass.getSalesOrderCardCode(), salesOrdersDocumentLines[0]));

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.dispatchGoodsResponse(deliveryCreated, "");
        }
    }

    public class RequestLotNumbers extends AsyncTask<Void, Void, Void> {

        String lotNumbers;

        @Override
        protected Void doInBackground(Void... aVoids) {

            lotNumbers = RetrofitInstance.getLotNumbers(SharedPreferencesClass.getCookie());

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.returnLotNumbers(lotNumbers);
        }
    }

}
