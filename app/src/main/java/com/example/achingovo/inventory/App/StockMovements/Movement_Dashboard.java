package com.example.achingovo.inventory.App.StockMovements;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.achingovo.inventory.App.StockMovements.ProductionReturn.ProductionReturn;
import com.example.achingovo.inventory.App.StockMovements.Sale.SaleLandingPage;
import com.example.achingovo.inventory.App.StockMovements.Sale.SalesOrdersList;
import com.example.achingovo.inventory.App.StockMovements.StockDisposals.StockDisposals;
import com.example.achingovo.inventory.App.StockMovements.Transfers.Transfers_LandingPage;
import com.example.achingovo.inventory.R;

public class Movement_Dashboard extends AppCompatActivity {

    Toolbar toolbar;
    CardView sale;
    CardView interWarehouse;
    CardView productionReturn;
    CardView stockDisposals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movement);

        toolbar = findViewById(R.id.toolbar);
        sale = findViewById(R.id.saleCard);
        interWarehouse = findViewById(R.id.interWareHouseMovementCard);
        productionReturn = findViewById(R.id.productionReturnCard);
        stockDisposals = findViewById(R.id.stockDisposalsCard);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movement_Dashboard.this, SalesOrdersList.class);
                startActivity(intent);
            }
        });

        interWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movement_Dashboard.this, Transfers_LandingPage.class);
                startActivity(intent);
            }
        });

        productionReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movement_Dashboard.this, ProductionReturn.class);
                startActivity(intent);
            }
        });

        stockDisposals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movement_Dashboard.this, StockDisposals.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }
}
