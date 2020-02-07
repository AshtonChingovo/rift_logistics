package com.logistics.riftvalley.ui.NewInventory;

import com.logistics.riftvalley.data.model.Entity.Warehouses;

import java.util.List;

public interface _NewInventoryView {

    void failed();

    void success();

    // returns the list of warehouses
    void warehousesList(List<Warehouses> warehousesList);

}
