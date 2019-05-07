package com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory;

public class StockTransferLinesBinAllocations {

    int BinAbsEntry;
    int Quantity;
    String AllowNegativeQuantity;
    int SerialAndBatchNumbersBaseLine;
    String BinActionType;
    int BaseLineNumber;

    public StockTransferLinesBinAllocations(int binAbsEntry, int quantity, String allowNegativeQuantity, int serialAndBatchNumbersBaseLine, String binActionType, int baseLineNumber) {
        BinAbsEntry = binAbsEntry;
        Quantity = quantity;
        AllowNegativeQuantity = allowNegativeQuantity;
        SerialAndBatchNumbersBaseLine = serialAndBatchNumbersBaseLine;
        BinActionType = binActionType;
        BaseLineNumber = baseLineNumber;
    }

    public int getBinAbsEntry() {
        return BinAbsEntry;
    }

    public void setBinAbsEntry(int binAbsEntry) {
        BinAbsEntry = binAbsEntry;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getAllowNegativeQuantity() {
        return AllowNegativeQuantity;
    }

    public void setAllowNegativeQuantity(String allowNegativeQuantity) {
        AllowNegativeQuantity = allowNegativeQuantity;
    }

    public int getSerialAndBatchNumbersBaseLine() {
        return SerialAndBatchNumbersBaseLine;
    }

    public void setSerialAndBatchNumbersBaseLine(int serialAndBatchNumbersBaseLine) {
        SerialAndBatchNumbersBaseLine = serialAndBatchNumbersBaseLine;
    }

    public String getBinActionType() {
        return BinActionType;
    }

    public void setBinActionType(String binActionType) {
        BinActionType = binActionType;
    }

    public int getBaseLineNumber() {
        return BaseLineNumber;
    }

    public void setBaseLineNumber(int baseLineNumber) {
        BaseLineNumber = baseLineNumber;
    }
}
