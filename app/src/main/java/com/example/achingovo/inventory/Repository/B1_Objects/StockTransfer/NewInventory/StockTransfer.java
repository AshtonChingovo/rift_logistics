package com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory;

import java.util.List;

public class StockTransfer {

    String FromWarehouse;
    String ToWarehouse;
    List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLines> StockTransferLines;

    public StockTransfer(String fromWarehouse, String toWarehouse, List<StockTransferLines> stockTransferLines) {
        FromWarehouse = fromWarehouse;
        ToWarehouse = toWarehouse;
        StockTransferLines = stockTransferLines;
    }

    public String getFromWarehouse() {
        return FromWarehouse;
    }

    public void setFromWarehouse(String fromWarehouse) {
        FromWarehouse = fromWarehouse;
    }

    public String getToWarehouse() {
        return ToWarehouse;
    }

    public void setToWarehouse(String toWarehouse) {
        ToWarehouse = toWarehouse;
    }

    public List<StockTransferLines> getStockTransferLines() {
        return StockTransferLines;
    }

    public void setStockTransferLines(List<com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLines> stockTransferLines) {
        StockTransferLines = stockTransferLines;
    }
}
