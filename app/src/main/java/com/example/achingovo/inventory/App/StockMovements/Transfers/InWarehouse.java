package com.example.achingovo.inventory.App.StockMovements.Transfers;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.achingovo.inventory.App.NewInventory.StackLocationDialog;
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

public class InWarehouse extends AppCompatActivity implements StackLocationDialog.StackLocation{

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView stackLocation;
    ImageView backgroundImage;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    String activeStackLocation;
    String stockTransferType;
    String barcode;
    List<String> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        stackLocation = findViewById(R.id.stackLocation);

        stockTransferType = getIntent().getStringExtra("option");

        stackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new StackLocationDialog();
                dialog.show(getSupportFragmentManager(), "Dialog");
            }
        });

        if(activeStackLocation == null){

            DialogFragment dialog = new StackLocationDialog();
            dialog.show(getSupportFragmentManager(), "Dialog");

        }

        toolbar.setTitle("In-Warehouse Transfer");

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

/*        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back); */

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null)
                    return;

                barcode = intent.getStringExtra("SCAN_BARCODE1");
                new TransferStock().execute(intent.getStringExtra("SCAN_BARCODE1"));

            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void StackLocationOkClicked(String stackLocation) {

        if(stackLocation.equals("") || stackLocation.trim().equals("")){

            DialogFragment dialog = new StackLocationDialog();
            dialog.show(getSupportFragmentManager(), "Dialog");
            return;

        }

        SharedPreferencesClass.writeStackLocation((StackLocationDialog.aisleCode + stackLocation).trim().toUpperCase());
        this.stackLocation.setText(StackLocationDialog.aisleCode + stackLocation.trim().toUpperCase());

    }

    @Override
    public void StackLocationReopenDialog() {
        finish();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(InWarehouse.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

        StockTransfer stockTransferObj;

        boolean stockTransferResponse = false;

        @Override
        protected Void doInBackground(String... values) {

            InWarehouse.this.unregisterReceiver(mReceiver);

            // Get scanned carton's system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        // Get scanned carton's data with BinLocationAbsEntry
                        downloadedBinLocationAbsEntry = RetrofitInstance.getBinLocationAbsEntryNumber(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getStackLocation());

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

            if(stockTransferResponse){

                barcodes.add(0, barcode);

                if(barcodes.size() > 0)
                    backgroundImage.setVisibility(View.INVISIBLE);
                else
                    backgroundImage.setVisibility(View.VISIBLE);

                recyclerView.getAdapter().notifyDataSetChanged();

            }
            else{

                barcodes.add(0, "B_" + barcode);

                if(barcodes.size() > 0)
                    backgroundImage.setVisibility(View.INVISIBLE);
                else
                    backgroundImage.setVisibility(View.VISIBLE);

                recyclerView.getAdapter().notifyDataSetChanged();

                Toast.makeText(InWarehouse.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

            }

            InWarehouse.this.registerReceiver(mReceiver, mFilter);

        }
    }

    public StockTransfer generateStockTransfersJson(int systemNumber, int binAbsEntry){

        // All stock movements first go to the TRANSIT warehouse
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();
        String itemCode = "BO3";
        int quantity = 1;
        String allowNegativeQuantity = "tNO";
        int serialAndBatchNumbersBaseLine = 0;
        String binActionType = "batToWarehouse";
        int baseLineNumber = 0;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);

        List<SerialNumbers> serialNumbers = new ArrayList<>();
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
