package com.logistics.riftvalley.ui.StockMovements;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.logistics.riftvalley.ui.StockMovements.ProductionReturn.ProductionReturn;
import com.logistics.riftvalley.ui.StockMovements.Sale.SalesOrdersView;
import com.logistics.riftvalley.ui.StockMovements.StockDisposals.StockDisposals;
import com.logistics.riftvalley.ui.StockMovements.Transfers.Transfers_LandingPage;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

public class Movement_Dashboard extends AppCompatActivity {

    final String LOGBAY10 = "LOGBAY10";

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

                if(SharedPreferencesClass.getWarehouseCode().equals(LOGBAY10)){
                    Toast.makeText(Movement_Dashboard.this, "Operation not applicable to BAY 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(Movement_Dashboard.this, SalesOrdersView.class);
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
