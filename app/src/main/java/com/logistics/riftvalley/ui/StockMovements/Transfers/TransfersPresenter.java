package com.logistics.riftvalley.ui.StockMovements.Transfers;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class TransfersPresenter implements _TransfersPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to view
    _TransfersView transfersView;

    public TransfersPresenter() {
        dataManager.initializeTransfersPresenter(this);
    }

    @Override
    public void initializeTransfersView( _TransfersView transfersView) {
        this.transfersView = transfersView;
    }

    @Override
    public void requestStockTransfer() {

    }

    @Override
    public void transferRequestResponse(boolean isSuccessful) {
        transfersView.success(isSuccessful);
    }

    @Override
    public void requestWarehousesList(int source) {
        dataManager.requestListOfWarehouses(WAREHOUSE_CHECK_OUT_LOCATIONS_LIST);
    }

    @Override
    public void warehouses(List<Warehouses> warehousesList) {

        List<Warehouses> filteredList = new ArrayList<>();

        // remove Bay10 from list because it does not receive any carton from other warehousesCheckOutOptionsList
        for(Warehouses warehousesObject : warehousesList){
            if(warehousesObject.getCode().equalsIgnoreCase(BAY_10_WAREHOUSE))
                continue;

            filteredList.add(warehousesObject);

        }

        transfersView.warehouses(filteredList);

    }

    @Override
    public void requestSystemNumber(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, null, source);
    }

    @Override
    public void serialNumberSystemNumber(int systemNumber) {
        transfersView.success(false);
    }

}
