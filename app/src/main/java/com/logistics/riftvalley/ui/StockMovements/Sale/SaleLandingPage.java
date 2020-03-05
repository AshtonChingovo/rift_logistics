package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.ui.StockMovements.Sale.PicturesPackage.PicturesView;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.DISPATCH_PICTURES;

public class SaleLandingPage extends AppCompatActivity implements _SalesLandingPageView {

    Toolbar toolbar;
    CardView moveToDispatch;
    CardView moveToFumigation;
    CardView takePictures;
    CardView dispatch;

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_landing_page);

        toolbar = findViewById(R.id.toolbar);
        moveToDispatch = findViewById(R.id.moveToDispatch);
        moveToFumigation = findViewById(R.id.moveToFumigation);
        takePictures = findViewById(R.id.takePictures);
        dispatch = findViewById(R.id.dispatch);

        // disable the dispatch card
        dispatch.setVisibility(View.INVISIBLE);

        // initialize view in Presenter
        salesPresenter.initializeView(this);

        // check if dispatch pictures have been taken
        salesPresenter.haveDispatchPicturesBeenTaken(this);

        moveToFumigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SharedPreferencesClass.getSalesOrderQuantity() == 0){
                    Toast.makeText(SaleLandingPage.this, "No Cartons are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SaleLandingPage.this, MoveToFumigation.class);
                startActivity(intent);

            }
        });

        moveToDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SharedPreferencesClass.getSalesOrderQuantity() == 0){
                    Toast.makeText(SaleLandingPage.this, "No Cartons are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SaleLandingPage.this, MoveToDispatch.class);
                startActivity(intent);

            }
        });

        takePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SaleLandingPage.this, PicturesView.class);
                // intent.putExtra(DISPATCH_PICTURES, DISPATCH_PICTURES);
                startActivity(intent);

            }
        });

        dispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(SharedPreferencesClass.getSalesOrderQuantity() == 0){
                    Toast.makeText(SaleLandingPage.this, "No Cartons are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(SaleLandingPage.this, Dispatch.class);
                startActivity(intent);

            }
        });

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }

    @Override
    public void havePicturesBeenTaken(boolean picturesTaken) {

        if(picturesTaken)
            dispatch.setVisibility(View.VISIBLE);
        else
            dispatch.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // check if dispatch pictures have been taken
        salesPresenter.haveDispatchPicturesBeenTaken(this);
    }
}
