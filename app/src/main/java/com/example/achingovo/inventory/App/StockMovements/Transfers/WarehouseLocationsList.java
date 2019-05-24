package com.example.achingovo.inventory.App.StockMovements.Transfers;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Repository.Entity.Warehouses;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WarehouseLocationsList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView label;
    ProgressBar progressBar;
    Button retry;
    List<Warehouses> warehouses = new ArrayList<>();

    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        new GetWarehouseLocations().execute();

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry);
        label = findViewById(R.id.label);

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(WarehouseLocationsList.this).inflate(R.layout.warehouse_location_list_items, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return warehouses.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView location;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.barcode);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(WarehouseLocationsList.this, CheckOut.class);
                    args.putString("option", "Check-Out");
                    args.putString("toWarehouse", warehouses.get(getAdapterPosition()).getCode());
                    intent.putExtras(args);
                    startActivity(intent);
                }
            });

        }

        public void bind(int position) {

            location.setText(warehouses.get(position).getName());

        }
    }

    public class GetWarehouseLocations extends AsyncTask<Void, Void, Void> {

        String responseVal;
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();

        @Override
        protected Void doInBackground(Void... voids) {

            responseVal = RetrofitInstance.getWarehouses(SharedPreferencesClass.getCookie());

            if(responseVal != null){
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(responseVal);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject warehouseObject = jsonArray.getJSONObject(i);

                        if(warehouseObject.getString("WarehouseCode").equals(warehouseCode))
                            continue;

                        warehouses.add(new Warehouses(warehouseObject.getString("WarehouseName"), warehouseObject.getString("WarehouseCode")));
                    }

                } catch (JSONException e) {
                    Log.i("Locationss", "Error: " + e.toString());
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(warehouses.size() > 0){
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new RecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(WarehouseLocationsList.this));
            }
            else{
                progressBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.VISIBLE);
                label.setText("No Warehouses Found");

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressBar.setVisibility(View.VISIBLE);
                        label.setVisibility(View.INVISIBLE);

                        new GetWarehouseLocations().execute();

                    }
                });

            }
        }
    }

}
