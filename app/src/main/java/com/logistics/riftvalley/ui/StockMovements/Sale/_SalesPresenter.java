package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.Context;

import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface _SalesPresenter {

    void initializeView(_SalesView salesView);

    void initializeView(_PicturesView picturesView);

    void initializeView(_SalesLandingPageView salesLandingPageView);

    void requestSalesOrdersList();

    void requestDeliveryNotesList(Context context);

    void moveToFumigation(String serialNumber, String warehouseCode, int source);

    void moveToDispatch(String serialNumber, String warehouseCode, int source);

    void dispatchGoods(List<SalesOrderDocumentLinesSerialNumbers> documentLines, Context context);

    void success(boolean isSuccessful);

    void salesOrdersList(List<SalesOrderList> salesOrderLists);

    void deliveryNotesList(List<DeliveryNote> deliveryNotesList);

    void doesSerialNumberExistInSAP(String serialNumber, String shippingLabelBarcode, String operationSource);

    void setShippingCaseNumberForSerialNumberSAP(String serialNumber, String shippingCaseNumber);

    void dispatchProcessesRequests(boolean isSuccessful, String message, JSONArray jsonArray);

    void dispatchGoodsResponse(boolean isSuccessful, String message, JSONArray jsonArray);

    void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray);

    /*
    *   PicturesView methods
    * */

    void updatePictures(Context context, List<PicturesDB> picturesDBList);

    void savePictures(Context context, List<PicturesDB> picturesDBList);

    void getPictures(Context context);

    void picturesList(List<PicturesDB> picturesDBS);

    void isSavePicturesSuccessful(boolean isSalesOperationSuccessful);

    void isUpdatePicturesSuccessful(boolean isUpdatePicturesSuccessful);

    void delete(PicturesDB picture);

    void haveDispatchPicturesBeenTaken(Context context);

    void dispatchPicturesHaveBeenTaken(boolean dispatchPicturesHaveBeenTaken);

}
