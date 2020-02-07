package com.logistics.riftvalley.ui.StockMovements.StockDisposals;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.SerialNumbers;
import com.logistics.riftvalley.data.model.StockDisposals.DocumentLines;
import com.logistics.riftvalley.data.model.StockDisposals.StockDisposalsEntity;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockDisposals extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    IntentFilter mFilter;
    ProgressBar progressBar;
    BroadcastReceiver mReceiver;
    long barcode;

    List<String> barcodes = new ArrayList<>();
    List<StockDisposalsEntity> DocumentLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_disposals);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(barcodes.size() > 0)
            backgroundImage.setVisibility(View.INVISIBLE);
        else
            backgroundImage.setVisibility(View.VISIBLE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null)
                    return;

                backgroundImage.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                unregisterReceiver(mReceiver);

                new StockDisposal().execute(intent.getStringExtra("SCAN_BARCODE1"));

            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

/*        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);*/

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(StockDisposals.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

    public class StockDisposal extends AsyncTask<String, Void, Void> {

        JSONObject jsonObject;
        JSONArray jsonArray;
        boolean stockDisposed = false;
        String downloadedSystemNumber;
        String serialNumber;

        List<com.logistics.riftvalley.data.model.Entity.SerialNumbers> SerialNumbers = new ArrayList<>();

        @Override
        protected Void doInBackground(String... values) {

            serialNumber = values[0];

            DocumentLines.clear();

            downloadedSystemNumber = RetrofitInstance.getCartonSystemNumber(SharedPreferencesClass.getCookie(), values[0]);

            try {

                jsonObject = new JSONObject(downloadedSystemNumber);
                jsonArray = jsonObject.getJSONArray("value");

                SerialNumbers.add(new SerialNumbers(values[0], jsonArray.getJSONObject(0).getInt("SystemNumber"), 1));

                DocumentLines.add(new StockDisposalsEntity(
                                jsonArray.getJSONObject(0).getString("ItemCode"),
                                1,
                                SharedPreferencesClass.getWarehouseCode(),
                                SerialNumbers
                        ));

                stockDisposed = RetrofitInstance.stockDisposal(SharedPreferencesClass.getCookie(), new DocumentLines(DocumentLines));

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("JSONException", e.toString());
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.INVISIBLE);

            if(stockDisposed)
               barcodes.add(0, serialNumber);
            else{
                barcodes.add(0, "B_" + serialNumber);
                Toast.makeText(StockDisposals.this, "Operation failed to complete", Toast.LENGTH_SHORT).show();
            }

            if(barcodes.size() > 0)
                backgroundImage.setVisibility(View.INVISIBLE);
            else
                backgroundImage.setVisibility(View.VISIBLE);

            recyclerView.getAdapter().notifyDataSetChanged();

            StockDisposals.this.registerReceiver(mReceiver, mFilter);

        }
    }

}
