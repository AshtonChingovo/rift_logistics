package com.logistics.riftvalley.ui.StockMovements.Transfers;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.logistics.riftvalley.R;

public class CheckOutLandingPage extends AppCompatActivity {

    Toolbar toolbar;
    CardView moveToDispatch;
    CardView moveOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out_options);

        toolbar = findViewById(R.id.toolbar);
        moveToDispatch = findViewById(R.id.dispatch);
        moveOut = findViewById(R.id.moveOut);

        moveToDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutLandingPage.this, MoveToDispatch.class);
                startActivity(intent);
            }
        });

        moveOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutLandingPage.this, WarehouseCheckOutLocationsList.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }
}
