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

    // serial number SAP B1 item code
    public static String ITEM_CODE = "FF2RMS";

    /*
    *   Warehouse codes
    */
    // TPZ warehouse where new stock is coming from
    public static final String TPZ_WAREHOUSE = "TPZ";
    public static final String TRANSIT_WAREHOUSE = "TRANSIT";
    public static final String BAY_10_WAREHOUSE = "LOGBAY10";

    public static final String DISPATCH_AREA = "DISPATCH";

    public static final String SCAN_BARCODE1 = "SCAN_BARCODE1";


    /*
    *   Intent tags
    * */
    public static String WAREHOUSE_CODE = "code";
    public static String WAREHOUSE_NAME = "warehouseName";

}
