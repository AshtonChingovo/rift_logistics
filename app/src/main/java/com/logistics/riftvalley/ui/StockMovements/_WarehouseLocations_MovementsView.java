package com.logistics.riftvalley.ui.StockMovements;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _WarehouseLocations_MovementsView {

    void failed();

    // returns the list of warehouses
    void warehousesList(List<Warehouses> warehousesList);


}
