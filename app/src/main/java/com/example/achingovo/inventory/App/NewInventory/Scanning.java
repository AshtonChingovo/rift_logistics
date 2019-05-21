package com.example.achingovo.inventory.App.NewInventory;

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

import com.example.achingovo.inventory.Repository.B1_Objects.SerialNumbers;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransfer;
import com.example.achingovo.inventory.Repository.B1_Objects.StockTransfer.NewInventory.StockTransferLines;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Scanning extends AppCompatActivity implements ScannedBaleDialog.ScanDialog, StackLocationDialog.StackLocation {

    public static String warehouseCode;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    TextView stackLocation;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    String barcode;
    String warehouseName;
    String systemNumber;
    String binLocationAbsEntry;

    List<String> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        stackLocation = findViewById(R.id.location);

        warehouseName = getIntent().getStringExtra("warehouseName");
        warehouseCode = getIntent().getStringExtra("code");

        setSupportActionBar(toolbar);

        if(warehouseName != null)
            toolbar.setTitle(warehouseName + " - Cartons");

/*        if(activeStackLocation == null){

            DialogFragment dialog = new StackLocationDialog();
            dialog.show(getSupportFragmentManager(), "Dialog");

        }*/

/*        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new StackLocationDialog();
                dialog.show(getSupportFragmentManager(), "Dialog");
            }
        });*/

/*        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);*/

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try{
                    if(intent.getStringExtra("SCAN_BARCODE1") == null)
                        return;

                    barcode = intent.getStringExtra("SCAN_BARCODE1");
                    new AddStockToWarehouse().execute(barcode, warehouseCode);

                }catch (Exception e){
                    Toast.makeText(Scanning.this, "Operation failed please try again", Toast.LENGTH_SHORT).show();
                }
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

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(Scanning.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

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
    public void ScanDialogOkClicked(String serialNumber, String stackLocation, String warehouseCode) {

        this.unregisterReceiver(mReceiver);

        new AddStockToWarehouse().execute(serialNumber, stackLocation, warehouseCode);

    }

    @Override
    public void ScanDialogCancelClicked() {
        Log.i("DialogScan", "Clicked");
        finish();
    }

    public class AddStockToWarehouse extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;

        String downloadedSystemNumber;
        String downloadedBinLocationAbsEntry;

        StockTransfer stockTransferObj;

        boolean stockTransferResponse = false;

        @Override
        protected Void doInBackground(String... values) {

            // Get scanned carton's data with system number
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){
                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){

                        // Get scanned carton's data with BinLocationAbsEntry
                        //downloadedBinLocationAbsEntry = RetrofitInstance.getBinLocationAbsEntryNumber(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getStackLocation());

                        /*if(downloadedBinLocationAbsEntry == null)
                            return null;*/

                        //jsonObject = new JSONObject(downloadedBinLocationAbsEntry);
                        //jsonArray = jsonObject.getJSONArray("value");

                        /*if(jsonArray.length() == 0)
                            return null;*/

                        // Extract scanned carton's BinLocationAbsEntry
                        //downloadedBinLocationAbsEntry = jsonArray.getJSONObject(0).get("AbsEntry").toString();

                        Log.i("BinLocation", "Abs:" + downloadedBinLocationAbsEntry);

                        stockTransferObj = generateStockTransfersJson(new Integer(downloadedSystemNumber));
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

                Toast.makeText(Scanning.this, "Operation failed to complete, try again", Toast.LENGTH_SHORT).show();

            }

            Scanning.this.registerReceiver(mReceiver, mFilter);

            systemNumber = downloadedSystemNumber;
            binLocationAbsEntry = downloadedBinLocationAbsEntry;

        }

    }

    public StockTransfer generateStockTransfersJson(int systemNumber){

        String fromWarehouse = "TPZ";
        String itemCode = "BO3";
        String allowNegativeQuantity = "tNO";
        String binActionType = "batToWarehouse";
        int quantity = 1;
        int serialAndBatchNumbersBaseLine = 0;
        int baseLineNumber = 0;

        // Serial number object
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);

        // Stock transfer lines bin allocations
     /*   StockTransferLinesBinAllocations stockTransferLinesBinAllocationsObj = new StockTransferLinesBinAllocations(binAbsEntry, quantity,
                allowNegativeQuantity, serialAndBatchNumbersBaseLine, binActionType, baseLineNumber);
*/
        List<SerialNumbers> serialNumbers = new ArrayList<>();
        //List<StockTransferLinesBinAllocations> stockTransferLinesBinAllocations = new ArrayList<>();
        List<StockTransferLines> stockTransferLines = new ArrayList<>();

        serialNumbers.add(serialNumbersObj);
        //stockTransferLinesBinAllocations.add(stockTransferLinesBinAllocationsObj);

        StockTransferLines stockTransferLinesObj = new StockTransferLines(itemCode, itemCode, quantity, barcode, warehouseCode, fromWarehouse, serialNumbers, null);

        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }
}
