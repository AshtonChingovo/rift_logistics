package com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder;

import java.util.List;

public class SalesOrderDocumentLinesSerialNumbers {

    String ManufacturerSerialNumber;
    String InternalSerialNumber;
    int SystemSerialNumber;

    public SalesOrderDocumentLinesSerialNumbers(String manufacturerSerialNumber, String internalSerialNumber, int systemSerialNumber) {
        ManufacturerSerialNumber = manufacturerSerialNumber;
        InternalSerialNumber = internalSerialNumber;
        SystemSerialNumber = systemSerialNumber;
    }

    public String getManufacturerSerialNumber() {
        return ManufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String manufacturerSerialNumber) {
        ManufacturerSerialNumber = manufacturerSerialNumber;
    }

    public String getInternalSerialNumber() {
        return InternalSerialNumber;
    }

    public void setInternalSerialNumber(String internalSerialNumber) {
        InternalSerialNumber = internalSerialNumber;
    }

    public int getSystemSerialNumber() {
        return SystemSerialNumber;
    }

    public void setSystemSerialNumber(int systemSerialNumber) {
        SystemSerialNumber = systemSerialNumber;
    }
}
