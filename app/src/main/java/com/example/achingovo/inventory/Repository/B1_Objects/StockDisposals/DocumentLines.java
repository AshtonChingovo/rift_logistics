package com.example.achingovo.inventory.Repository.B1_Objects.StockDisposals;

import com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers;

import java.util.ArrayList;
import java.util.List;

public class DocumentLines {

    List<StockDisposalsEntity> DocumentLines = new ArrayList<>();

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
