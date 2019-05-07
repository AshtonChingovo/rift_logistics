package com.example.achingovo.inventory.App.StockMovements.Sale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.App.NewInventory.Scanning;

import java.util.ArrayList;
import java.util.List;

public class Dispatch extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView backgroundImage;
    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    List<Long> barcodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale);

        toolbar = findViewById(R.id.toolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        barcodes.add(new Long(741311763));
        barcodes.add(new Long(494105808));
        barcodes.add(new Long(047157337));
        barcodes.add(new Long(587896097));
        barcodes.add(new Long(899924481));

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
                    if(barcodes.size() > 0) {

                        barcodes.remove(0);
                        recyclerView.getAdapter().notifyDataSetChanged();

                        if(barcodes.size() == 0)
                            backgroundImage.setVisibility(View.VISIBLE);
                    }
                    else
                        backgroundImage.setVisibility(View.VISIBLE);
                }
            }
        };

        mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(Dispatch.this).inflate(R.layout.scanned_cartons_list_items, parent, false));
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

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            barcode = itemView.findViewById(R.id.barcode);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Dispatch.this, Scanning.class);
                    startActivity(intent);
                }
            });
        }

        public void bind(int position) {

            barcode.setText("" + barcodes.get(position));

        }
    }
}
