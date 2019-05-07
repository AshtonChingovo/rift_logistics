package com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory;


import java.util.List;

public class StockTransferLines {

    String ItemCode;
    String ItemDescription;
    int Quantity;
    String SerialNumber;
    // ToWarehouseCode
    String WarehouseCode;
    String FromWarehouseCode;
    List<com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers> SerialNumbers;
    List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations> StockTransferLinesBinAllocations;

    public StockTransferLines(String itemCode, String itemDescription, int quantity, String serialNumber, String warehouseCode, String fromWarehouseCode, List<com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers> serialNumbers, List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations> stockTransferLinesBinAllocations) {
        ItemCode = itemCode;
        ItemDescription = itemDescription;
        Quantity = quantity;
        SerialNumber = serialNumber;
        WarehouseCode = warehouseCode;
        FromWarehouseCode = fromWarehouseCode;
        SerialNumbers = serialNumbers;
        StockTransferLinesBinAllocations = stockTransferLinesBinAllocations;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getWarehouseCode() {
        return WarehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        WarehouseCode = warehouseCode;
    }

    public String getFromWarehouseCode() {
        return FromWarehouseCode;
    }

    public void setFromWarehouseCode(String fromWarehouseCode) {
        FromWarehouseCode = fromWarehouseCode;
    }

    public List<com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers> getSerialNumbers() {
        return SerialNumbers;
    }

    public void setSerialNumbers(List<com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers> serialNumbers) {
        SerialNumbers = serialNumbers;
    }

    public List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations> getStockTransferLinesBinAllocations() {
        return StockTransferLinesBinAllocations;
    }

    public void setStockTransferLinesBinAllocations(List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations> stockTransferLinesBinAllocations) {
        StockTransferLinesBinAllocations = stockTransferLinesBinAllocations;
    }
}
