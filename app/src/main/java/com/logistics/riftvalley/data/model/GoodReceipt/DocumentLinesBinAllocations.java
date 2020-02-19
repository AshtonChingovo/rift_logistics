package com.logistics.riftvalley.data.model.GoodReceipt;

public class DocumentLinesBinAllocations {

    int BinAbsEntry;
    int Quantity;

    // optional field
    int SerialAndBatchNumbersBaseLine;

    public DocumentLinesBinAllocations(int binAbsEntry, int quantity) {
        BinAbsEntry = binAbsEntry;
        Quantity = quantity;
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

    public int getSerialAndBatchNumbersBaseLine() {
        return SerialAndBatchNumbersBaseLine;
    }

    public void setSerialAndBatchNumbersBaseLine(int serialAndBatchNumbersBaseLine) {
        SerialAndBatchNumbersBaseLine = serialAndBatchNumbersBaseLine;
    }
}
