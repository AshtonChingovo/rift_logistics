package com.logistics.riftvalley.ui.StockMovements;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class WarehouseLocations_MovementPresenter implements _WarehouseLocations_MovementsPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to _WarehouseLocations_MovementsView
    _WarehouseLocations_MovementsView warehouseLocations_movementsView;

    public WarehouseLocations_MovementPresenter() {
        dataManager.initializeStockMovementsWarehouseListPresenter(this);
    }

    @Override
    public void initializeWarehouseLocations_MovementsView(_WarehouseLocations_MovementsView warehouseLocations_movementView) {
        this.warehouseLocations_movementsView = warehouseLocations_movementView;
    }

    @Override
    public void requestActiveWarehousesList() {
        dataManager.requestListOfWarehouses(WAREHOUSE_LOCATIONS_MOVEMENTS_ACTIVITY);
    }

    @Override
    public void warehousesList(List<Warehouses> warehousesList) {
        if(warehousesList == null)
            warehouseLocations_movementsView.failed();
        else
            warehouseLocations_movementsView.warehousesList(warehousesList);
    }

}
