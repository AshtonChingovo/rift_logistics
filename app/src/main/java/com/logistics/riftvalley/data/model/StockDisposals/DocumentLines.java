package com.logistics.riftvalley.data.model.StockDisposals;

import java.util.List;

public class DocumentLines {

    List<StockDisposalsEntity> DocumentLines;

    public DocumentLines(List<StockDisposalsEntity> documentLines) {
        DocumentLines = documentLines;
    }

    public List<StockDisposalsEntity> getDocumentLines() {
        return DocumentLines;
    }

    public void setDocumentLines(List<StockDisposalsEntity> documentLines) {
        DocumentLines = documentLines;
    }

}
