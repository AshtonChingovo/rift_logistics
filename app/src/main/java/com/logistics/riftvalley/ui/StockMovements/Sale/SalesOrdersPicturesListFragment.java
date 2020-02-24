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
import com.logistics.riftvalley.ui.StockMovements.Sale.PicturesPackage.PicturesView;

import java.util.ArrayList;
import java.util.List;

public class SalesOrdersPicturesListFragment extends Fragment implements _SalesView{

    View view;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView label;
    Button retry;

    BottomNavigationView bottomNavigationView;

    List<DeliveryNote> deliveryNoteList = new ArrayList<>();

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
        salesPresenter.requestDeliveryNotesList(getContext());

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retry.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                label.setVisibility(View.INVISIBLE);

                // request salesOrdersList
                salesPresenter.requestDeliveryNotesList(getContext());

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

    }

    @Override
    public void deliveryNotesList(List<DeliveryNote> deliveryNoteList) {

        progressBar.setVisibility(View.INVISIBLE);

        this.deliveryNoteList = deliveryNoteList;

        if(this.deliveryNoteList.size() > 0){
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
    public void dispatchProcessResponse(boolean isSuccessful, String message, String operationSource) {

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
            return new RecyclerViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.sales_order_pictures_list_items, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return deliveryNoteList.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView customerName;
        TextView picturesCount;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            picturesCount = itemView.findViewById(R.id.picturesCount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferencesClass.writeDocEntryNumber(
                            deliveryNoteList.get(getAdapterPosition()).getDocEntry());

                    Intent intent = new Intent(getActivity(), PicturesView.class);
                    startActivity(intent);

                    deliveryNoteList.clear();
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
            });
        }

        public void bind(int position) {

            customerName.setText(deliveryNoteList.get(position).getCardName());

            if(deliveryNoteList.get(position).getTotalPictures() > 0)
                picturesCount.setText(deliveryNoteList.get(position).getTotalUploaded() + "/" + deliveryNoteList.get(position).getTotalPictures());
            else
                picturesCount.setText("0/0");

        }
    }

}
