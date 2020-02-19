package com.logistics.riftvalley.ui.StockMovements.Transfers;

import java.util.Map;

public interface _GradeReclassificationView {

    void lotNumber(Map<String, String> lotNumbersMap);

    void doesSerialNumberExistSAP(boolean doesSerialNumberExistSAP, int systemNumber, int binAbsEntry);

    void reclassifyResult(boolean isSuccessful);

}
