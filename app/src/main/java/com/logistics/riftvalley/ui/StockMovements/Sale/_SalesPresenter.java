package com.logistics.riftvalley.ui.StockMovements.Sale;

import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import org.json.JSONArray;

import java.util.List;

public interface _SalesPresenter {

    void initializeView(_SalesView salesView);

    void requestSalesOrdersList();

    void moveToFumigation(String serialNumber, String warehouseCode, int source);

    void moveToDispatch(String serialNumber, String warehouseCode, int source);

    void dispatchGoods(List<SalesOrderDocumentLinesSerialNumbers> documentLines);

    void success(boolean isSuccessful);

    void salesOrdersList(List<SalesOrderList> salesOrderLists);

    void doesSerialNumberExistInSAP(String serialNumber, String operationSource);

    void setShippingCaseNumberForSerialNumberSAP(String serialNumber, String shippingCaseNumber);

    void dispatchProcessesRequests(boolean isSuccessful, String message);

    void dispatchGoodsResponse(boolean isSuccessful, String message);

    void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray);

}
