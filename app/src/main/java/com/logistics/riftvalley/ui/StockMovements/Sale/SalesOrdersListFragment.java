package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.model.SalesOrder.DeliveryNote;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderDocumentLinesSerialNumbers;
import com.logistics.riftvalley.data.model.SalesOrder.SalesOrderList;

import java.util.ArrayList;
import java.util.List;

public class SalesOrdersListFragment extends Fragment implements _SalesView{

    View view;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView label;
    Button retry;

    BottomNavigationView bottomNavigationView;

    List<SalesOrderList> salesOrderLists = new ArrayList<>();

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = inflater.inflate(R.layout.recyclerview, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        retry = view.findViewById(R.id.retry);
        label = view.findViewById(R.id.label);

        // hide toolbar
        toolbar.setVisibility(View.GONE);

        // initialize view in Presenter
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
        });

        return view;

    }

    @Override
    public void success(boolean isSuccessful) {

        progressBar.setVisibility(View.INVISIBLE);

        if(!isSuccessful){
            Toast.makeText(getActivity(), "Sorry failed to get list", Toast.LENGTH_SHORT).show();
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
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
    public void dispatchProcessResponse(boolean isSuccessful, String message, SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers) {

    }

    @Override
    public void dispatchGoodsResponse(boolean isSuccessful, String message) {

    }

    @Override
    public void isShippingCaseNumberAdded(boolean isSuccessful, String message, SalesOrderDocumentLinesSerialNumbers salesOrderDocumentLinesSerialNumbers) {

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.sales_order_list_items, parent, false));
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

                    // set the active delivery note docEntry to zero
                    SharedPreferencesClass.writeDocEntryNumber(0);

                    SharedPreferencesClass.writeSalesOrderData(
                            salesOrderLists.get(getAdapterPosition()).getCardCode(),
                            salesOrderLists.get(getAdapterPosition()).getQuantity(),
                            salesOrderLists.get(getAdapterPosition()).getItemCode(),
                            salesOrderLists.get(getAdapterPosition()).getDocEntry(),
                            salesOrderLists.get(getAdapterPosition()).getCardName());

                    Intent intent = new Intent(getActivity(), SaleLandingPage.class);
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

}
