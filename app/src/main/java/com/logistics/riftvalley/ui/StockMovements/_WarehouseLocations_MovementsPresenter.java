package com.logistics.riftvalley.ui.StockMovements;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _WarehouseLocations_MovementsPresenter {

    void initializeWarehouseLocations_MovementsView(_WarehouseLocations_MovementsView warehouseLocations_movementView);

    // request warehouses list
    void requestActiveWarehousesList();

    // warehousesList from the DataManager
    void warehousesList(List<Warehouses> warehousesList);

}
