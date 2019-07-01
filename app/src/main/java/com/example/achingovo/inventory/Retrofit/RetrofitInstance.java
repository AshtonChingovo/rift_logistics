package com.example.achingovo.inventory.Retrofit;

import android.util.Log;

import com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder.DeliveryDocument;
import com.example.achingovo.inventory.Repository.B1_Objects.StockDisposals.DocumentLines;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransfer;
import com.example.achingovo.inventory.Repository.Entity.DispatchPictures;
import com.example.achingovo.inventory.Repository.Entity.ManufacturingSerialNumber;
import com.example.achingovo.inventory.Repository.Entity.Login;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    /*
    *  Methods to upload reports to the server
    * */
    private static Response response = null;
    private static Response patchResponse = null;
    String loginResponse;

    public static Retrofit getRetrofit(){

        try{

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            Retrofit retroFit = new Retrofit.Builder()
                    .baseUrl("https://rivdb:50000/b1s/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            return retroFit;

        }catch (Exception e){
            Log.i("ResponseSize", "Size: Retrofit -- " + e.toString());
        }

        return null;

    }

    public static String login(Login login){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitPOST = retrofitAPI.login(login);

        try {

            response = retrofitPOST.execute();

            if(response.isSuccessful() && response.code() == 200){
                return response.headers().values("Set-Cookie").toArray()[0].toString();
            }
            else{
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ResponseSize", "Size: Retrofit -- " + e.toString());
        }

        return null;

    }

    public static String getWarehouses(String cookie){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitPOST = retrofitAPI.getWarehouses(cookie);

        try {

            response = retrofitPOST.execute();

            if(response.isSuccessful() && response.code() == 200){
                Log.i("getWarehouses", "Warehouses: " + response.body());
                return response.body().toString();
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 200){
                    Log.i("getWarehouses", "Warehouses: " + response.body());
                    return response.body().toString();
                }

                return null;
            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("getWarehouses", "Error: " + e.toString());
        }

        return null;

    }

    public static String getCartonSystemNumber(String cookie, String serialNumber){

        String url = "SerialNumberDetails?$filter=SerialNumber eq '" + serialNumber + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitGET = retrofitAPI.getCartonAndSystemNumber(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){
                Log.i("ScanningProcess", "SystemNumber: " + response.body());
                return response.body().toString();
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 200){
                    Log.i("ScanningProcess", "SystemNumber: " + response.body());
                    return response.body().toString();
                }

                return null;
            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "Sys# Error: " + e.toString());
        }

        return null;

    }

    public static String getBinLocationAbsEntryNumber(String cookie, String stackLocation){

        //String url = "BinLocations?$select=AbsEntry &$filter=Sublevel1 eq '" + stackLocation + "'";
        String url = "BinLocations?$select=AbsEntry &$filter=BinCode eq '" + stackLocation + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitGET = retrofitAPI.getBinLocationAbsEntryNumber(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){
                Log.i("ScanningProcess", "BinLocationAbsEntryNumber: " + response.body());
                return response.body().toString();
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 200){
                    Log.i("ScanningProcess", "BinLocationAbsEntryNumber: " + response.body());
                    return response.body().toString();
                }

                return null;

            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "BinLocationAbsEntry# Error: " + e.toString());
        }

        return null;

    }

    public static boolean stockTransfer(String cookie, StockTransfer stockTransfer){

        Log.i("ScanningProcess", new Gson().toJson(stockTransfer));
        Log.i("ScanningProcess", "called");

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitGET = retrofitAPI.stockTransfer(cookie, stockTransfer);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 201){
                return true;
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 201){
                    return true;
                }

                return false;
            }
            else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "StockTransfer Error: " + e.toString());
        }

        return false;

    }

    public static String getSalesOrders(String cookie, String warehouseCode){

        String url = "$crossjoin(Orders,Orders/DocumentLines)?$expand=Orders($select=CardCode, CardName,DocEntry), " +
                "Orders/DocumentLines($select=ItemCode,Quantity,RemainingOpenQuantity) &$filter=Orders/DocEntry eq Orders/DocumentLines/DocEntry and " +
                "Orders/DocumentStatus eq 'O' and Orders/DocumentLines/WarehouseCode  eq '" + warehouseCode + "' &$orderby=Orders/DocumentStatus desc, Orders/DocEntry desc";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitGET = retrofitAPI.getSalesOrdersList(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){
                Log.i("ScanningProcess", "SalesOrders: " + response.body());
                return response.body().toString();
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 200){
                    Log.i("ScanningProcess", "SalesOrders: " + response.body());
                    return response.body().toString();
                }

                return null;

            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "SalesOrders# Error: " + e.toString());
        }

        return null;

    }

    public static Boolean setShippingCaseNumber(String cookie, String serialNumber, int shippingCaseNumber){

        String url = "SerialNumberDetails?$select = DocEntry &$filter=SerialNumber eq '" + serialNumber + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitPatch;
        Call retrofitGET = retrofitAPI.getSerialNumberId(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){

                Log.i("ScanningProcess", "setShippingCaseNumber: " + response.body());

                JSONObject jsonObject = new JSONObject(response.body().toString());

                Log.i("ScanningProcess", "values: " + jsonObject.getJSONArray("value").getJSONObject(0).getInt("DocEntry"));

                if(jsonObject == null || jsonObject.getJSONArray("value") == null || jsonObject.getJSONArray("value").length() == 0)
                    return false;

                retrofitPatch = retrofitAPI.patchShippingCaseNumber(cookie, jsonObject.getJSONArray("value").getJSONObject(0).getInt("DocEntry"), new ManufacturingSerialNumber(shippingCaseNumber));

                patchResponse = retrofitPatch.execute();

                if(patchResponse.code() == 204){

                    return true;

                }

            }
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "setShippingCaseNumber Error: " + e.toString());
        }

        return false;

    }

    public static String getSerialNumber(String cookie, String serialNumber){

        String url = "SerialNumberDetails?$filter=SerialNumber eq '" + serialNumber + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.getSerialNumber(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){

                return response.body().toString();

            }
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "setShippingCaseNumber Error: " + e.toString());
        }

        return null;

    }

    public static boolean createDeliveryNote(String cookie, DeliveryDocument deliveryDocument){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.createDeliveryNote(cookie, deliveryDocument);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 201)
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "setShippingCaseNumber Error: " + e.toString());
        }

        return false;

    }

    public static String getSalesOrdersQuantity(String cookie, int docEntry){

        String url = "$crossjoin(Orders,Orders/DocumentLines)?$expand=Orders/DocumentLines($select=Quantity) &$filter=Orders/DocEntry eq Orders/DocumentLines/DocEntry and Orders/DocEntry  eq "+ docEntry +"";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.getSalesOrderQuantity(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200)
                return response.body().toString();
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "setShippingCaseNumber Error: " + e.toString());
        }

        return null;

    }

    public static boolean uploadPicturesToSAP(List<DispatchPictures> dispatchPictures){
        return true;
    }

    public static boolean stockDisposal(String cookie, DocumentLines stockDisposalEntities){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.stockDisposal(cookie, stockDisposalEntities);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 201)
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "setShippingCaseNumber Error: " + e.toString());
        }

        return false;

    }

}
