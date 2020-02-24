package com.logistics.riftvalley.data.model;

import android.content.Context;

import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;

import org.json.JSONArray;

import java.util.List;

public interface _API_Helper {

    void login(Login login);

    void loginResponse(boolean loginResponse);

    // get warehouse list for the WarehouseName_CodeList in the NewInventory
    void requestListOfWarehouses(int source);

    void returnWarehouseList(List<Warehouses> warehousesList);

    // scanning operations
    void requestSerialNumberSystemNumber(String serialNumber, String warehouseCode, int source);

    // returns the system number of the scanned serial number and bin location IF needed
    void returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(int systemNumber, int downloadedBinLocationAbsEntry);

    // transfer serial number from TPZ to designated warehouse
    void transferSerialNumberSAP(StockTransfer stockTransferObject);

    void serialNumberTransferResponse(boolean isSuccessful);

    // transfer stock to new warehouse
    void transferSerialNumber();

    void stockDisposal(com.logistics.riftvalley.data.model.StockDisposals.DocumentLines documentLines);

    void stockDisposalResponse(boolean isSuccessful);


    /*
    *   Sales
    * */
    void requestSalesOrderList();

    void requestDeliveryNotesList(Context context);

    void salesOrderListResponse(String salesOrdersJsonString);

    void deliveryNotesResponse(List<DeliveryNote> deliveryNotesList);

    void doesSerialNumberExistInSAP(String serialNumber);

    void setShippingCaseNumberToSerialNumber(String serialNumber, String shippingCaseNumber);

    void salesOrderDispatch(boolean isSuccessful);

    // returns results for dispatch processes from SAP
    void dispatchProcesses(boolean isSuccessful, String message);

    void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray);

    void dispatchGoods(List<SalesOrdersDocumentLines> documentLines);

    void dispatchGoodsResponse(boolean isSuccessful, String message);

    /*
    *  grade reclassification
    * */
    void getLotNumbers();

    void returnLotNumbers(String lotNumberJson);

    void reclassifyGrade(com.logistics.riftvalley.data.model.StockDisposals.DocumentLines documentLines, com.logistics.riftvalley.data.model.GoodReceipt.DocumentLines documentLinesGoodsReceipt);

    void reclassifyResult(boolean isSuccessful);

    /*
    *   PicturesView
    * */
    void getPictures(Context context);

    void picturesList(List<PicturesDB> pictures);

    void picturesUpdate(Context context, List<PicturesDB> pictures);

    void picturesSave(Context context, List<PicturesDB> pictures);

    void isPicturesOperationSuccessful(boolean isSuccessful, int operationId);

    void deleteImage(PicturesDB picture);

}
