package com.logistics.riftvalley.ui.StockMovements.ProductionReturn;

public interface _ProductionReturnPresenter {

    void initializeView(_ProductionReturnView productionReturnView);

    void requestStockTransfer(String serialNumber, String warehouseCode, int source);

    void success(boolean isSuccessful);

}
