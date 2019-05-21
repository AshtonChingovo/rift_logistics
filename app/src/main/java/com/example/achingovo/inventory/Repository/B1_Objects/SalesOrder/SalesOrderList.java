package com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder;

public class SalesOrderList {

    String cardCode;
    String cardName;
    String itemCode;
    int quantity;
    int docEntry;

    public SalesOrderList(String cardCode, String cardName, String itemCode, int quantity, int docEntry) {
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.docEntry = docEntry;
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


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(int docEntry) {
        this.docEntry = docEntry;
    }
}
