package com.logistics.riftvalley.data.model;

import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;

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

}
