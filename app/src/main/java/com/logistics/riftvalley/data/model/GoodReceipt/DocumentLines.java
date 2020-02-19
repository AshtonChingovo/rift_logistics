package com.logistics.riftvalley.data.model.GoodReceipt;

import java.util.List;

public class DocumentLines {

    public List<DocumentLineProperties> DocumentLines;

    public DocumentLines(List<DocumentLineProperties> documentLines) {
        DocumentLines = documentLines;
    }

    public List<DocumentLineProperties> getDocumentLines() {
        return DocumentLines;
    }

    public void setDocumentLines(List<DocumentLineProperties> documentLines) {
        DocumentLines = documentLines;
    }

}
