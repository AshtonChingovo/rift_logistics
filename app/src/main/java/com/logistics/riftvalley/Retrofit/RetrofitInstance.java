package com.logistics.riftvalley.Retrofit;

import android.util.Log;

import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.data.model.Entity.SerialNumbersPatchObject;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryDocument;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.Entity.ManufacturingSerialNumber;
import com.logistics.riftvalley.data.model.Entity.Login;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
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

    public static Retrofit getSwagggerAPI(){

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
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
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
                    .baseUrl("http://192.168.14.144/LAPI/swagger")
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

        String url = "$crossjoin(Orders,Orders/DocumentLines)?$expand=Orders($select=CardCode,CardName,DocEntry), " +
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
            return null;
        }


    }

    public static String getDeliveryNotesList(String cookie, String warehouseCode){

        String url = "$crossjoin(DeliveryNotes,DeliveryNotes/DocumentLines)?$expand=DeliveryNotes($select=CardName,DocEntry), " +
                "DeliveryNotes/DocumentLines($select=DeliveryNotes/DocumentLines/DocEntry,WarehouseCode,LineNum)" +
                "&$filter=DeliveryNotes/DocEntry eq DeliveryNotes/DocumentLines/DocEntry " +
                "and DeliveryNotes/DocumentLines/WarehouseCode eq '" + warehouseCode + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitGET = retrofitAPI.getDeliveryNotesList(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){
                return response.body().toString();
            }
            else if(response.code() == 401){

                if(response.isSuccessful() && response.code() == 200){
                    return response.body().toString();
                }

                return null;

            }
            else
                return null;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ScanningProcess", "SalesOrders# Error: " + e.toString());
            return null;
        }


    }

    public static Boolean setShippingCaseNumber(String cookie, String serialNumber, int shippingCaseNumber){

        /*
        *   1. Get carton DocEntry
        *   2. Update ManufacturingSerialNumber value
        * */

        String url = "SerialNumberDetails?$select = DocEntry &$filter=SerialNumber eq '" + serialNumber + "'";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitPatch;
        Call retrofitGET = retrofitAPI.getSerialNumberId(cookie, url);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 200){

                JSONObject jsonObject = new JSONObject(response.body().toString());

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

        String url = "$crossjoin(Orders,Orders/DocumentLineProperties)?$expand=Orders/DocumentLineProperties($select=Quantity) &$filter=Orders/DocEntry eq Orders/DocumentLineProperties/DocEntry and Orders/DocEntry  eq "+ docEntry +"";

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

    public static String getLotNumbers(String cookie){

        String url = "$crossjoin(BlanketAgreements,BlanketAgreements/BlanketAgreements_ItemsLines)?$expand=BlanketAgreements/BlanketAgreements_ItemsLines($select=ItemNo,U_LotNumber,U_PRFX,U_SFFX)";

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.getLotNumbers(cookie, url);

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

    public static boolean uploadPicturesToSAP(String cookie, RequestBody imageString, RequestBody docEntryNumber, RequestBody imageExtention){

        RetrofitAPI retrofitAPI = getSwagggerAPI().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.uploadImage(cookie, imageString, docEntryNumber, imageExtention);

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

    public static boolean patchSerialNumberDetails(String cookie, int docEntry, SerialNumbersPatchObject serialNumbersPatchObject){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);

        Call retrofitGET = retrofitAPI.patchSerialNumberDetails(cookie, docEntry, serialNumbersPatchObject);

        try {

            response = retrofitGET.execute();

            if(response.isSuccessful() && response.code() == 204)
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("patchSerialNumber", "patchSerialNumberDetails Error: " + e.toString());
        }

        return false;

    }

    public static boolean goodsReceipt(String cookie, com.logistics.riftvalley.data.model.GoodReceipt.DocumentLines documentLines){

        RetrofitAPI retrofitAPI = getRetrofit().create(RetrofitAPI.class);
        Call retrofitPOST = retrofitAPI.goodsReceipt(cookie, documentLines);

        try {

            response = retrofitPOST.execute();

            if(response.isSuccessful() && response.code() == 201){
                return true;
            }
            else if(response.code() == 401){
                if(response.isSuccessful() && response.code() == 200){
                    Log.i("getWarehouses", "Warehouses: " + response.body());
                    return true;
                }

                return false;
            }
            else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("getWarehouses", "Error: " + e.toString());
            return false;
        }

    }


}
