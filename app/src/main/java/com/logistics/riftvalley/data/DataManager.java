package com.logistics.riftvalley.data;

import android.content.Context;
import android.util.Log;

import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.model.API_Helper;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.Entity.SerialNumbers;
import com.logistics.riftvalley.data.model.Entity.StaticVariables;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLines;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLinesBinAllocations;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.StockDisposalsEntity;
import com.logistics.riftvalley.data.model._API_Helper;
import com.logistics.riftvalley.ui.Login._LoginPresenter;
import com.logistics.riftvalley.ui.NewInventory._NewInventoryPresenter;
import com.logistics.riftvalley.ui.StockMovements.ProductionReturn._ProductionReturnPresenter;
import com.logistics.riftvalley.ui.StockMovements.Sale._SalesPresenter;
import com.logistics.riftvalley.ui.StockMovements.StockDisposals._StockDisposalsPresenter;
import com.logistics.riftvalley.ui.StockMovements.Transfers._TransfersPresenter;
import com.logistics.riftvalley.ui.StockMovements._WarehouseLocations_MovementsPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.CHECK_IN;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.CHECK_OUT;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.GRADE_RECLASSIFICATION;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_DISPATCH_ACTIVITY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_DISPATCH_SALES;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_DISPATCH_TRANSFERS_ACTIVITY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_FUMIGATION_SALES;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.PRODUCTION_RETURN;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.STOCK_DISPOSALS;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.TPZ_WAREHOUSE;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.IN_WAREHOUSE_ACTIVITY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.ITEM_CODE;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.SCAN_NEW_INVENTORY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.TRANSIT_WAREHOUSE;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.WAREHOUSE_CHECK_OUT_LOCATIONS_LIST;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.WAREHOUSE_LOCATIONS_MOVEMENTS_ACTIVITY;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.WAREHOUSE_NAME_CODE_LIST_ACTIVITY;

public class DataManager implements _DataManager{

    // reference to Helper classes
    _API_Helper api_helper = new API_Helper(this);

    // reference to Presenter View
    _LoginPresenter loginPresenter;
    _NewInventoryPresenter newInventoryPresenter;
    _WarehouseLocations_MovementsPresenter warehouseLocations_movementsPresenter;
    _TransfersPresenter transfersPresenter;
    _ProductionReturnPresenter productionReturnPresenter;
    _StockDisposalsPresenter stockDisposalsPresenter;
    _SalesPresenter salesPresenter;

    // new inventory variables
    String warehouseCode;
    String barcode;

    // activity id i.e states which activity requested for the warehouseList
    int warehouseListActivityRequestSource;

    // activity id i.e states which activity requested for a serial number transfer
    int serialNumberTransferActivityRequestSource;

    // initialize respective views
    @Override
    public void initializeLoginPresenter(_LoginPresenter loginPresenter) {
        this.loginPresenter = loginPresenter;
    }

    @Override
    public void initializeNewInventoryPresenter(_NewInventoryPresenter newInventoryPresenter) {
        this.newInventoryPresenter = newInventoryPresenter;
    }

    @Override
    public void initializeStockMovementsWarehouseListPresenter(_WarehouseLocations_MovementsPresenter warehouseLocations_movementsPresenter) {
        this.warehouseLocations_movementsPresenter = warehouseLocations_movementsPresenter;
    }

    @Override
    public void initializeTransfersPresenter(_TransfersPresenter transfersPresenter) {
        this.transfersPresenter = transfersPresenter;
    }

    @Override
    public void initializeProductionReturnPresenter(_ProductionReturnPresenter productionReturnPresenter) {
        this.productionReturnPresenter = productionReturnPresenter;
    }

    @Override
    public void initializeStockDisposalsPresenter(_StockDisposalsPresenter stockDisposalsPresenter) {
        this.stockDisposalsPresenter = stockDisposalsPresenter;
    }

    @Override
    public void initializeSalesPresenter(_SalesPresenter salesPresenter) {
        this.salesPresenter = salesPresenter;
    }

    @Override
    public void login(Login login) {
        Log.d("DataManagerLogin", " ** login attempt ** ");
        api_helper.login(login);
    }

    @Override
    public void loginResponse(boolean loginResponse) {
        Log.d("DataManagerLogin", " ** DataManager login response ** " + loginResponse);
        loginPresenter.loginResponse(loginResponse);
    }

    @Override
    public void requestListOfWarehouses(int activitySource) {
        warehouseListActivityRequestSource = activitySource;
        api_helper.requestListOfWarehouses(activitySource);

    }

    @Override
    public void returnWarehouseList(List<Warehouses> warehousesList) {
        if(warehouseListActivityRequestSource == WAREHOUSE_NAME_CODE_LIST_ACTIVITY)
            newInventoryPresenter.warehousesList(warehousesList);
        else if(warehouseListActivityRequestSource == WAREHOUSE_LOCATIONS_MOVEMENTS_ACTIVITY)
            warehouseLocations_movementsPresenter.warehousesList(warehousesList);
        else if(warehouseListActivityRequestSource == WAREHOUSE_CHECK_OUT_LOCATIONS_LIST)
            transfersPresenter.warehouses(warehousesList);
    }

    /*
    *   New Inventory operations
    * */
    @Override
    public void requestSerialNumberSystemNumber(String serialNumber, String warehouseCode, int source) {

        this.warehouseCode = warehouseCode;
        this.barcode = serialNumber;

        serialNumberTransferActivityRequestSource = source;

        api_helper.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);

    }

    // TODO check line with the following code :: serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY
    @Override
    public void returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(int systemNumber, int downloadedBinLocationAbsEntry) {
        // IF serialNumber > 0 (zero) means all's good ELSE something is wrong
        if(systemNumber > 0){
            if(serialNumberTransferActivityRequestSource == SCAN_NEW_INVENTORY)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == IN_WAREHOUSE_ACTIVITY)
                api_helper.transferSerialNumberSAP(generateStockTransferJsonForStockMovements_InWarehouse(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_MoveToDispatch(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == CHECK_OUT)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_CheckOut(systemNumber));
            else if(serialNumberTransferActivityRequestSource == CHECK_IN)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_CheckIn(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == PRODUCTION_RETURN)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_ProductionReturn(systemNumber));
            else if(serialNumberTransferActivityRequestSource == STOCK_DISPOSALS)
                api_helper.stockDisposal(generateDocumentLinesObject(systemNumber));
            else if(serialNumberTransferActivityRequestSource == MOVE_TO_FUMIGATION_SALES || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_SALES)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_Sales(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == GRADE_RECLASSIFICATION)
                transfersPresenter.doesSerialNumberExist(true, systemNumber, downloadedBinLocationAbsEntry);
        }
        else{
            if(serialNumberTransferActivityRequestSource == SCAN_NEW_INVENTORY)
                newInventoryPresenter.serialNumberSystemNumber(systemNumber);
            else if(serialNumberTransferActivityRequestSource == IN_WAREHOUSE_ACTIVITY || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_TRANSFERS_ACTIVITY
                    || serialNumberTransferActivityRequestSource == CHECK_OUT || serialNumberTransferActivityRequestSource == CHECK_IN)
                transfersPresenter.serialNumberSystemNumber(systemNumber);
            else if(serialNumberTransferActivityRequestSource == PRODUCTION_RETURN)
                productionReturnPresenter.success(false);
            else if(serialNumberTransferActivityRequestSource == STOCK_DISPOSALS)
                stockDisposalsPresenter.success(false);
            else if(serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_SALES)
                salesPresenter.success(false);
            else if(serialNumberTransferActivityRequestSource == GRADE_RECLASSIFICATION)
                transfersPresenter.doesSerialNumberExist(false, systemNumber, downloadedBinLocationAbsEntry);
        }
    }

    @Override
    public void transferSerialNumberSAP(StockTransfer stockTransferObject) {

    }

    @Override
    public void serialNumberTransferResponse(boolean isSuccessful) {
        if(serialNumberTransferActivityRequestSource == SCAN_NEW_INVENTORY)
            newInventoryPresenter.transferRequestResponse(isSuccessful);
        else if(serialNumberTransferActivityRequestSource == IN_WAREHOUSE_ACTIVITY || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY
                || serialNumberTransferActivityRequestSource == CHECK_OUT || serialNumberTransferActivityRequestSource == CHECK_IN)
            transfersPresenter.transferRequestResponse(isSuccessful);
        else if(serialNumberTransferActivityRequestSource == PRODUCTION_RETURN)
            productionReturnPresenter.success(isSuccessful);
        else if(serialNumberTransferActivityRequestSource == MOVE_TO_FUMIGATION_SALES || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_SALES)
            salesPresenter.success(isSuccessful);
    }

    @Override
    public void transferSerialNumber() {

    }

    @Override
    public void stockDisposal(DocumentLines documentLines) {

    }

    @Override
    public void stockDisposalResponse(boolean isSuccessful) {
        stockDisposalsPresenter.success(isSuccessful);
    }

    @Override
    public void requestSalesOrderList() {
        api_helper.requestSalesOrderList();
    }

    @Override
    public void requestDeliveryNotesList(Context context) {
        api_helper.requestDeliveryNotesList(context);
    }

    @Override
    public void salesOrderListResponse(String salesOrdersJsonString) {

        List<SalesOrderList> salesOrderLists = new ArrayList<>();

        Log.d("SalesList", " DataManager jString : " + salesOrdersJsonString);

        if(salesOrdersJsonString != null){
            try {
                JSONObject jsonObject;

                jsonObject = new JSONObject(salesOrdersJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("value");

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject ordersObject = jsonArray.getJSONObject(i).getJSONObject("Orders");
                    JSONObject ordersDocumentLinesObject = jsonArray.getJSONObject(i).getJSONObject("Orders/DocumentLines");

                    salesOrderLists.add(new SalesOrderList(ordersObject.getString("CardCode"),
                            ordersObject.getString("CardName"),
                            ordersDocumentLinesObject.getString("ItemCode"),
                            ordersDocumentLinesObject.getInt("Quantity") == ordersDocumentLinesObject.getInt("RemainingOpenQuantity") ? ordersDocumentLinesObject.getInt("Quantity"): ordersDocumentLinesObject.getInt("RemainingOpenQuantity"),
                            ordersObject.getInt("DocEntry")));

                }

                salesPresenter.salesOrdersList(salesOrderLists);

            }
            catch (Exception e){
                Log.d("SalesList", " DataManager ERROR : " + e.toString());
                salesPresenter.success(false);
            }

        }
        else
            salesPresenter.success(false);

    }

    @Override
    public void deliveryNotesResponse(List<DeliveryNote> deliveryNotesList) {
        if(deliveryNotesList != null)
            salesPresenter.deliveryNotesList(deliveryNotesList);
        else
            salesPresenter.success(false);
    }

    @Override
    public void doesSerialNumberExistInSAP(String serialNumber, String shippingLabelBarcode) {
        api_helper.doesSerialNumberExistInSAP(serialNumber, shippingLabelBarcode);
    }

    @Override
    public void setShippingCaseNumberToSerialNumber(String serialNumber, String shippingCaseNumber) {
        api_helper.setShippingCaseNumberToSerialNumber(serialNumber, shippingCaseNumber);
    }

    public void salesOrderDispatch(boolean isSuccessful){
        salesPresenter.success(isSuccessful);
    }

    @Override
    public void dispatchProcesses(boolean isSuccessful, String message,JSONArray jsonArray) {
        salesPresenter.dispatchProcessesRequests(isSuccessful, message, jsonArray);
    }

    @Override
    public void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray) {
        salesPresenter.shippingCaseNumber(isSuccessful, message, jsonArray);
    }

    @Override
    public void dispatchGoods(List<SalesOrdersDocumentLines> documentLines, Context context) {
        api_helper.dispatchGoods(documentLines, context);
    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {
        salesPresenter.dispatchGoodsResponse(isSuccessful, message, null);
    }

    @Override
    public void getLotNumbers() {
        api_helper.getLotNumbers();
    }

    @Override
    public void returnLotNumbers(String lotNumberJson) {
        transfersPresenter.lotNumbers(lotNumberJson);
    }

    @Override
    public void reclassifyGrade(com.logistics.riftvalley.data.model.StockDisposals.DocumentLines documentLinesStockDisposal,
                                com.logistics.riftvalley.data.model.GoodReceipt.DocumentLines documentLinesLinesGoodsReceipt) {
        api_helper.reclassifyGrade(documentLinesStockDisposal, documentLinesLinesGoodsReceipt);
    }

    @Override
    public void reclassifyResult(boolean isSuccessful) {
        transfersPresenter.reclassifyResult(isSuccessful);
    }

    @Override
    public void getPictures(Context context) {
        api_helper.getPictures(context);
    }

    // pictures list from the database
    @Override
    public void picturesList(List<PicturesDB> pictures) {
        salesPresenter.picturesList(pictures);
    }

    @Override
    public void picturesUpdate(Context context, List<PicturesDB> pictures) {
        api_helper.picturesUpdate(context, pictures);
    }

    @Override
    public void picturesSave(Context context, List<PicturesDB> pictures) {
        api_helper.picturesSave(context, pictures);
    }

    @Override
    public void isPicturesOperationSuccessful(boolean isSuccessful, int operationId) {
        // if operationID == 0 :: save operation
        // if operationID == 1 :: save to drafts operation
        if(operationId == 0)
            salesPresenter.isSavePicturesSuccessful(isSuccessful);
        else if(operationId == 1)
            salesPresenter.isUpdatePicturesSuccessful(isSuccessful);

    }

    @Override
    public void deleteImage(PicturesDB picture) {
        api_helper.deleteImage(picture);
    }

    @Override
    public void haveDispatchPicturesBeenTaken(Context context) {
        api_helper.haveDispatchPicturesBeenTaken(context);
    }

    @Override
    public void dispatchPicturesHaveBeenTaken(boolean dispatchPicturesTaken) {
        salesPresenter.dispatchPicturesHaveBeenTaken(dispatchPicturesTaken);
    }

    public StockTransfer generateStockTransfersJson(int systemNumber, int binAbsEntry){

        String fromWarehouse = TPZ_WAREHOUSE;
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();

        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;


        List<SerialNumbers> serialNumbers = new ArrayList<>();
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        serialNumbers.add(serialNumbersObj);

        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        return new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);


    }

    /*public StockTransfer generateStockTransfersJson(int systemNumber){

        String fromWarehouse = TPZ_WAREHOUSE;
        int quantity = 1;

        List<SerialNumbers> serialNumbers = new ArrayList<>();
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        serialNumbers.add(serialNumbersObj);

        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, StaticVariables.FROMWAREHOUSE, serialNumbers, null);
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }*/

    public StockTransfer generateStockTransferJsonForStockMovements_InWarehouse(int systemNumber, int binAbsEntry){

        // All stock movements first go to the TRANSIT warehouse
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();

        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        serialNumbers.add(serialNumbersObj);

        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        return new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

    }

    public StockTransfer generateStockTransfersJson_MoveToDispatch(int systemNumber, int binAbsEntry){

        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();

        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);

        List<SerialNumbers> serialNumbers = new ArrayList<>();
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();

        serialNumbers.add(serialNumbersObj);
        stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

    public StockTransfer generateStockTransfersJson_CheckOut(int systemNumber){

        // All stock movements first go to the TRANSIT warehouse
        String toWarehouseCode = TRANSIT_WAREHOUSE;
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();
        int quantity = 1;

        //Serial Numbers
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        serialNumbers.add(serialNumbersObj);

        // Bin location
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();

        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, StaticVariables.TRANSITWAREHOUSE, fromWarehouse, serialNumbers, stockTransferLinesBinAllocations);
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, toWarehouseCode, stockTransferLines);

        return stockTransfer;

    }

    public StockTransfer generateStockTransfersJson_CheckIn(int systemNumber, int binAbsEntry){

        // All stock movements first go to the TRANSIT warehouse
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = TRANSIT_WAREHOUSE;
        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);

        List<SerialNumbers> serialNumbers = new ArrayList<>();
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();

        serialNumbers.add(serialNumbersObj);
        //stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                null);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

    public StockTransfer generateStockTransfersJson_ProductionReturn(int systemNumber){

        // All stock movements first go to the TRANSIT warehouse
        String toWarehouseCode = TPZ_WAREHOUSE;
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();

        int quantity = 1;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        serialNumbers.add(serialNumbersObj);

        // Bin location
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, StaticVariables.FROMWAREHOUSE, fromWarehouse, serialNumbers, stockTransferLinesBinAllocations);
        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, toWarehouseCode, stockTransferLines);

        return stockTransfer;

    }

    public com.logistics.riftvalley.data.model.StockDisposals.DocumentLines generateDocumentLinesObject(int systemNumber){

        List<SerialNumbers> SerialNumbers = new ArrayList<>();
        List<StockDisposalsEntity> DocumentLines = new ArrayList<>();

        SerialNumbers.add(new SerialNumbers(barcode, systemNumber, 1));

        DocumentLines.add(new StockDisposalsEntity(
                        ITEM_CODE,
                        1,
                        SharedPreferencesClass.getWarehouseCode(),
                        SerialNumbers
                )
        );

        return new DocumentLines(DocumentLines);
    }

    public StockTransfer generateStockTransfersJson_Sales(int systemNumber, int binAbsEntry){

        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();
        String itemCode = SharedPreferencesClass.getSalesOrderItemCode();
        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        // Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();

        // Bin Locations
        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();

        serialNumbers.add(serialNumbersObj);
        stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(itemCode, itemCode, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

}
