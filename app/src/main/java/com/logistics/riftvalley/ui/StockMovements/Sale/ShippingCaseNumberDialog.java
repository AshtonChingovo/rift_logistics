package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.logistics.riftvalley.R;

public class ShippingCaseNumberDialog extends DialogFragment {

    String barcodeNumber;
    TextView heading;
    TextView barcode;
    TextView stackLocation;
    ScanDialog mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.scan_dialog, null);

        heading = view.findViewById(R.id.heading);
        barcode = view.findViewById(R.id.warehouseName);
        stackLocation = view.findViewById(R.id.location);

        heading.setText("Shipping Case Number");
        barcodeNumber = getArguments().getString("barcode");

        barcode.setText(barcodeNumber);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!stackLocation.getText().toString().equals(""))
                            mListener.ScanDialogOkClicked(barcodeNumber, stackLocation.getText().toString());
                    }
                });

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
