package com.logistics.riftvalley.ui.StockMovements.Transfers;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _TransfersView {

    void success(boolean isSuccessful);

    void warehouses(List<Warehouses> warehouses);

}
