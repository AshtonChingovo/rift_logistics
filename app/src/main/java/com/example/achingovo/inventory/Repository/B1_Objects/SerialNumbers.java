package com.example.achingovo.inventory.Repository.B1_Objects;

public class SerialNumbers {

    String InternalSerialNumber;
    int SystemSerialNumber;
    int Quantity;

    public SerialNumbers(String internalSerialNumber, int systemSerialNumber, int quantity) {
        InternalSerialNumber = internalSerialNumber;
        SystemSerialNumber = systemSerialNumber;
        Quantity = quantity;
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

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
