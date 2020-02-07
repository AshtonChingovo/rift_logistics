package com.logistics.riftvalley.data;

import android.util.Log;

import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.model.API_Helper;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.SerialNumbers;
import com.logistics.riftvalley.data.model.Entity.StaticVariables;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLines;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLinesBinAllocations;
import com.logistics.riftvalley.data.model._API_Helper;
import com.logistics.riftvalley.ui.Login._LoginPresenter;
import com.logistics.riftvalley.ui.NewInventory._NewInventoryPresenter;
import com.logistics.riftvalley.ui.StockMovements.Transfers._TransfersPresenter;
import com.logistics.riftvalley.ui.StockMovements._WarehouseLocations_MovementsPresenter;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.CHECK_IN;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.CHECK_OUT;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.MOVE_TO_DISPATCH_ACTIVITY;
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

    @Override
    public void returnSerialNumberSAPSystemNumberWithBinLocationAbsEntry(int systemNumber, int downloadedBinLocationAbsEntry) {
        // IF serialNumber > 0 (zero) means all's good ELSE something is wrong
        if(systemNumber > 0){
            if(serialNumberTransferActivityRequestSource == SCAN_NEW_INVENTORY)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson(systemNumber));
            else if(serialNumberTransferActivityRequestSource == IN_WAREHOUSE_ACTIVITY)
                api_helper.transferSerialNumberSAP(generateStockTransferJsonForStockMovements_InWarehouse(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_MoveToDispatch(systemNumber, downloadedBinLocationAbsEntry));
            else if(serialNumberTransferActivityRequestSource == CHECK_OUT)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_CheckOut(systemNumber));
            else if(serialNumberTransferActivityRequestSource == CHECK_IN)
                api_helper.transferSerialNumberSAP(generateStockTransfersJson_CheckIn(systemNumber, downloadedBinLocationAbsEntry));
        }
        else{
            if(serialNumberTransferActivityRequestSource == SCAN_NEW_INVENTORY)
                newInventoryPresenter.serialNumberSystemNumber(systemNumber);
            else if(serialNumberTransferActivityRequestSource == IN_WAREHOUSE_ACTIVITY || serialNumberTransferActivityRequestSource == MOVE_TO_DISPATCH_ACTIVITY
                    || serialNumberTransferActivityRequestSource == CHECK_OUT || serialNumberTransferActivityRequestSource == CHECK_IN)
                transfersPresenter.serialNumberSystemNumber(systemNumber);
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
    }

    @Override
    public void transferSerialNumber() {

    }

    public StockTransfer generateStockTransfersJson(int systemNumber){

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

    }

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

}
