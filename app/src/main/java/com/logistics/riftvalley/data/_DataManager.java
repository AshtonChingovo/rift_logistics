package com.logistics.riftvalley.data;

import com.logistics.riftvalley.data.model._API_Helper;
import com.logistics.riftvalley.ui.Login._LoginPresenter;
import com.logistics.riftvalley.ui.NewInventory._NewInventoryPresenter;
import com.logistics.riftvalley.ui.StockMovements.ProductionReturn._ProductionReturnPresenter;
import com.logistics.riftvalley.ui.StockMovements.Sale._SalesPresenter;
import com.logistics.riftvalley.ui.StockMovements.StockDisposals._StockDisposalsPresenter;
import com.logistics.riftvalley.ui.StockMovements.Transfers._TransfersPresenter;
import com.logistics.riftvalley.ui.StockMovements._WarehouseLocations_MovementsPresenter;

public interface _DataManager extends _API_Helper {

    void initializeLoginPresenter(_LoginPresenter loginPresenter);

    void initializeNewInventoryPresenter(_NewInventoryPresenter newInventoryPresenter);

    void initializeStockMovementsWarehouseListPresenter(_WarehouseLocations_MovementsPresenter warehouseLocations_movementsPresenter);

    void initializeTransfersPresenter(_TransfersPresenter transfersPresenter);

    void initializeProductionReturnPresenter(_ProductionReturnPresenter productionPresenter);

    void initializeStockDisposalsPresenter(_StockDisposalsPresenter stockDisposalsPresenter);

    void initializeSalesPresenter(_SalesPresenter salesPresenter);

}
