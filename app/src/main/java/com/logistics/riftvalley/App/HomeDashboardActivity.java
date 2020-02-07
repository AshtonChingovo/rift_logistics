package com.logistics.riftvalley.App;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.logistics.riftvalley.ui.StockMovements.WarehouseLocations_Movements;
import com.logistics.riftvalley.ui.NewInventory.WarehouseName_CodeList;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.DB.AppDatabase;

public class HomeDashboardActivity extends AppCompatActivity {

    CardView newInventory;
    CardView movement;
    ImageView warningImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        newInventory = findViewById(R.id.newInventory);
        movement = findViewById(R.id.inWarehouseMovement);
        //warningImage = findViewById(R.id.warning);

/*        warningImage.setVisibility(View.INVISIBLE);

        warningImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, PicturesNotUploadedList.class);
                startActivity(intent);
            }
        });*/

        // Check for images
        new GetPicturesNotUploaded().execute();

        newInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, WarehouseName_CodeList.class);
                startActivity(intent);
            }
        });

        movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeDashboardActivity.this, WarehouseLocations_Movements.class);
                startActivity(intent);
            }
        });

    }

    public class GetPicturesNotUploaded extends AsyncTask<Void, Void, Void> {

        boolean picturesAvailable;

        @Override
        protected Void doInBackground(Void... voids) {

            if(AppDatabase.getDatabase(HomeDashboardActivity.this).dispatchPicturesDao().getAllDispatchPictures().size() > 0)
                picturesAvailable = true;
            else
                picturesAvailable = false;

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //warningImage.setVisibility(picturesAvailable == true ? View.VISIBLE : View.INVISIBLE);

        }

    }

}
