package com.logistics.riftvalley.data.model.StockDisposals;

import com.logistics.riftvalley.data.model.Entity.SerialNumbers;

import java.util.List;

public class StockDisposalsEntity {

    String ItemCode;
    int Quantity;
    String WarehouseCode;
    List<com.logistics.riftvalley.data.model.Entity.SerialNumbers> SerialNumbers;

    public StockDisposalsEntity(String itemCode, int quantity, String warehouseCode, List<SerialNumbers> serialNumbers) {
        ItemCode = itemCode;
        Quantity = quantity;
        WarehouseCode = warehouseCode;
        SerialNumbers = serialNumbers;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getWarehouseCode() {
        return WarehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        WarehouseCode = warehouseCode;
    }

    public List<SerialNumbers> getSerialNumbers() {
        return SerialNumbers;
    }

    public void setSerialNumbers(List<SerialNumbers> serialNumbers) {
        SerialNumbers = serialNumbers;
    }

}
