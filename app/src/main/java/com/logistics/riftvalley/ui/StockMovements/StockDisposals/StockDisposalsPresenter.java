package com.logistics.riftvalley.ui.StockMovements.StockDisposals;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;

public class StockDisposalsPresenter implements _StockDisposalsPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to View
    _StockDisposalsView stockDisposalsView;

    public StockDisposalsPresenter() {
        dataManager.initializeStockDisposalsPresenter(this);
    }

    @Override
    public void initializeView(_StockDisposalsView stockDisposalsView) {
        this.stockDisposalsView = stockDisposalsView;
    }

    @Override
    public void requestSystemNumber(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, warehouseCode, source);
    }

    @Override
    public void success(boolean isSuccessful) {
        stockDisposalsView.success(isSuccessful);
    }



}
