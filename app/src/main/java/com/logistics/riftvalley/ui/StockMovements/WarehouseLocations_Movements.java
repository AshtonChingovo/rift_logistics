package com.logistics.riftvalley.ui.StockMovements;

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
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.Warehouses;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WarehouseLocations_Movements extends AppCompatActivity implements _WarehouseLocations_MovementsView{

    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView label;
    Button retry;

    List<Warehouses> warehouses = new ArrayList<>();

    Bundle args = new Bundle();

    // Reference to Presenter
    _WarehouseLocations_MovementsPresenter warehouseLocations_movementsPresenter = new WarehouseLocations_MovementPresenter();

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

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                label.setVisibility(View.INVISIBLE);
                retry.setVisibility(View.INVISIBLE);

                // new GetWarehouseLocations().execute();

                // request warehouses list
                warehouseLocations_movementsPresenter.requestActiveWarehousesList();

            }
        });

        // initialize view in Presenter
        warehouseLocations_movementsPresenter.initializeWarehouseLocations_MovementsView(this);
        // request warehouses list
        warehouseLocations_movementsPresenter.requestActiveWarehousesList();

    }

    @Override
    public void failed() {
        Toast.makeText(this, "Sorry failed to retrieve list, please try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void warehousesList(List<Warehouses> warehousesList) {

        warehouses = warehousesList;

        if(warehouses.size() > 0){
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(new RecyclerViewAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(WarehouseLocations_Movements.this));
        }
        else{

            progressBar.setVisibility(View.INVISIBLE);
            label.setVisibility(View.VISIBLE);
            label.setText("No Warehouses Found");
            retry.setVisibility(View.VISIBLE);

        }

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(WarehouseLocations_Movements.this).inflate(R.layout.warehouse_location_list_items, parent, false));
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

            location = itemView.findViewById(R.id.warehouseName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferencesClass.writeWarehouseCode(warehouses.get(getAdapterPosition()).getCode());

                    Intent intent = new Intent(WarehouseLocations_Movements.this, Movement_Dashboard.class);
                    args.putString("code", warehouses.get(getAdapterPosition()).getCode());
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
                        warehouses.add(new Warehouses(warehouseObject.getString("WarehouseName"), warehouseObject.getString("WarehouseCode")));
                    }

                } catch (JSONException e) {
                    Log.i("Locations", "Error: " + e.toString());
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
                recyclerView.setLayoutManager(new LinearLayoutManager(WarehouseLocations_Movements.this));
            }
            else{

                progressBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.VISIBLE);
                label.setText("No Warehouses Found");
                retry.setVisibility(View.VISIBLE);

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressBar.setVisibility(View.VISIBLE);
                        label.setVisibility(View.INVISIBLE);
                        retry.setVisibility(View.INVISIBLE);

                        new GetWarehouseLocations().execute();
                    }
                });

            }

        }
    }

}
