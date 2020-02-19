package com.logistics.riftvalley.data.model.GoodReceipt;

import com.logistics.riftvalley.data.model.Entity.SerialNumbers;

import java.util.List;

public class DocumentLineProperties {

    String ItemCode;
    String ItemDescription;
    int Quantity;
    double UnitPrice;
    String SerialNum;
    String WarehouseCode;
    List<SerialNumbers> SerialNumbers;
    List<DocumentLinesBinAllocations> DocumentLinesBinAllocations;

    // optional field
    String LotNumber;

    public DocumentLineProperties(String itemCode, String itemDescription, int quantity, String serialNum, String warehouseCode, List<SerialNumbers> serialNumbers) {
        ItemCode = itemCode;
        ItemDescription = itemDescription;
        Quantity = quantity;
        SerialNum = serialNum;
        WarehouseCode = warehouseCode;
        SerialNumbers = serialNumbers;
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

    public String getSerialNum() {
        return SerialNum;
    }

    public void setSerialNum(String serialNum) {
        SerialNum = serialNum;
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

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        UnitPrice = unitPrice;
    }

    public List<com.logistics.riftvalley.data.model.GoodReceipt.DocumentLinesBinAllocations> getDocumentLinesBinAllocations() {
        return DocumentLinesBinAllocations;
    }

    public void setDocumentLinesBinAllocations(List<DocumentLinesBinAllocations> documentLinesBinAllocations) {
        DocumentLinesBinAllocations = documentLinesBinAllocations;
    }

    public String getLotNumber() {
        return LotNumber;
    }

    public void setLotNumber(String lotNumber) {
        LotNumber = lotNumber;
    }
}
