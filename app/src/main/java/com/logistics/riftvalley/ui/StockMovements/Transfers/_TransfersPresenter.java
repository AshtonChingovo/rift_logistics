package com.logistics.riftvalley.ui.StockMovements.Transfers;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _TransfersPresenter {

    void initializeTransfersView( _TransfersView transfersView);

    void requestStockTransfer();

    void transferRequestResponse(boolean isSuccessful);

    void requestWarehousesList(int source);

    void warehouses(List<Warehouses> warehousesList);

    // request system number of the scanned serial number
    void requestSystemNumber(String serialNumber, String warehouseCode, int source);

    // system number request result
    void serialNumberSystemNumber(int systemNumber);

}
