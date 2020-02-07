package com.logistics.riftvalley.data.model.SalesOrder;

import java.util.List;

public class SalesOrdersDocumentLines {

    String ItemCode;
    String WarehouseCode;
    int Quantity;
    int BaseType;
    int BaseEntry;
    int BaseLine;
    List<SalesOrderDocumentLinesSerialNumbers> SerialNumbers;

    public SalesOrdersDocumentLines(String itemCode, String warehouseCode, int quantity, int baseType, int baseEntry, int baseLine,
                                    List<SalesOrderDocumentLinesSerialNumbers> serialNumbers) {

        ItemCode = itemCode;
        WarehouseCode = warehouseCode;
        Quantity = quantity;
        BaseType = baseType;
        BaseEntry = baseEntry;
        BaseLine = baseLine;
        SerialNumbers = serialNumbers;

    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getWarehouseCode() {
        return WarehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        WarehouseCode = warehouseCode;
    }

    public List<SalesOrderDocumentLinesSerialNumbers> getSerialNumbers() {
        return SerialNumbers;
    }

    public void setSerialNumbers(List<SalesOrderDocumentLinesSerialNumbers> serialNumbers) {
        SerialNumbers = serialNumbers;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getBaseType() {
        return BaseType;
    }

    public void setBaseType(int baseType) {
        BaseType = baseType;
    }

    public int getBaseEntry() {
        return BaseEntry;
    }

    public void setBaseEntry(int baseEntry) {
        BaseEntry = baseEntry;
    }

    public int getBaseLine() {
        return BaseLine;
    }

    public void setBaseLine(int baseLine) {
        BaseLine = baseLine;
    }
}
