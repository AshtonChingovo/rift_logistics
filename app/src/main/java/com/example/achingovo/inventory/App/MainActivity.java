package com.example.achingovo.inventory.App;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.example.achingovo.inventory.App.StockMovements.WarehouseLocations_Movements;
import com.example.achingovo.inventory.App.NewInventory.WarehouseLocations;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

public class MainActivity extends AppCompatActivity {

    CardView newInventory;
    CardView movement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        newInventory = findViewById(R.id.newInventory);
        movement = findViewById(R.id.inWarehouseMovement);

        // Set global context value
        SharedPreferencesClass.context = this;
        SharedPreferencesClass.setSharePreference();

        // Login
        new Login().execute();

        newInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WarehouseLocations.class);
                startActivity(intent);
            }
        });

        movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WarehouseLocations_Movements.class);
                startActivity(intent);
            }
        });
    }

    public class Login extends AsyncTask<Void, Void, Void> {

        String responseVal;

        @Override
        protected Void doInBackground(Void... voids) {

            responseVal = RetrofitInstance.login();

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(responseVal != null){
                if(SharedPreferencesClass.writeCookie(responseVal)){

                }
                else
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
