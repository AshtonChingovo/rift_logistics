package com.logistics.riftvalley.ui.StockMovements.Transfers;

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

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.StaticVariables;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class WarehouseCheckOutLocationsList extends AppCompatActivity implements _TransfersView{

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView label;
    ProgressBar progressBar;
    Button retry;
    List<Warehouses> warehousesCheckOutOptionsList = new ArrayList<>();

    Bundle args = new Bundle();

    final String bay10WarehouseCode = StaticVariables.BAY10CODE;

    // Reference to Presenter
    _TransfersPresenter transfersPresenter = new TransfersPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        // new GetWarehouseLocations().execute();

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry);
        label = findViewById(R.id.label);

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        // initialize view in Presenter
        transfersPresenter.initializeTransfersView(this);
        transfersPresenter.requestWarehousesList(WAREHOUSE_CHECK_OUT_LOCATIONS_LIST);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                label.setVisibility(View.INVISIBLE);

                // new GetWarehouseLocations().execute();

                transfersPresenter.requestWarehousesList(WAREHOUSE_CHECK_OUT_LOCATIONS_LIST);

            }
        });

    }

    @Override
    public void success(boolean isSuccessful) {

    }

    @Override
    public void warehouses(List<Warehouses> warehouses) {

        if(warehouses == null){
            retry.setVisibility(View.VISIBLE);
            return;
        }

        warehousesCheckOutOptionsList = warehouses;

        if(warehouses.size() > 0){
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(new RecyclerViewAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(WarehouseCheckOutLocationsList.this));
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            label.setVisibility(View.VISIBLE);
            label.setText("No Warehouses Found");
        }

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(WarehouseCheckOutLocationsList.this).inflate(R.layout.warehouse_location_list_items, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return warehousesCheckOutOptionsList.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView location;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.warehouseName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(WarehouseCheckOutLocationsList.this, CheckOut.class);
                    args.putString("option", "Check-Out");
                    args.putString("toWarehouse", warehousesCheckOutOptionsList.get(getAdapterPosition()).getCode());
                    intent.putExtras(args);
                    startActivity(intent);

                }
            });

        }

        public void bind(int position) {

            location.setText(warehousesCheckOutOptionsList.get(position).getName());

        }
    }

    public class GetWarehouseLocations extends AsyncTask<Void, Void, Void> {

        String responseVal;
        String warehouseCode = SharedPreferencesClass.getWarehouseCode();

        @Override
        protected Void doInBackground(Void... voids) {

            responseVal = RetrofitInstance.getWarehouses(SharedPreferencesClass.getCookie());

            if(responseVal != null){

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(responseVal);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject warehouseObject = jsonArray.getJSONObject(i);

                        if(warehouseObject.getString("WarehouseCode").equals(warehouseCode) || warehouseObject.getString("WarehouseCode").equals(bay10WarehouseCode))
                            continue;

                        warehousesCheckOutOptionsList.add(new Warehouses(warehouseObject.getString("WarehouseName"), warehouseObject.getString("WarehouseCode")));

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

            if(warehousesCheckOutOptionsList.size() > 0){
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new RecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(WarehouseCheckOutLocationsList.this));
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
