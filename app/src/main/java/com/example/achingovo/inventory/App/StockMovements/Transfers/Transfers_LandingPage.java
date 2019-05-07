package com.example.achingovo.inventory.App.StockMovements.Transfers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.achingovo.inventory.R;

public class Transfers_LandingPage extends AppCompatActivity implements InterWarehouseDialog.SelectedOption{

    Toolbar toolbar;
    CardView interWarehouseMovementCard;
    CardView inWarehouseMovementCard;

    DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfers_landing_page);

        interWarehouseMovementCard = findViewById(R.id.interWareHouseMovementCard);
        inWarehouseMovementCard = findViewById(R.id.inWarehouseMovement);

        interWarehouseMovementCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new InterWarehouseDialog();
                dialog.show(getSupportFragmentManager(), "Dialog");
            }
        });

        inWarehouseMovementCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Transfers_LandingPage.this, InWarehouse.class);
                startActivity(intent);

            }
        });

/*        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back); */

    }

    @Override
    public void SelectedOptionClicked(String selectedOption) {

        if(dialog == null)
            return;

        dialog.dismiss();

        Intent intent;
        Bundle args = new Bundle();

        if(selectedOption.equals("Check-In")){
            intent = new Intent(Transfers_LandingPage.this, CheckIn.class);
            args.putString("option", "Check-In");
            intent.putExtras(args);
        }
        else if(selectedOption.equals("Check-Out"))
            intent = new Intent(Transfers_LandingPage.this, CheckOutOptions.class);
        else
            return;

        startActivity(intent);

    }

}
