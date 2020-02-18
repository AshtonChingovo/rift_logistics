package com.logistics.riftvalley.ui.NewInventory;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class NewInventoryPresenter implements _NewInventoryPresenter {

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to _
    _NewInventoryView newInventoryView;

    public NewInventoryPresenter() {
        dataManager.initializeNewInventoryPresenter(this);
    }

    @Override
    public void initializeNewInventoryView(_NewInventoryView newInventoryView) {
        this.newInventoryView = newInventoryView;
    }

    @Override
    public void requestActiveWarehousesList() {
        dataManager.requestListOfWarehouses(WAREHOUSE_NAME_CODE_LIST_ACTIVITY);
    }

    @Override
    public void warehousesList(List<Warehouses> warehousesList) {
        if(warehousesList == null)
            newInventoryView.failed();
        else
            newInventoryView.warehousesList(warehousesList);
    }

    @Override
    public void requestSystemNumber(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    // receives the system number of the scanned serial number
    @Override
    public void serialNumberSystemNumber(int systemNumber) {
        newInventoryView.failed();
    }

    @Override
    public void transferRequestResponse(boolean isSuccessful) {
        if(isSuccessful)
            newInventoryView.success();
        else
            newInventoryView.failed();
    }


}
