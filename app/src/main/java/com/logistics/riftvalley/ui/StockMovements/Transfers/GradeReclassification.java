package com.logistics.riftvalley.ui.StockMovements.Transfers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.logistics.riftvalley.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Map;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class GradeReclassification extends AppCompatActivity implements _GradeReclassificationView, AdapterView.OnItemSelectedListener{

    ConstraintLayout oldBarcodeConstraintLayout;
    ConstraintLayout newBarcodeConstraintLayout;

    TextView oldBarcodeString;
    TextView newBarcodeString;
    TextView grade;

    ImageView oldBarcodeImage;
    ImageView newBarcodeImage;

    RadioButton oldBarcodeRadioButton;
    RadioButton newBarcodeRadioButton;

    ProgressBar oldBarcodeProgressBar;
    ProgressBar newBarcodeProgressBar;
    ProgressBar processingProgressBar;

    Button processButton;

    SearchableSpinner lotNumbers;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    // 1 == oldBarcode scanning section is active
    // 2 == newBarcode scanning section is active
    int activeBarcode;

    String oldBarcode;
    String newBarcode;

    Map<String, String> lotNumbersMap;
    ArrayList lotNumbersList;

    // Reference to Presenter
    _TransfersPresenter transfersPresenter = new TransfersPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_reclassification);

        // initialize view in Presenter
        transfersPresenter.initializeTransfersView(this);

        // get list of lot numbers and grades
        transfersPresenter.getLotNumberList();

        oldBarcodeConstraintLayout = findViewById(R.id.oldBarcodeConstraintLayout);
        newBarcodeConstraintLayout = findViewById(R.id.newBarcodeConstraintLayout);
        oldBarcodeString = findViewById(R.id.oldBarcodeString);
        newBarcodeString = findViewById(R.id.newBarcodeString);
        oldBarcodeImage = findViewById(R.id.oldBracodeImage);
        newBarcodeImage = findViewById(R.id.newBarcodeImage);
        oldBarcodeRadioButton = findViewById(R.id.oldBarcodeRadioButton);
        newBarcodeRadioButton = findViewById(R.id.newBarcodeRadioButton);
        grade = findViewById(R.id.grade);
        lotNumbers = findViewById(R.id.lotNumbers);
        oldBarcodeProgressBar = findViewById(R.id.oldBarcodeProgressBar);
        newBarcodeProgressBar = findViewById(R.id.newBarcodeProgressBar);
        processingProgressBar = findViewById(R.id.processingProgressBar);
        processButton = findViewById(R.id.processButton);

        // hide all progress bars and disable processButton
        oldBarcodeProgressBar.setVisibility(View.INVISIBLE);
        newBarcodeProgressBar.setVisibility(View.INVISIBLE);
        processingProgressBar.setVisibility(View.INVISIBLE);
        processButton.setEnabled(false);

        oldBarcodeConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newBarcodeRadioButton.isChecked())
                    newBarcodeRadioButton.setChecked(false);

                oldBarcodeRadioButton.setChecked(true);

                activeBarcode = 1;

            }
        });

        newBarcodeConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(oldBarcodeRadioButton.isChecked())
                    oldBarcodeRadioButton.setChecked(false);

                newBarcodeRadioButton.setChecked(true);

                activeBarcode = 2;

            }
        });

        oldBarcodeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newBarcodeRadioButton.isChecked())
                    newBarcodeRadioButton.setChecked(false);

                activeBarcode = 1;

            }
        });

        newBarcodeRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(oldBarcodeRadioButton.isChecked())
                    oldBarcodeRadioButton.setChecked(false);

                activeBarcode = 2;

            }
        });

        oldBarcodeString.setVisibility(View.INVISIBLE);
        newBarcodeString.setVisibility(View.INVISIBLE);
        lotNumbers.setVisibility(View.INVISIBLE);
        grade.setVisibility(View.INVISIBLE);

        // PDA scanning broadcast receiver
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra(SCAN_BARCODE1) == null)
                    return;

                Log.d("dataBarcode", "active :: " + activeBarcode + " barcode :: " + intent.getStringExtra(SCAN_BARCODE1));

                if(activeBarcode == 1){

                    oldBarcode = intent.getStringExtra(SCAN_BARCODE1);

                    oldBarcodeImage.setVisibility(View.INVISIBLE);
                    oldBarcodeString.setVisibility(View.VISIBLE);
                    oldBarcodeString.setText(oldBarcode);

                }
                else if(activeBarcode == 2){

                    newBarcode = intent.getStringExtra(SCAN_BARCODE1);

                    newBarcodeImage.setVisibility(View.INVISIBLE);
                    lotNumbers.setVisibility(View.VISIBLE);
                    grade.setVisibility(View.VISIBLE);
                    newBarcodeString.setVisibility(View.VISIBLE);
                    newBarcodeString.setText(newBarcode);

                }

                // unregisterReceiver(mReceiver);

            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        this.registerReceiver(mReceiver, mFilter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        grade.setText(lotNumbersMap.get(lotNumbersList.get(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void lotNumber(Map<String, String> lotNumbersMap) {

        this.lotNumbersMap = lotNumbersMap;
        this.lotNumbersList = new ArrayList<>(lotNumbersMap.keySet());

        ArrayAdapter adapter = new ArrayAdapter(GradeReclassification.this,R.layout.spinner_textview, lotNumbersList);
        lotNumbers.setAdapter(adapter);
        lotNumbers.setOnItemSelectedListener(this);
        lotNumbers.setTitle("Select LotNumber");
        lotNumbers.setPositiveButton("Done");

    }
}
