package com.logistics.riftvalley.ui.StockMovements.ProductionReturn;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;

public class ProductionReturnPresenter implements _ProductionReturnPresenter {

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to View
    _ProductionReturnView productionReturnView;

    public ProductionReturnPresenter() {
        dataManager.initializeProductionReturnPresenter(this);
    }


    @Override
    public void initializeView(_ProductionReturnView productionReturnView) {
        this.productionReturnView = productionReturnView;
    }

    @Override
    public void requestStockTransfer(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    @Override
    public void success(boolean isSuccessful) {
        productionReturnView.success(isSuccessful);
    }


}
