package com.logistics.riftvalley.Utilities;

public class PublicStaticVariables {

    // activity ids
    public static final int WAREHOUSE_NAME_CODE_LIST_ACTIVITY = 1;
    public static final int WAREHOUSE_LOCATIONS_MOVEMENTS_ACTIVITY = 2;
    public static final int SCAN_NEW_INVENTORY = 3;
    public static final int IN_WAREHOUSE_ACTIVITY = 4;
    public static final int MOVE_TO_DISPATCH_ACTIVITY = 5;
    public static final int WAREHOUSE_CHECK_OUT_LOCATIONS_LIST = 6;
    public static final int CHECK_OUT = 7;
    public static final int CHECK_IN = 8;
    public static final int PRODUCTION_RETURN = 9;
    public static final int STOCK_DISPOSALS = 10;
    public static final int MOVE_TO_FUMIGATION_SALES = 11;
    public static final int MOVE_TO_DISPATCH_SALES = 12;
    public static final int GRADE_RECLASSIFICATION = 13;

    // serial number SAP B1 item code
    public static String ITEM_CODE = "";
    public static int SERIAL_NUMBER_ID;

    public static final int SALES_BASE_TYPE = 17;
    public static final int SALES_BASE_LINE = 0;

    /*
    *  Warehouse Names
    * */


    /*
    *   Warehouse codes
    */
    // TPZ warehouse where new stock is coming from
    public static final String TPZ_WAREHOUSE = "TPZ";
    public static final String TRANSIT_WAREHOUSE = "TRANSIT";
    public static final String BAY_10_WAREHOUSE = "LOGBAY10";

    /*
    *  Bin locations
    * */
    public static final String RECEIVING_AREA = "REC_A";
    public static final String DISPATCH_AREA = "DISPATCH";
    public static final String FUMIGATION_AREA = "FUMIGATION";
    public static final String OVERFLOW_AREA = "OVERFLOW";

    public static final String SCAN_BARCODE1 = "SCAN_BARCODE1";

    /*
    *   Intent tags
    * */
    public static String WAREHOUSE_CODE = "code";
    public static String WAREHOUSE_NAME = "warehouseName";

    public static int SERIAL_AND_BATCH_NUMBER_BASELINE = 0;

}
