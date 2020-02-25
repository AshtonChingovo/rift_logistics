package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SalesOrdersView extends AppCompatActivity implements _SalesView{

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView label;
    Button retry;

    BottomNavigationView bottomNavigationView;

    List<SalesOrderList> salesOrderLists = new ArrayList<>();

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_list_bottom_navigation_view);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        // openFragment(new SalesOrdersListFragment());

        toolbar.setTitle("Sales Orders");

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        fragmentManager = getSupportFragmentManager();
        // fragmentTransaction = fragmentManager.beginTransaction();

/*                    // initialize view in Presenter
        salesPresenter.initializeView(this);

        // request salesOrdersList
        salesPresenter.requestSalesOrdersList();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retry.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                label.setVisibility(View.INVISIBLE);

                // request salesOrdersList
                salesPresenter.requestSalesOrdersList();

                // new GetSalesOrdersList().execute();

            }
        });*/

    }

    @Override
    public void success(boolean isSuccessful) {

        progressBar.setVisibility(View.INVISIBLE);

        if(!isSuccessful){
            Toast.makeText(this, "Sorry failed to get list", Toast.LENGTH_SHORT).show();
            retry.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void salesOrdersList(List<SalesOrderList> salesOrderList) {

        progressBar.setVisibility(View.INVISIBLE);

        salesOrderLists = salesOrderList;

        if(salesOrderLists.size() > 0){
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(new RecyclerViewAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(SalesOrdersView.this));
        }
        else{

            progressBar.setVisibility(View.INVISIBLE);
            label.setVisibility(View.VISIBLE);
            label.setText("No Sales Found");
            retry.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void deliveryNotesList(List<DeliveryNote> deliveryNoteList) {

    }

    @Override
    public void dispatchProcessResponse(boolean isSuccessful, String message, String operationSource) {

    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {

    }

    @Override
    public void isShippingCaseNumberAdded(boolean isSuccessful, String message, SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers) {

    }

    public class GetSalesOrdersList extends AsyncTask<Void, Void, Void> {

        String responseVal;

        @Override
        protected Void doInBackground(Void... voids) {

            if(SharedPreferencesClass.context == null)
                SharedPreferencesClass.context = SalesOrdersView.this;

            responseVal = RetrofitInstance.getSalesOrders(SharedPreferencesClass.getCookie(), SharedPreferencesClass.getWarehouseCode().trim().toUpperCase());

            if(responseVal != null){
                JSONObject jsonObject;
                try {

                    jsonObject = new JSONObject(responseVal);
                    JSONArray jsonArray = jsonObject.getJSONArray("value");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject ordersObject = jsonArray.getJSONObject(i).getJSONObject("Orders");
                        JSONObject ordersDocumentLinesObject = jsonArray.getJSONObject(i).getJSONObject("Orders/DocumentLineProperties");

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
                recyclerView.setLayoutManager(new LinearLayoutManager(SalesOrdersView.this));
            }
            else{

                progressBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.VISIBLE);
                label.setText("No Sales Found");
                retry.setVisibility(View.VISIBLE);
            }
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(SalesOrdersView.this).inflate(R.layout.sales_order_list_items, parent, false));
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

                    Intent intent = new Intent(SalesOrdersView.this, SaleLandingPage.class);
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

        // bottomNavigationView.setSelectedItemId(R.id.salesMenuItem);
        openFragment(new SalesOrdersListFragment());

        // progressBar.setVisibility(View.VISIBLE);
        // salesPresenter.requestSalesOrdersList();
        // deliveryNoteList.clear();
        // new GetSalesOrdersList().execute();

    }

    public void openFragment(Fragment fragment) {

        // fragmentTransaction = fragmentManager.beginTransaction();
        // fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.salesMenuItem:
                            openFragment(new SalesOrdersListFragment());
                            return true;
                        case R.id.picturesMenuItem:
                            openFragment(new SalesOrdersPicturesListFragment());
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
