package com.logistics.riftvalley.ui.StockMovements.Sale;

import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.SALES_BASE_LINE;
import static com.logistics.riftvalley.Utilities.PublicStaticVariables.SALES_BASE_TYPE;

public class SalesPresenter implements _SalesPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to view
    _SalesView salesView;

    String operationSource;

    public SalesPresenter() {
        dataManager.initializeSalesPresenter(this);
    }

    @Override
    public void initializeView(_SalesView salesView) {
        this.salesView = salesView;
    }

    @Override
    public void requestSalesOrdersList() {
        dataManager.requestSalesOrderList();
    }

    @Override
    public void moveToFumigation(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    @Override
    public void moveToDispatch(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);

    }

    @Override
    public void dispatchGoods(List<SalesOrderDocumentLinesSerialNumbers> barcodes) {

        List<SalesOrderDocumentLinesSerialNumbers> validBarcodes = new ArrayList<>();
        List<SalesOrdersDocumentLines> documentLines = new ArrayList<>();

        for(int i = 0; i < barcodes.size(); ++i){
            if(barcodes.get(i).getInternalSerialNumber().startsWith("B_"))
                continue;
            else
                validBarcodes.add(barcodes.get(i));
        }

        documentLines.add(new SalesOrdersDocumentLines(SharedPreferencesClass.getSalesOrderItemCode(), SharedPreferencesClass.getWarehouseCode(), validBarcodes.size(), SALES_BASE_TYPE, SharedPreferencesClass.getSalesOrderDocEntry(), SALES_BASE_LINE, validBarcodes));

        dataManager.dispatchGoods(documentLines);

    }

    @Override
    public void success(boolean isSuccessful) {
        salesView.success(isSuccessful);
    }

    @Override
    public void salesOrdersList(List<SalesOrderList> salesOrderLists) {
        salesView.salesOrdersList(salesOrderLists);
    }

    @Override
    public void doesSerialNumberExistInSAP(String serialNumber, String operationSource) {
        this.operationSource = operationSource;
        dataManager.doesSerialNumberExistInSAP(serialNumber);
    }

    @Override
    public void setShippingCaseNumberForSerialNumberSAP(String serialNumber, String shippingCaseNumber) {
        dataManager.setShippingCaseNumberToSerialNumber(serialNumber, shippingCaseNumber);
    }

    @Override
    public void dispatchProcessesRequests(boolean isSuccessful, String message) {
        salesView.dispatchProcessResponse(isSuccessful, message, operationSource);
    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {
        salesView.dispatchGoodsResponse(isSuccessful, message);
    }

    @Override
    public void shippingCaseNumber(boolean isSuccessful, String message, JSONArray jsonArray) {
        if(isSuccessful){
            try {
                 salesView.isShippingCaseNumberAdded(true, message, new SalesOrderDocumentLinesSerialNumbers(jsonArray.getJSONObject(0).getString("MfrSerialNo"),
                         jsonArray.getJSONObject(0).getString("SerialNumber"), jsonArray.getJSONObject(0).getInt("SystemNumber")));
            } catch (JSONException e) {
                salesView.isShippingCaseNumberAdded(false, message, null);
                e.printStackTrace();
            }
        }
        else
            salesView.isShippingCaseNumberAdded(false, message, null);

    }



}
