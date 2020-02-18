package com.logistics.riftvalley.ui.StockMovements.StockDisposals;

public interface _StockDisposalsPresenter {

    void initializeView(_StockDisposalsView stockDisposalsView);

    void requestSystemNumber(String serialNumber, String warehouseCode, int source);

    void success(boolean isSuccessful);

}
