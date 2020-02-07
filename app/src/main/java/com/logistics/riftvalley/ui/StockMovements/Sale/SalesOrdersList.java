package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalesOrdersList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView label;
    Button retry;
    List<SalesOrderList> salesOrderLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        retry = findViewById(R.id.retry);
        label = findViewById(R.id.label);

        setSupportActionBar(toolbar);

        toolbar.setTitle("Sales Orders");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }

    public class GetSalesOrdersList extends AsyncTask<Void, Void, Void> {

        String responseVal;

        @Override
        protected Void doInBackground(Void... voids) {

            if(SharedPreferencesClass.context == null)
                SharedPreferencesClass.context = SalesOrdersList.this;

            responseVal = RetrofitInstance.getSalesOrders(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getWarehouseCode().trim().toUpperCase());

            if(responseVal != null){
                JSONObject jsonObject;
                try {

                    jsonObject = new JSONObject(responseVal);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject ordersObject = jsonArray.getJSONObject(i).getJSONObject("Orders");
                        JSONObject ordersDocumentLinesObject = jsonArray.getJSONObject(i).getJSONObject("Orders/DocumentLines");

                        salesOrderLists.add(new SalesOrderList(ordersObject.getString("CardCode"),
                                ordersObject.getString("CardName"),
                                ordersDocumentLinesObject.getString("ItemCode"),
                                ordersDocumentLinesObject.getInt("Quantity") == ordersDocumentLinesObject.getInt("RemainingOpenQuantity") ? ordersDocumentLinesObject.getInt("Quantity"): ordersDocumentLinesObject.getInt("RemainingOpenQuantity"),
                                ordersObject.getInt("DocEntry")));

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

            if(salesOrderLists.size() > 0){
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new RecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(SalesOrdersList.this));
            }
            else{

                progressBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.VISIBLE);
                label.setText("No Sales Found");
                retry.setVisibility(View.VISIBLE);

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        retry.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        label.setVisibility(View.INVISIBLE);

                        new GetSalesOrdersList().execute();

                    }
                });
            }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(SalesOrdersList.this).inflate(R.layout.sales_order_list_items, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return salesOrderLists.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView customerName;
        TextView quantity;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            quantity = itemView.findViewById(R.id.quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferencesClass.writeSalesOrderData(
                            salesOrderLists.get(getAdapterPosition()).getCardCode(),
                            salesOrderLists.get(getAdapterPosition()).getQuantity(),
                            salesOrderLists.get(getAdapterPosition()).getItemCode(),
                            salesOrderLists.get(getAdapterPosition()).getDocEntry(),
                            salesOrderLists.get(getAdapterPosition()).getCardName());

                    Intent intent = new Intent(SalesOrdersList.this, SaleLandingPage.class);
                    startActivity(intent);

                    salesOrderLists.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
            });

        }

        public void bind(int position) {

            customerName.setText(salesOrderLists.get(position).getCardName());
            quantity.setText(String.valueOf(salesOrderLists.get(position).getQuantity()) + (salesOrderLists.get(position).getQuantity() == 1 ? " Carton" : " Cartons"));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);

        salesOrderLists.clear();

        new GetSalesOrdersList().execute();

    }


}
