package com.example.achingovo.inventory.App.StockMovements.Sale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransfer;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLines;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoveToFumigation extends AppCompatActivity implements ShippingCaseNumberDialog.ScanDialog{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    ProgressBar progressBar;

    String barcode;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    List<String> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

/*        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);*/

        toolbar.setTitle("Move To Fumigation");

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null)
                    return;

                backgroundImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                unregisterReceiver(mReceiver);

                barcode = intent.getStringExtra("SCAN_BARCODE1");

                Bundle args = new Bundle();
                args.putString("barcode", intent.getStringExtra("SCAN_BARCODE1"));

                DialogFragment dialog = new ShippingCaseNumberDialog();
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "Dialog");


            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void ScanDialogOkClicked(String serialNumber, String shippingCaseNumber) {
        new TransferStock().execute(serialNumber, shippingCaseNumber);
    }

    @Override
    public void ScanDialogCancelClicked() {

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(MoveToFumigation.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

        ImageView barcodeImg;
        TextView barcode;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            barcodeImg = itemView.findViewById(R.id.barcodeImg);
            barcode = itemView.findViewById(R.id.barcode);

        }

        public void bind(int position) {

            if(barcodes.get(position).startsWith("B_")){
                barcode.setTextColor(getResources().getColor(R.color.red));
                barcodeImg.setImageResource(R.drawable.badcode);
                barcode.setText("" + barcodes.get(position).substring(2));
                return;
            }
            else{
                barcode.setTextColor(getResources().getColor(R.color.fontColor));
                barcodeImg.setImageResource(R.drawable.barcode_background);
                barcode.setText("" + barcodes.get(position));
                return;
            }

        }
    }

    public class TransferStock extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;
        String downloadedBinLocationAbsEntry;

        String stackLocation;

        StockTransfer stockTransferObj;

        boolean stockTransferResponse = false;

        @Override
        protected Void doInBackground(String... values) {

            // Get scanned carton's system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            stackLocation = SharedPreferencesClass.getWarehouseCode() + "-FUMIGATION";

            // Add shipping case number to serial number
            if(!RetrofitInstance.setShippingCaseNumber(SharedPreferencesClass.getCookie(), values[0], Integer.valueOf(values[1].trim()))) {
                barcode = values[0];
                stockTransferResponse = false;
                return null;
            }

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        // Get scanned carton's data with BinLocationAbsEntry
                        downloadedBinLocationAbsEntry = RetrofitInstance.getBinLocationAbsEntryNumber(SharedPreferencesClass.getCookie(), stackLocation);

                        if(downloadedBinLocationAbsEntry == null)
                            return null;

                        jsonObject = new JSONObject(downloadedBinLocationAbsEntry);
                        jsonArray = jsonObject.getJSONArray("value");

                        if(jsonArray.length() == 0)
                            return null;

                        // Extract scanned carton's BinLocationAbsEntry
                        downloadedBinLocationAbsEntry = jsonArray.getJSONObject(0).get("AbsEntry").toString();

                        Log.i("BinLocation", "Abs:" + downloadedBinLocationAbsEntry);

                        stockTransferObj = generateStockTransfersJson(new Integer(downloadedSystemNumber), new Integer(downloadedBinLocationAbsEntry));
                        stockTransferResponse = RetrofitInstance.stockTransfer(SharedPreferencesClass.getCookie(), stockTransferObj);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("ScanningProcess", "Scanning Error: " + e.toString());
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);

            if(stockTransferResponse){

                barcodes.add(0, barcode);

            }
            else{

                barcodes.add(0, "B_" + barcode);


                Toast.makeText(MoveToFumigation.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

            }

            if(barcodes.size() > 0)
                backgroundImage.setVisibility(View.INVISIBLE);
            else
                backgroundImage.setVisibility(View.VISIBLE);

            recyclerView.getAdapter().notifyDataSetChanged();

            MoveToFumigation.this.registerReceiver(mReceiver, mFilter);

        }
    }

    public StockTransfer generateStockTransfersJson(int systemNumber, int binAbsEntry){

        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();
        String itemCode = SharedPreferencesClass.getSalesOrderItemCode();
        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        // Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();

        // Bin Locations
        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();

        serialNumbers.add(serialNumbersObj);
        stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(itemCode, itemCode, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

}
