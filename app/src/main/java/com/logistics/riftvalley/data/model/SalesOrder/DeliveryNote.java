package com.logistics.riftvalley.data.model.SalesOrder;

public class DeliveryNote {

    String cardName;
    int docEntry;

    private int totalUploaded;
    private int totalPictures;

    public DeliveryNote(String cardName, int docEntry) {
        this.cardName = cardName;
        this.docEntry = docEntry;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(int docEntry) {
        this.docEntry = docEntry;
    }

    public int getTotalUploaded() {
        return totalUploaded;
    }

    public void setTotalUploaded(int totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    public int getTotalPictures() {
        return totalPictures;
    }

    public void setTotalPictures(int totalPictures) {
        this.totalPictures = totalPictures;
    }
}
