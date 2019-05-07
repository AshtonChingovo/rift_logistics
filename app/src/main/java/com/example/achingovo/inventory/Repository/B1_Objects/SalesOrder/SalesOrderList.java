package com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder;

public class SalesOrderList {

    String cardCode;
    String cardName;
    int quantity;

    public SalesOrderList(String cardCode, String cardName, int quantity) {
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.quantity = quantity;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
