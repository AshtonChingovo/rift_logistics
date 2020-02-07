package com.logistics.riftvalley.data.model.SalesOrder;

import java.util.List;

public class DeliveryDocument {

    String CardCode;
    List<SalesOrdersDocumentLines> DocumentLines;

    public DeliveryDocument(String cardCode, List<SalesOrdersDocumentLines> documentLines) {
        CardCode = cardCode;
        DocumentLines = documentLines;
    }

    public String getCardCode() {
        return CardCode;
    }

    public void setCardCode(String cardCode) {
        CardCode = cardCode;
    }

    public List<SalesOrdersDocumentLines> getDocumentLines() {
        return DocumentLines;
    }

    public void setDocumentLines(List<SalesOrdersDocumentLines> documentLines) {
        DocumentLines = documentLines;
    }
}
