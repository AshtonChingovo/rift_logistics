package com.logistics.riftvalley.ui.NewInventory;

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

import com.logistics.riftvalley.data.model.Entity.SerialNumbers;
import com.logistics.riftvalley.data.model.Entity.StaticVariables;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.data.model.NewInventory.StockTransfer;
import com.logistics.riftvalley.data.model.NewInventory.StockTransferLines;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.ui.StackLocationDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class ScanNewInventory extends AppCompatActivity implements _NewInventoryView, ScannedBaleDialog.ScanDialog, StackLocationDialog.StackLocation {

    public static String warehouseCode;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    TextView stackLocation;
    ProgressBar progressBar;
    IntentFilter mFilter;
    BroadcastReceiver mReceiver;
    String barcode;
    String warehouseName;
    String systemNumber;
    String binLocationAbsEntry;

    List<String> barcodes = new ArrayList<>();

    // reference to Presenter
    _NewInventoryPresenter newInventoryPresenter = new NewInventoryPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning);

        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        stackLocation = findViewById(R.id.location);

        setSupportActionBar(toolbar);

        warehouseName = getIntent().getStringExtra(WAREHOUSE_NAME);
        warehouseCode = getIntent().getStringExtra(WAREHOUSE_CODE);

        // initialize view in Presenter
        newInventoryPresenter.initializeNewInventoryView(this);

        // write warehouse location to Shared preference
        SharedPreferencesClass.writeWarehouseCode(warehouseCode);

        // set the stack location to the warehouse Receiving area
        SharedPreferencesClass.writeStackLocation((SharedPreferencesClass.getWarehouseCode() + "-" + RECEIVING_AREA).trim().toUpperCase());

        if(warehouseName != null)
            toolbar.setTitle(warehouseName + " - Cartons");

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try{

                    if(intent.getStringExtra(SCAN_BARCODE1) == null)
                        return;

                    backgroundImage.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    barcode = intent.getStringExtra(SCAN_BARCODE1);

                    unregisterReceiver(mReceiver);

                    // new AddStockToWarehouse().execute(barcode, warehouseCode);

                    newInventoryPresenter.requestSystemNumber(barcode, warehouseCode, SCAN_NEW_INVENTORY);

                }catch (Exception e){
                    Toast.makeText(ScanNewInventory.this, "Operation failed", Toast.LENGTH_SHORT).show();
                }

            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
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

    @Override
    public void failed() {

        progressBar.setVisibility(View.INVISIBLE);

        barcodes.add(0, "B_" + barcode);

        Toast.makeText(ScanNewInventory.this, "Operation failed to complete", Toast.LENGTH_SHORT).show();

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();

        ScanNewInventory.this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void success() {

        progressBar.setVisibility(View.INVISIBLE);

        barcodes.add(0, barcode);

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        recyclerView.getAdapter().notifyDataSetChanged();

        ScanNewInventory.this.registerReceiver(mReceiver, mFilter);


    }

    // not used
    @Override
    public void warehousesList(List<Warehouses> warehousesList) {

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(ScanNewInventory.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

        newInventoryPresenter.requestSystemNumber(barcode, warehouseCode, SCAN_NEW_INVENTORY);

        // new AddStockToWarehouse().execute(serialNumber, stackLocation, warehouseCode);

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

            // Get scanned carton's data with system number i.e json
            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            if(downloadedSystemNumber != null){

                try {

                    jsonObject = new JSONObject(downloadedSystemNumber);
                    jsonArray = jsonObject.getJSONArray("value");

                    // Extract scanned carton's system number
                    downloadedSystemNumber = jsonArray.getJSONObject(0).get("SystemNumber").toString();

                    if(jsonArray.length() != 0){
                        // create stockTransfer object
                        stockTransferObj = generateStockTransfersJson(new Integer(downloadedSystemNumber));
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

                Toast.makeText(ScanNewInventory.this, "Operation failed to complete", Toast.LENGTH_SHORT).show();

            }

            if(barcodes.size() > 0)
                backgroundImage.setVisibility(View.INVISIBLE);
            else
                backgroundImage.setVisibility(View.VISIBLE);

            recyclerView.getAdapter().notifyDataSetChanged();

            ScanNewInventory.this.registerReceiver(mReceiver, mFilter);

            systemNumber = downloadedSystemNumber;
            binLocationAbsEntry = downloadedBinLocationAbsEntry;

        }

    }

    public StockTransfer generateStockTransfersJson(int systemNumber){

        String fromWarehouse = TPZ_WAREHOUSE;
        int quantity = 1;

        List<SerialNumbers> serialNumbers = new ArrayList<>();
        SerialNumbers serialNumbersObj = new SerialNumbers(barcode, systemNumber, quantity);
        serialNumbers.add(serialNumbersObj);

        List<StockTransferLines> stockTransferLines = new ArrayList<>();
        StockTransferLines stockTransferLinesObj = new StockTransferLines(ITEM_CODE, ITEM_CODE, quantity, barcode, warehouseCode, StaticVariables.FROMWAREHOUSE, serialNumbers, null);
        stockTransferLines.add(stockTransferLinesObj);

        StockTransfer stockTransfer = new StockTransfer(fromWarehouse, warehouseCode, stockTransferLines);

        return stockTransfer;

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(mReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }

}
