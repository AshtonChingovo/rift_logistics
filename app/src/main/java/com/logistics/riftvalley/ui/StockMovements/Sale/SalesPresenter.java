package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.SALES_BASE_LINE;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.SALES_BASE_TYPE;

public class SalesPresenter implements _SalesPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to views
    _SalesView salesView;
    _PicturesView picturesView;
    _SalesLandingPageView salesLandingPageView;

    String operationSource;

    public SalesPresenter() {
        dataManager.initializeSalesPresenter(this);
    }

    @Override
    public void initializeView(_SalesView salesView) {
        this.salesView = salesView;
    }

    @Override
    public void initializeView(_PicturesView picturesView) {
        this.picturesView = picturesView;
    }

    @Override
    public void initializeView(_SalesLandingPageView salesLandingPageView) {
        this.salesLandingPageView = salesLandingPageView;
    }

    @Override
    public void requestSalesOrdersList() {
        dataManager.requestSalesOrderList();
    }

    @Override
    public void requestDeliveryNotesList(Context context) {
        dataManager.requestDeliveryNotesList(context);
    }

    @Override
    public void moveToFumigation(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    @Override
    public void moveToDispatch(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    @Override
    public void dispatchGoods(List<SalesOrderDocumentLinesSerialNumbers> barcodes, Context context) {

        List<SalesOrderDocumentLinesSerialNumbers> validBarcodes = new ArrayList<>();
        List<SalesOrdersDocumentLines> documentLines = new ArrayList<>();

        for(int i = 0; i < barcodes.size(); ++i){
            if(barcodes.get(i).getInternalSerialNumber().startsWith("B_"))
                continue;
            else
                validBarcodes.add(barcodes.get(i));
        }

        documentLines.add(new SalesOrdersDocumentLines(SharedPreferencesClass.getSalesOrderItemCode(), SharedPreferencesClass.getWarehouseCode(), validBarcodes.size(), SALES_BASE_TYPE, SharedPreferencesClass.getSalesOrderDocEntry(), SALES_BASE_LINE, validBarcodes));

        dataManager.dispatchGoods(documentLines, context);

    }

    @Override
    public void success(boolean isSuccessful) {
        salesView.success(isSuccessful);
    }

    @Override
    public void salesOrdersList(List<SalesOrderList> salesOrderLists) {
        salesView.salesOrdersList(salesOrderLists);
    }

    @Override
    public void deliveryNotesList(List<DeliveryNote> deliveryNotesList) {
        salesView.deliveryNotesList(deliveryNotesList);
    }

    @Override
    public void doesSerialNumberExistInSAP(String serialNumber, String shippingLabelBarcode, String operationSource) {
        this.operationSource = operationSource;
        dataManager.doesSerialNumberExistInSAP(serialNumber, shippingLabelBarcode);
    }

    @Override
    public void setShippingCaseNumberForSerialNumberSAP(String serialNumber, String shippingCaseNumber) {
        dataManager.setShippingCaseNumberToSerialNumber(serialNumber, shippingCaseNumber);
    }

    @Override
    public void dispatchProcessesRequests(boolean isSuccessful, String message, JSONArray jsonArray) {
        // salesView.dispatchProcessResponse(isSuccessful, message, operationSource);

        if(isSuccessful){
            try {
                salesView.dispatchProcessResponse(true, message, new SalesOrderDocumentLinesSerialNumbers(jsonArray.getJSONObject(0).getString("MfrSerialNo"),
                        jsonArray.getJSONObject(0).getString("SerialNumber"), jsonArray.getJSONObject(0).getInt("SystemNumber")));
            } catch (JSONException e) {
                salesView.dispatchProcessResponse(false, message, null);
                e.printStackTrace();
            }
        }
        else
            salesView.dispatchProcessResponse(false, message, null);

    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message, JSONArray jsonArray) {
        salesView.dispatchGoodsResponse(isSuccessful, message);
    }

    @Override
    public void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray) {
        if(isSuccessful){
            try {
                 salesView.isShippingCaseNumberAdded(true, message, new SalesOrderDocumentLinesSerialNumbers(jsonArray.getJSONObject(0).getString("MfrSerialNo"),
                         jsonArray.getJSONObject(0).getString("SerialNumber"), jsonArray.getJSONObject(0).getInt("SystemNumber")));
            } catch (JSONException e) {
                salesView.isShippingCaseNumberAdded(false, message, null);
                e.printStackTrace();
            }
        }
        else
            salesView.isShippingCaseNumberAdded(false, message, null);

    }

    /*
    *   PicturesView
    * */
    @Override
    public void updatePictures(Context context, List<PicturesDB> picturesDBList) {

        // set all pictures to saved value of zero
        for(PicturesDB picture : picturesDBList){
            if(picture.getId() == 0){
                picture.setSaved(0);
            }
        }

        dataManager.picturesUpdate(context, picturesDBList);

    }

    @Override
    public void savePictures(Context context, List<PicturesDB> picturesDBList) {

        // set all pictures to saved value of zero
        for(PicturesDB picture : picturesDBList){
            picture.setSaved(1);
            // picture.setBase64String(convertToBase64(picture.getUri(), context));
        }

        dataManager.picturesSave(context, picturesDBList);

    }

    @Override
    public void getPictures(Context context) {
        dataManager.getPictures(context);
    }

    @Override
    public void picturesList(List<PicturesDB> picturesList) {
        picturesView.picturesList(picturesList);
    }

    @Override
    public void isSavePicturesSuccessful(boolean isSalesOperationSuccessful) {
        picturesView.isSavePicturesOperationSuccessful(isSalesOperationSuccessful);
    }

    @Override
    public void isUpdatePicturesSuccessful(boolean isUpdatePicturesSuccessful) {
        picturesView.isUpdatePicturesOperationSuccessful(isUpdatePicturesSuccessful);
    }

    @Override
    public void delete(PicturesDB picture) {
        dataManager.deleteImage(picture);
    }

    @Override
    public void haveDispatchPicturesBeenTaken(Context context) {
        dataManager.haveDispatchPicturesBeenTaken(context);
    }

    @Override
    public void dispatchPicturesHaveBeenTaken(boolean dispatchPicturesHaveBeenTaken) {
        salesLandingPageView.havePicturesBeenTaken(dispatchPicturesHaveBeenTaken);
    }

    public String convertToBase64(String filePath, Context context) {

        InputStream ims = null;

        try {

            ims = context.getContentResolver().openInputStream(Uri.fromFile(new File(filePath)));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(ims);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;

/*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;
*/

    }

}
