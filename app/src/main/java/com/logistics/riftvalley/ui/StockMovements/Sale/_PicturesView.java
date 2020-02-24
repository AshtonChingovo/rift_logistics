package com.logistics.riftvalley.ui.StockMovements.Sale;

import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;

import java.util.List;

public interface _PicturesView {

    void picturesList(List<PicturesDB> picturesList);

    void isSavePicturesOperationSuccessful(boolean isSuccessful);

    void isUpdatePicturesOperationSuccessful(boolean isSuccessful);

}
