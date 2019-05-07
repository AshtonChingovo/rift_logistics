package com.example.achingovo.inventory.App.StockMovements.Transfers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransfer;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLines;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLinesBinAllocations;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckOut extends AppCompatActivity implements StackLocationDialog.StackLocation {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView movementType;
    ImageView backgroundImage;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    String stockTransferType;
    String barcode;
    List<String> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        movementType = findViewById(R.id.stackLocation);

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
                else{

                    barcode = intent.getStringExtra("SCAN_BARCODE1");
                    new TransferStock().execute(intent.getStringExtra("SCAN_BARCODE1"));

/*                    if(barcodes.size() > 0) {

                        if(barcodes.size() == 0){
                            backgroundImage.setVisibility(View.VISIBLE);
                            movementType.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                        backgroundImage.setVisibility(View.VISIBLE);*/
                }
            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void StackLocationOkClicked(String stackLocation) {

    }

    @Override
    public void StackLocationReopenDialog() {

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(CheckOut.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

        StockTransfer stockTransferObj;

        boolean stockTransferResponse = false;

        @Override
        protected Void doInBackground(String... values) {

            CheckOut.this.unregisterReceiver(mReceiver);

            // Get scanned carton's system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        stockTransferObj = generateStockTransfersJson(new Integer(downloadedSystemNumber));

                        stockTransferResponse = RetrofitInstance.stockTransfer(SharedPreferencesClass.getCookie(), stockTransferObj);
                        Log.i("ScanningProcess", "stockTransferResponse: " + downloadedSystemNumber);

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
            else
                Toast.makeText(CheckOut.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

            CheckOut.this.registerReceiver(mReceiver, mFilter);

        }
    }

    public StockTransfer generateStockTransfersJson(int systemNumber){

        // All stock movements first go to the TRANSIT warehouse
        String toWarehouseCode = "TRANSIT";
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();
        String itemCode = "BO3";
        int quantity = 1;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        serialNumbers.add(serialNumbersObj);

        // Bin location
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();

        StockTransferLines stockTransferLinesObj = new StockTransferLines(itemCode, itemCode, quantity, barcode, toWarehouseCode, fromWarehouse, serialNumbers, stockTransferLinesBinAllocations);
        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, toWarehouseCode, stockTransferLines);

        Log.i("JSON", "Data: " + new Gson().toJson(stockTransfer));

        return stockTransfer;

    }

}
