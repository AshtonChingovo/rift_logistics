package com.logistics.riftvalley.ui.StockMovements.Transfers;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Warehouses;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class TransfersPresenter implements _TransfersPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // References to views
    _TransfersView transfersView;
    _GradeReclassificationView gradeReclassificationView;

    public TransfersPresenter() {
        dataManager.initializeTransfersPresenter(this);
    }

    @Override
    public void initializeTransfersView( _TransfersView transfersView) {
        this.transfersView = transfersView;
    }

    @Override
    public void initializeTransfersView(_GradeReclassificationView gradeReclassificationView) {
        this.gradeReclassificationView = gradeReclassificationView;
    }

    @Override
    public void requestStockTransfer() {

    }

    @Override
    public void transferRequestResponse(boolean isSuccessful) {
        transfersView.success(isSuccessful);
    }

    @Override
    public void requestWarehousesList(int source) {
        dataManager.requestListOfWarehouses(WAREHOUSE_CHECK_OUT_LOCATIONS_LIST);
    }

    @Override
    public void warehouses(List<Warehouses> warehousesList) {

        List<Warehouses> filteredList = new ArrayList<>();

        // remove Bay10 from list because it does not receive any carton from other warehousesCheckOutOptionsList
        for(Warehouses warehousesObject : warehousesList){
            if(warehousesObject.getCode().equalsIgnoreCase(BAY_10_WAREHOUSE))
                continue;

            filteredList.add(warehousesObject);

        }

        transfersView.warehouses(filteredList);

    }

    @Override
    public void requestSystemNumber(String serialNumber, String warehouseCode, int source) {
        dataManager.requestSerialNumberSystemNumber(serialNumber, null, source);
    }

    @Override
    public void serialNumberSystemNumber(int systemNumber) {
        transfersView.success(false);
    }

    @Override
    public void getLotNumberList() {
        dataManager.getLotNumbers();
    }

    @Override
    public void lotNumbers(String lotNumbersJsonString) {
        // process return lotNumbers if the response is not null
        Map<String, String> lotNumbersMap = new HashMap<>();

        //List<LotNumbers> lotNumbers = new ArrayList<>();

        if(lotNumbersJsonString != null){

            try {

                JSONObject jsonObject;

                jsonObject = new JSONObject(lotNumbersJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("value");

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject lotNumberObject = jsonArray.getJSONObject(i).getJSONObject("BlanketAgreements/BlanketAgreements_ItemsLines");

                    lotNumbersMap.put(lotNumberObject.getString("ItemNo"),
                            lotNumberObject.getString("U_PRFX") + lotNumberObject.getString("U_LotNumber") + lotNumberObject.getString("U_SFFX"));

/*
                    lotNumbers.add(new LotNumbers(lotNumberObject.getString("ItemNo"),
                            lotNumberObject.getString("U_PRFX") + lotNumberObject.getString("U_LotNumber") + lotNumberObject.getString("U_SFFX")));
*/

                }

                gradeReclassificationView.lotNumber(lotNumbersMap);

            }
            catch (Exception e){
                gradeReclassificationView.lotNumber(null);
            }

        }

    }

}
