package com.logistics.riftvalley.ui.StockMovements.Transfers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.SerialNumbers;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLines;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLinesBinAllocations;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class MoveToDispatch extends AppCompatActivity implements _TransfersView{

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView stackLocation;
    ImageView backgroundImage;
    ProgressBar progressBar;
    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    final String DISPATCH_AREA_CODE = SharedPreferencesClass.getWarehouseCode() + "-" + DISPATCH_AREA;
    String barcode;
    List<String> barcodes = new ArrayList<>();

    // Reference to Presenter
    _TransfersPresenter transfersPresenter = new TransfersPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        stackLocation = findViewById(R.id.location);
        progressBar = findViewById(R.id.progressBar);

        toolbar.setTitle("DISPATCH-AREA");
        stackLocation.setText("DISPATCH-AREA");

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        // initialize view in Presenter
        transfersPresenter.initializeTransfersView(this);

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null)
                    return;

                backgroundImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                unregisterReceiver(mReceiver);

                // variable keeps the serial number for future use
                barcode = intent.getStringExtra("SCAN_BARCODE1");

                // new TransferStock().execute(intent.getStringExtra("SCAN_BARCODE1"));

                // write the aisle code to the SharePreference
                SharedPreferencesClass.writeStackLocation(DISPATCH_AREA_CODE);

                transfersPresenter.requestSystemNumber(intent.getStringExtra(SCAN_BARCODE1), null, MOVE_TO_DISPATCH_TRANSFERS_ACTIVITY);

            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(MoveToDispatch.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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
            barcode = itemView.findViewById(R.id.warehouseName);

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

    @Override
    public void success(boolean isSuccessful) {

        progressBar.setVisibility(View.INVISIBLE);

        if(isSuccessful)
            barcodes.add(0, barcode);
        else{

            barcodes.add(0, "B_" + barcode);

            Toast.makeText(MoveToDispatch.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

        }

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();

        MoveToDispatch.this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void warehouses(List<Warehouses> warehouses) {

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

            // Get scanned carton's system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        downloadedBinLocationAbsEntry = RetrofitInstance.getBinLocationAbsEntryNumber(SharedPreferencesClass.getCookie(), DISPATCH_AREA_CODE);

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
                    Log.i("ScanningProcess", "ScanNewInventory Error: " + e.toString());
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

                Toast.makeText(MoveToDispatch.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

            }

            if(barcodes.size() > 0)
                backgroundImage.setVisibility(View.INVISIBLE);
            else
                backgroundImage.setVisibility(View.VISIBLE);

            recyclerView.getAdapter().notifyDataSetChanged();

            MoveToDispatch.this.registerReceiver(mReceiver, mFilter);

        }
    }

    public StockTransfer generateStockTransfersJson(int systemNumber, int binAbsEntry){

        String warehouseCode = SharedPreferencesClass.getWarehouseCode();
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();

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

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers,
                stockTransferLinesBinAllocations);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

}
