package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.logistics.riftvalley.R;

public class ShippingCaseNumberDialog extends DialogFragment {

    String serialNumber;

    TextView heading;
    TextView barcode;
    TextView stackLocation;
    ScanDialog mListener;

    IntentFilter mFilter;
    BroadcastReceiver mReceiver;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.scan_dialog, null);

        heading = view.findViewById(R.id.heading);
        barcode = view.findViewById(R.id.barcode);
        stackLocation = view.findViewById(R.id.location);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.ScanDialogOkClicked(getArguments().getString("barcode"), serialNumber);
                        dismiss();
                    }
                });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getStringExtra("SCAN_BARCODE1") == null){
                    serialNumber = null;
                    return;
                }
                else{

                    serialNumber = intent.getStringExtra("SCAN_BARCODE1");

                    barcode.setText(serialNumber);

                }
            }
        };

        mFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        getActivity().registerReceiver(mReceiver, mFilter);

        return dialog.create();

    }

    public interface ScanDialog{
        void ScanDialogOkClicked(String serialNumber, String shippingCaseNumber);
        void ScanDialogCancelClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (ScanDialog) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement ScannedBaleDialog");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mListener.ScanDialogCancelClicked();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mListener.ScanDialogCancelClicked();
    }

}
