package com.logistics.riftvalley.Retrofit;

import com.logistics.riftvalley.data.model.SalesOrder.DeliveryDocument;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.Entity.ManufacturingSerialNumber;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @POST("Login")
    Call<Object> login(@Body Login login);

    @GET("Warehouses?$select=WarehouseName,WarehouseCode&$filter=GlobalLocationNumber eq '1'&$orderby=WarehouseName asc")
    Call<JsonObject> getWarehouses(@Header("Cookie") String cookie);

    // get System Number of carton in the TPZ warehouse
    @GET
    Call<JsonObject> getCartonAndSystemNumber(@Header("Cookie") String cookie, @Url String serialNumberUrl);

    @GET
    Call<JsonObject> getBinLocationAbsEntryNumber(@Header("Cookie") String cookie, @Url String stackLocationUrl);

    @GET()
    Call<JsonObject> getSalesOrdersList(@Header("Cookie") String cookie, @Url String salesOrdersUrl);

    @GET()
    Call<JsonObject> getSerialNumberId(@Header("Cookie") String cookie, @Url String serialNumberIdUrl);

    @GET()
    Call<JsonObject> getSerialNumber(@Header("Cookie") String cookie, @Url String serialNumber);

    @GET()
    Call<JsonObject> getSalesOrderQuantity(@Header("Cookie") String cookie, @Url String salesOrderQuantityUrl);

    @GET()
    Call<JsonObject> getLotNumbers(@Header("Cookie") String cookie, @Url String lotNumbersUrl);

    @PATCH("SerialNumberDetails({shippingCaseNumber})")
    Call<JsonObject> patchShippingCaseNumber(@Header("Cookie") String cookie, @Path("shippingCaseNumber") int shippingCaseNumber, @Body ManufacturingSerialNumber manufacturingSerialNumber);

    @POST("StockTransfers")
    Call<JsonObject> stockTransfer(@Header("Cookie") String cookie, @Body StockTransfer stockTransfer);

    @POST("DeliveryNotes")
    Call<JsonObject> createDeliveryNote(@Header("Cookie") String cookie, @Body DeliveryDocument deliveryDocument);

    @POST("Orders{docEntry}/Close")
    Call<JsonObject> closeSalesOrder(@Header("Cookie") String cookie, @Path("docEntry") int docEntry);

    @POST("InventoryGenExits")
    Call<JsonObject> stockDisposal(@Header("Cookie") String cookie, @Body DocumentLines documentLines);



}
