package com.logistics.riftvalley.ui.NewInventory;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _NewInventoryPresenter {

    void initializeNewInventoryView(_NewInventoryView newInventoryView);

    // request warehouses list
    void requestActiveWarehousesList();

    // warehousesList from the DataManager
    void warehousesList(List<Warehouses> warehousesList);

    // request system number of the scanned serial number
    void requestSystemNumber(String serialNumber, String warehouseCode, int source);

    // system number request result
    void serialNumberSystemNumber(int systemNumber);

    void transferRequestResponse(boolean isSuccessful);

}
