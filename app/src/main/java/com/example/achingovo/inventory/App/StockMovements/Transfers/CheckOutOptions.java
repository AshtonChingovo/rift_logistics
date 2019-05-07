package com.example.achingovo.inventory.App.StockMovements.Transfers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.achingovo.inventory.R;

public class CheckOutOptions extends AppCompatActivity {

    CardView moveToDispatch;
    CardView moveOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out_options);

        moveToDispatch = findViewById(R.id.dispatch);
        moveOut = findViewById(R.id.moveOut);

        moveToDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutOptions.this, MoveToDispatch.class);
                startActivity(intent);
            }
        });

        moveOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutOptions.this, WarehouseLocationsList.class);
                startActivity(intent);
            }
        });

    }
}
