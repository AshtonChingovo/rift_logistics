package com.logistics.riftvalley.ui.StockMovements.ProductionReturn;

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
import com.logistics.riftvalley.data.model.Entity.StaticVariables;
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

public class ProductionReturn extends AppCompatActivity implements _ProductionReturnView{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    ProgressBar progressBar;

    String barcode;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    List<String> barcodes = new ArrayList<>();

    // Reference to Presenter
    _ProductionReturnPresenter productionReturnPresenter = new ProductionReturnPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.production_return);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);


        // initialize view in Presenter
        productionReturnPresenter.initializeView(this);

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra(SCAN_BARCODE1) == null)
                    return;

                backgroundImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                barcode = intent.getStringExtra(SCAN_BARCODE1);

                unregisterReceiver(mReceiver);

                // new TransferStock().execute(intent.getStringExtra("SCAN_BARCODE1"));
                productionReturnPresenter.requestStockTransfer(barcode, null, PRODUCTION_RETURN);

            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void success(boolean isSuccessful) {

        progressBar.setVisibility(View.INVISIBLE);

        if(isSuccessful){
            barcodes.add(0, barcode);
        }
        else{
            barcodes.add(0, "B_" + barcode);

            Toast.makeText(ProductionReturn.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();
        }

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();


        ProductionReturn.this.registerReceiver(mReceiver, mFilter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(ProductionReturn.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

    public class TransferStock extends AsyncTask<String, Void, Void> {
        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;

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

                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        stockTransferObj = generateStockTransfersJson(new Integer(downloadedSystemNumber));

                        stockTransferResponse = RetrofitInstance.stockTransfer(SharedPreferencesClass.getCookie(), stockTransferObj);
                        Log.i("ScanningProcess", "stockTransferResponse: " + downloadedSystemNumber);

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

                Toast.makeText(ProductionReturn.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();
            }

            if(barcodes.size() > 0)
                backgroundImage.setVisibility(View.INVISIBLE);
            else
                backgroundImage.setVisibility(View.VISIBLE);

            recyclerView.getAdapter().notifyDataSetChanged();


            ProductionReturn.this.registerReceiver(mReceiver, mFilter);

        }

    }

    public StockTransfer generateStockTransfersJson(int systemNumber){

        // All stock movements first go to the TRANSIT warehouse
        String toWarehouseCode = "TPZ";
        String fromWarehouse = SharedPreferencesClass.getWarehouseCode();

        int quantity = 1;

        //Serial Numbers
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        serialNumbers.add(serialNumbersObj);

        // Bin location
        List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();

        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, StaticVariables.FROMWAREHOUSE, fromWarehouse, serialNumbers, stockTransferLinesBinAllocations);
        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, toWarehouseCode, stockTransferLines);

        return stockTransfer;

    }

}
