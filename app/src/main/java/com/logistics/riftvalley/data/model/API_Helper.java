package com.logistics.riftvalley.data.model;

import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Dao.PicturesDao;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.Entity.SerialNumbersPatchObject;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryDocument;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class API_Helper implements _API_Helper {

    // reference to DataManagerPresenter
    _DataManager dataManager;

    Context context;

    // warehouses list
    List<Warehouses> warehouses = new ArrayList<>();

    // pictures list
    List<PicturesDB> pictures = new ArrayList<>();

    // picture object
    PicturesDB picture;

    // binLocationAbsEntryNumber is set to zero for endpoints that do not require it
    int binLocationAbsEntryNumber = 0;

    // the identity of the activity that requested data
    int source;

    com.logistics.riftvalley.data.model.StockDisposals.DocumentLines documentLinesStockDisposal;
    com.logistics.riftvalley.data.model.GoodReceipt.DocumentLines documentLinesLinesGoodsReceipt;

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

        this.source = source;

        if(source == SCAN_NEW_INVENTORY || source == IN_WAREHOUSE_ACTIVITY || source == MOVE_TO_DISPATCH_ACTIVITY || source == CHECK_IN
                || source == MOVE_TO_FUMIGATION_SALES || source == MOVE_TO_DISPATCH_SALES || source == GRADE_RECLASSIFICATION)
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
    public void requestDeliveryNotesList(Context context) {
        this.context = context;
        new GetDeliveryNotesList().execute();
    }

    @Override
    public void salesOrderListResponse(String salesOrdersJsonString) {

    }

    @Override
    public void deliveryNotesResponse(List<DeliveryNote> deliveryNotesList) {

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

    @Override
    public void reclassifyGrade(com.logistics.riftvalley.data.model.StockDisposals.DocumentLines documentLines,
                                com.logistics.riftvalley.data.model.GoodReceipt.DocumentLines documentLinesLinesGoodsReceipt) {
        this.documentLinesStockDisposal = documentLines;
        this.documentLinesLinesGoodsReceipt = documentLinesLinesGoodsReceipt;

        new GradeReclassification().execute();

    }

    @Override
    public void reclassifyResult(boolean isSuccessful) {

    }

    @Override
    public void getPictures(Context context) {
        this.context = context;
        new GetAlreadyTakenPictures().execute();
    }

    @Override
    public void picturesList(List<PicturesDB> pictures) {
        this.context = context;
    }

    @Override
    public void picturesUpdate(Context context, List<PicturesDB> pictures) {
        this.context = context;
        new UpdatePictures().execute();
    }

    @Override
    public void picturesSave(Context context, List<PicturesDB> pictures) {
        this.context = context;
        new SavePictures().execute();
    }

    @Override
    public void isPicturesOperationSuccessful(boolean isSuccessful, int operationId) {

    }

    @Override
    public void deleteImage(PicturesDB picture) {
        this.picture = picture;
        new DeletePicture().execute();
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

        int systemNumber = 0;

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

                    // set the static ItemCode to the one retrieved from the end point
                    ITEM_CODE = jsonArray.getJSONObject(0).get("ItemCode").toString();

                    // set the serial number id
                    SERIAL_NUMBER_ID = jsonArray.getJSONObject(0).getInt("DocEntry");

                    systemNumber = Integer.parseInt(downloadedSystemNumber);


                } catch (JSONException e) {
                    e.printStackTrace();
                    systemNumber = 0;
                    Log.i("ScanningProcess", "ScanNewInventory Error: " + e.toString());
                    return null;
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(systemNumber, binLocationAbsEntryNumber);
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

                    // set the static ItemCode to the one retrieved from the end point
                    ITEM_CODE = jsonArray.getJSONObject(0).get("ItemCode").toString();

                    // set the serial number id
                    SERIAL_NUMBER_ID = jsonArray.getJSONObject(0).getInt("DocEntry");

                    if(jsonArray.length() != 0){

                        Log.d("BinLocationAbsEntry", " location " + SharedPreferencesClass.getStackLocation());

                        // if request is coming from Grade Reclassifications set the stack location to the Location field in the returned JSON
                        if(source == GRADE_RECLASSIFICATION)
                            SharedPreferencesClass.writeStackLocation(jsonArray.getJSONObject(0).getString("Details"));

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
        boolean patchSerialNumberDetails = false;

        @Override
        protected Void doInBackground(StockTransfer... stockTransferObject) {

            // transfer stock
            stockTransferResponse = RetrofitInstance.stockTransfer(SharedPreferencesClass.getCookie(), stockTransferObject[0]);

            // patch the serial # details
            patchSerialNumberDetails = RetrofitInstance.patchSerialNumberDetails(
                    SharedPreferencesClass.getCookie(),
                    SERIAL_NUMBER_ID,
                    new SerialNumbersPatchObject(
                            SharedPreferencesClass.getWarehouseCode(),
                            SharedPreferencesClass.getStackLocation()
                    )
            );

            Log.d("PerformTransfer", " response -- " + patchSerialNumberDetails);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Callback to DataManager
            dataManager.serialNumberTransferResponse(patchSerialNumberDetails);
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

        String jsonResponseString;

        @Override
        protected Void doInBackground(Void... voids) {

            jsonResponseString = RetrofitInstance.getSalesOrders(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getWarehouseCode().trim().toUpperCase());

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("SalesList", " API_Helper jString : " + jsonResponseString);

            dataManager.salesOrderListResponse(jsonResponseString);
        }
    }

    public class GetDeliveryNotesList extends AsyncTask<Void, Void, Void> {

        String jsonResponseString;

        List<DeliveryNote> deliveryNotesList = new ArrayList<>();

        boolean isSuccessful;

        @Override
        protected Void doInBackground(Void... voids) {

            PicturesDao picturesDao = AppDatabase.getDatabase(context).picturesDao();

            jsonResponseString = RetrofitInstance.getDeliveryNotesList(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getWarehouseCode().trim().toUpperCase());

            if(jsonResponseString != null){
                try {

                    JSONObject jsonObject;

                    jsonObject = new JSONObject(jsonResponseString);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject deliveryNoteObject = jsonArray.getJSONObject(i).getJSONObject("DeliveryNotes");

                        DeliveryNote deliveryNote = new DeliveryNote(deliveryNoteObject.getString("CardName"),
                                deliveryNoteObject.getInt("DocEntry"));

                        deliveryNote.setTotalUploaded(picturesDao.getPicturesUploadedCount(deliveryNote.getDocEntry()));
                        deliveryNote.setTotalPictures(picturesDao.getPicturesSavedTotal(deliveryNote.getDocEntry()));

                        deliveryNotesList.add(deliveryNote);

                    }

                }
                catch (Exception e){
                    Log.d("SalesList", " DataManager ERROR : " + e.toString());

                }

            }
            else
                isSuccessful = false;

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.deliveryNotesResponse(deliveryNotesList);
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

    public class GradeReclassification extends AsyncTask<Void, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        boolean stockDisposed = false;
        boolean goodReceipt = false;
        boolean patch = false;

        String downloadedSystemNumber;

        SerialNumbersPatchObject serialNumbersPatchObject;

        @Override
        protected Void doInBackground(Void... voids) {

            stockDisposed = RetrofitInstance.stockDisposal(SharedPreferencesClass.getCookie(), documentLinesStockDisposal);

            if(stockDisposed){
                goodReceipt = RetrofitInstance.goodsReceipt(SharedPreferencesClass.getCookie(), documentLinesLinesGoodsReceipt);

                // patch in details for the newly created serial number
                // add lotNumber
                if(goodReceipt){

                    downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(),
                            documentLinesLinesGoodsReceipt.getDocumentLines().get(0).getSerialNum());

                    if(downloadedSystemNumber != null){

                        try {

                            jsonObject = new JSONObject(downloadedSystemNumber);
                            jsonArray = jsonObject.getJSONArray("value");

                            // Extract scanned carton's system number
                            downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                            // set the serial number id
                            SERIAL_NUMBER_ID = jsonArray.getJSONObject(0).getInt("DocEntry");

                            serialNumbersPatchObject = new SerialNumbersPatchObject(
                                    SharedPreferencesClass.getWarehouseCode(),
                                    null
                            );

                            serialNumbersPatchObject.setLotNumber(documentLinesLinesGoodsReceipt.getDocumentLines().get(0).getLotNumber());

                            // patch lotNumber of the new serial number
                            patch = RetrofitInstance.patchSerialNumberDetails(
                                    SharedPreferencesClass.getCookie(),
                                    SERIAL_NUMBER_ID, serialNumbersPatchObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            patch = false;
                            return null;
                        }

                    }

                }
                return null;
            }
            else
                return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.reclassifyResult(patch);
        }
    }

    /*
    *   PicturesView
    * */
    public class GetAlreadyTakenPictures extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {

            PicturesDao picturesDao = AppDatabase.getDatabase(context).picturesDao();

            pictures = picturesDao.getPicturesTakenAlready(SharedPreferencesClass.getDocEntryNumber());

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.picturesList(pictures);
        }
    }

    public class UpdatePictures extends AsyncTask<Void, Void, Void> {

        long insertOps;
        int updateOps;

        @Override
        protected Void doInBackground(Void... aVoid) {

            PicturesDao picturesDao = AppDatabase.getDatabase(context).picturesDao();

            for(PicturesDB picture : pictures){
                if(picture.getId() == 0)
                    insertOps = picturesDao.insertOnePicture(picture);
                else
                    updateOps = picturesDao.updatePicture(picture);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.isPicturesOperationSuccessful(insertOps > 0 || updateOps > 0, 1);
        }
    }

    public class SavePictures extends AsyncTask<Void, Void, Void> {

        long insertOps;
        int updateOps;

        @Override
        protected Void doInBackground(Void... aVoid) {

            PicturesDao picturesDao = AppDatabase.getDatabase(context).picturesDao();

            for(PicturesDB picture : pictures){
                if(picture.getId() == 0)
                    insertOps = picturesDao.insertOnePicture(picture);
                else
                    updateOps = picturesDao.updatePicture(picture);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataManager.isPicturesOperationSuccessful(insertOps > 0 || updateOps > 0, 0);
        }
    }

    public class DeletePicture extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {

            PicturesDao picturesDao = AppDatabase.getDatabase(context).picturesDao();

            picturesDao.deletePicture(picture);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
