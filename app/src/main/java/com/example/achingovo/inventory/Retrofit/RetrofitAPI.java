package com.example.achingovo.inventory.Retrofit;

import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransfer;
import com.example.achingovo.inventory.Utilities.Login.Login;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @POST("StockTransfers")
    Call<JsonObject> stockTransfer(@Header("Cookie") String cookie, @Body StockTransfer stockTransfer);

    @GET()
    Call<JsonObject> getSalesOrdersList(@Header("Cookie") String cookie, @Url String salesOrdersUrl);

}
