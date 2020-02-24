package com.logistics.riftvalley.ui.StockMovements.Sale;

import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;

import java.util.List;

public interface _SalesView {

    void success(boolean isSuccessful);

    void salesOrdersList(List<SalesOrderList> salesOrderLists);

    void deliveryNotesList(List<DeliveryNote> deliveryNoteList);

    void dispatchProcessResponse(boolean isSuccessful, String message, String operationSource);

    void dispatchGoodsResponse(boolean isSuccessful, String message);

    void isShippingCaseNumberAdded(boolean isSuccessful, String message, SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers);

}
