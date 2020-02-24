package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryDocument;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrdersDocumentLines;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dispatch extends AppCompatActivity implements DispatchDialog.DispatchDialogListener, ShippingCaseNumberDialog.ScanDialog, _SalesView{

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView count;
    ImageView backgroundImage;
    ProgressBar progressBar;
    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    String barcode;
    String shippingCaseNumber;
    List<SalesOrderDocumentLinesSerialNumbers> barcodes = new ArrayList<>();

    // dispatch methods
    final String EXISTS = "EXISTS";

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        count = findViewById(R.id.count);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

/*
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);
*/

        // initialize view in Presenter
        salesPresenter.initializeView(this);

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null)
                    return;
                else{

                    backgroundImage.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    unregisterReceiver(mReceiver);

                    // Dialog for adding Shipping Case number
                    barcode = intent.getStringExtra("SCAN_BARCODE1");

                    Bundle args = new Bundle();
                    args.putString("barcode", intent.getStringExtra("SCAN_BARCODE1"));

                    Log.d("DispatchScan", " *** 1st point *** ");

                    // check if serial number has not been scanned already
                    for(SalesOrderDocumentLinesSerialNumbers scannedBarcodes: barcodes){
                        if(scannedBarcodes.getInternalSerialNumber().equalsIgnoreCase(barcode) || scannedBarcodes.getInternalSerialNumber().equalsIgnoreCase("B_" + barcode)){
                            Toast.makeText(Dispatch.this, "Sorry, carton has already been scanned", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);

                            mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
                            Dispatch.this.registerReceiver(mReceiver, mFilter);

                            return;
                        }
                    }

                    Log.d("DispatchScan", " *** Over HERE *** ");

                    DialogFragment dialog = new ShippingCaseNumberDialog();
                    dialog.setArguments(args);
                    dialog.show(getSupportFragmentManager(), "Dialog");

                }
            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void success(boolean isSuccessful) {

        progressBar.setVisibility(View.GONE);

        if(barcodes.size() > 0) {

            int validSerialNumbersCount = 0;

            for(SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers : barcodes){

                if(salesOrderDocumentLinesSerialNumbers.getInternalSerialNumber().startsWith("B_"))
                    continue;
                else
                    validSerialNumbersCount += 1;

            }

            String validSerialNumbers = validSerialNumbersCount + "/" + SharedPreferencesClass.getSalesOrderQuantity();

            count.setText(validSerialNumbers);

            backgroundImage.setVisibility(View.INVISIBLE);
            count.setVisibility(View.VISIBLE);

        }
        else{
            backgroundImage.setVisibility(View.VISIBLE);
            count.setVisibility(View.INVISIBLE);
        }

        recyclerView.getAdapter().notifyDataSetChanged();

        Dispatch.this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void salesOrdersList(List<SalesOrderList> salesOrderLists) {

    }

    @Override
    public void deliveryNotesList(List<DeliveryNote> deliveryNoteList) {

    }

    @Override
    public void dispatchProcessResponse(boolean isSuccessful, String message, String operationSource) {

        // if isSuccessful returns true then SerialNumber exists in SAP thus set Shipping Case Number to the SAP serial number
        if(isSuccessful){
            salesPresenter.setShippingCaseNumberForSerialNumberSAP(barcode, shippingCaseNumber);
        }
        else{
            barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(null, "B_" + barcode, 0));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            success(false);
        }
    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {

        progressBar.setVisibility(View.INVISIBLE);

        if(isSuccessful){
            Toast.makeText(Dispatch.this, "Goods successfully dispatched", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(Dispatch.this, "Sorry, operation failed", Toast.LENGTH_SHORT).show();
            // finish();
        }
    }

    @Override
    public void isShippingCaseNumberAdded(boolean isSuccessful, String message, SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers) {

        if(isSuccessful){
            // add barcode to list
            barcodes.add(0, salesOrderDocumentLinesSerialNumbers);
            success(true);
        }
        else{
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(null, "B_" + barcode, 0));
            success(false);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(Dispatch.this).inflate(R.layout.scanned_cartons_list_items_with_delete_option, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return barcodes.size();
        }

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView barcode;
        ImageView barcodeImg;
        ImageView delete;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.warehouseName);
            barcodeImg = itemView.findViewById(R.id.barcodeImg);
            delete = itemView.findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    barcodes.remove(getAdapterPosition());
                    recyclerView.getAdapter().notifyDataSetChanged();

                    if(barcodes.size() > 0){
                        count.setVisibility(View.VISIBLE);
                        count.setText(barcodes.size() + "/" + SharedPreferencesClass.getSalesOrderQuantity());
                        return;
                    }

                    count.setVisibility(View.INVISIBLE);

                }
            });

        }

        public void bind(int position) {
            if(barcodes.get(position).getInternalSerialNumber().startsWith("B_")){
                delete.setVisibility(View.INVISIBLE);
                barcode.setTextColor(getResources().getColor(R.color.red));
                barcodeImg.setImageResource(R.drawable.badcode);
                barcode.setText("" + barcodes.get(position).getInternalSerialNumber().substring(2));
                return;
            }
            else{
                barcode.setTextColor(getResources().getColor(R.color.fontColor));
                barcodeImg.setImageResource(R.drawable.barcode_background);
                barcode.setText("" + barcodes.get(position).getInternalSerialNumber());
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dispatch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(barcodes.size() != SharedPreferencesClass.getSalesOrderQuantity()){
            Toast.makeText(this, SharedPreferencesClass.getSalesOrderQuantity() + " required to Dispatch" , Toast.LENGTH_SHORT).show();
            return true;
        }

        if(barcodes.size() > 0){
            DialogFragment dialog = new DispatchDialog();
            dialog.show(getSupportFragmentManager(), "Dialog");
        }

        return true;
    }

    public class CheckSerialNumber extends AsyncTask<String, Void, Void> {

        boolean isInList = false;
        boolean max = false;
        JSONObject jsonResponse;

        @Override
        protected Void doInBackground(String... values) {

            try {

                // value[0] = serialNumber
                jsonResponse = new JSONObject(RetrofitInstance.getSerialNumber(SharedPreferencesClass.getCookie(), values[0]));

                JSONArray jsonArray = jsonResponse.getJSONArray("value");

                if(jsonArray.length() > 0){

                    /*
                    *   1. Check for cartons that may have already been scanned
                    *   2. Add shipping case number
                    *   3. Check that the total scanned cartons matches the total required for the Sales Order
                    *   4. Add carton serialNumber to the barcodes list
                    * */

                    for(int i = 0; i < jsonArray.length(); ++i){

                        // replace with Java Stream
                        for(SalesOrderDocumentLinesSerialNumbers scannedBarcodes: barcodes){
                            if(scannedBarcodes.getInternalSerialNumber().equals(values[0].trim())){
                                isInList = true;
                                return null;
                            }
                        }

                        // Add shipping case number to serial number
                        // values[0] = serialNumber
                        // values[1] = shippingCaseNumber
                        if(!RetrofitInstance.setShippingCaseNumber(SharedPreferencesClass.getCookie(), values[0], Integer.valueOf(values[1].trim()))) {
                            barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(null, "B_" + values[0], 0));
                            return null;
                        }

                        // check if max has not been reached yet
                        if(barcodes.size() == SharedPreferencesClass.getSalesOrderQuantity()){
                            max = true;
                            return null;
                        }

                        // add barcode to list
                        barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(jsonArray.getJSONObject(i).getString("MfrSerialNo"),
                                jsonArray.getJSONObject(i).getString("SerialNumber"), jsonArray.getJSONObject(i).getInt("SystemNumber")));

                    }
                }
                // Carton not found in SAP
                else
                    barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(null, "B_" + values[0], 0));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);

            if(max)
                Toast.makeText(Dispatch.this, "Maximum number of cartons reached", Toast.LENGTH_SHORT).show();

            if(isInList)
                Toast.makeText(Dispatch.this, "Carton has already been scanned", Toast.LENGTH_SHORT).show();

            if(barcodes.size() > 0) {
                backgroundImage.setVisibility(View.INVISIBLE);
                count.setVisibility(View.VISIBLE);
            }
            else{
                backgroundImage.setVisibility(View.VISIBLE);
                count.setVisibility(View.INVISIBLE);
                return;
            }

            count.setText(barcodes.size() + "/" + SharedPreferencesClass.getSalesOrderQuantity());
            recyclerView.getAdapter().notifyDataSetChanged();

            Dispatch.this.registerReceiver(mReceiver, mFilter);

        }
    }

    public class CreateDeliveryNote extends AsyncTask<String, Void, Void> {

        List<SalesOrderDocumentLinesSerialNumbers> validBarcodes = new ArrayList<>();

        int SALES_BASE_TYPE = 17;
        int SALES_BASE_LINE = 0;
        JSONObject jsonResponseSalesOrderQuantity;
        boolean deliveryCreated;

        @Override
        protected Void doInBackground(String... values) {

            List<SalesOrdersDocumentLines> documentLines = new ArrayList<>();

            for(int i = 0; i < barcodes.size(); ++i){
                if(barcodes.get(i).getInternalSerialNumber().startsWith("B_"))
                    continue;
                else
                    validBarcodes.add(barcodes.get(i));
            }

            documentLines.add(new SalesOrdersDocumentLines(SharedPreferencesClass.getSalesOrderItemCode(), SharedPreferencesClass.getWarehouseCode(), validBarcodes.size(), SALES_BASE_TYPE, SharedPreferencesClass.getSalesOrderDocEntry(), SALES_BASE_LINE, validBarcodes));

            if(RetrofitInstance.createDeliveryNote(SharedPreferencesClass.getCookie(),
                    new DeliveryDocument(SharedPreferencesClass.getSalesOrderCardCode(), documentLines))){

                deliveryCreated = true;

                String jsonSalesOrderQuantity = RetrofitInstance.getSalesOrdersQuantity(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getSalesOrderDocEntry());

                if(jsonSalesOrderQuantity != null){

                    try {
                        jsonResponseSalesOrderQuantity = new JSONObject(jsonSalesOrderQuantity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);

            if(deliveryCreated){
                Toast.makeText(Dispatch.this, "Goods successfully dispatched", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public void dispatchDialogOkClicked() {
        progressBar.setVisibility(View.VISIBLE);
        // new CreateDeliveryNote().execute();

        salesPresenter.dispatchGoods(barcodes);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void ScanDialogOkClicked(final String serialNumber, String shippingCaseNumber) {
        this.shippingCaseNumber = shippingCaseNumber;

/*        if(barcodes.stream().filter(new Predicate<SalesOrderDocumentLinesSerialNumbers>() {
            @Override
            public boolean test(SalesOrderDocumentLinesSerialNumbers s) {
                return s.getInternalSerialNumber().equalsIgnoreCase(serialNumber);
            }
        }).count() > 0){
            Toast.makeText(this, "Sorry, carton has already been scanned", Toast.LENGTH_LONG).show();
            return;
        }*/

        // new CheckSerialNumber().execute(serialNumber, shippingCaseNumber);
        salesPresenter.doesSerialNumberExistInSAP(serialNumber, EXISTS);

    }

    @Override
    public void ScanDialogCancelClicked() {

    }

}
