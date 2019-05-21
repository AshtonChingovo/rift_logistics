package com.example.achingovo.inventory.App.StockMovements.Sale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.App.NewInventory.Scanning;
import com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder.DeliveryDocument;
import com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.example.achingovo.inventory.Repository.B1_Objects.SalesOrder.SalesOrdersDocumentLines;
import com.example.achingovo.inventory.Retrofit.RetrofitAPI;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dispatch extends AppCompatActivity implements DispatchDialog.DispatchDialogListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView count;
    ImageView backgroundImage;
    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    List<SalesOrderDocumentLinesSerialNumbers> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
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
                    new CheckSerialNumber().execute(intent.getStringExtra("SCAN_BARCODE1"));
                }
            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

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

            barcode = itemView.findViewById(R.id.barcode);
            barcodeImg = itemView.findViewById(R.id.barcodeImg);
            delete = itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Dispatch.this, Scanning.class);
                    startActivity(intent);
                }
            });

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

                jsonResponse = new JSONObject(RetrofitInstance.getSerialNumber(SharedPreferencesClass.getCookie(), values[0]));

                JSONArray jsonArray = jsonResponse.getJSONArray("value");

                if(jsonArray.length() > 0){

                    Log.i("dispatchCode", "size: " + jsonArray.length());

                    for(int i = 0; i < jsonArray.length(); ++i){
                        for(SalesOrderDocumentLinesSerialNumbers scannedBarcodes: barcodes){
                            if(scannedBarcodes.getInternalSerialNumber().equals(values[0].trim())){
                                isInList = true;
                                return null;
                            }
                        }

                        if(barcodes.size() == SharedPreferencesClass.getSalesOrderQuantity()){
                            max = true;
                            return null;
                        }

                        barcodes.add(0, new SalesOrderDocumentLinesSerialNumbers(jsonArray.getJSONObject(i).getString("MfrSerialNo"),
                                jsonArray.getJSONObject(i).getString("SerialNumber"), jsonArray.getJSONObject(i).getInt("SystemNumber")));

                    }
                }
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

        }
    }

    public class CreateDeliveryNote extends AsyncTask<String, Void, Void> {

        int BaseType = 17;
        int BaseLine = 0;
        JSONObject jsonResponseSalesOrderQuantity;
        boolean deliveryCreated;

        @Override
        protected Void doInBackground(String... values) {

            List<SalesOrdersDocumentLines> documentLines = new ArrayList<>();

            for(int i = 0; i < barcodes.size(); ++i){
                if(barcodes.get(i).getInternalSerialNumber().startsWith("B_"))
                    barcodes.remove(i);
            }

            documentLines.add(new SalesOrdersDocumentLines(SharedPreferencesClass.getSalesOrderItemCode(), SharedPreferencesClass.getWarehouseCode(), barcodes.size(), BaseType, SharedPreferencesClass.getSalesOrderDocEntry(), BaseLine, barcodes));

            if(RetrofitInstance.createDeliveryNote(SharedPreferencesClass.getCookie(), new DeliveryDocument(SharedPreferencesClass.getSalesOrderCardCode(), documentLines))){

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

            if(deliveryCreated){
                Toast.makeText(Dispatch.this, "Goods successfully dispatched", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public void dispatchDialogOkClicked() {
        new CreateDeliveryNote().execute();
    }


}
