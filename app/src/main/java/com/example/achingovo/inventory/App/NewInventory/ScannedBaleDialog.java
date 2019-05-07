package com.example.achingovo.inventory.App.NewInventory;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.example.achingovo.inventory.R;

public class ScannedBaleDialog extends DialogFragment {

    String barcodeNumber;
    String warehouseCode;
    TextView barcode;
    TextView stackLocation;
    ScanDialog mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.scan_dialog, null);

        barcode = view.findViewById(R.id.barcode);
        stackLocation = view.findViewById(R.id.stackLocation);

        barcodeNumber = getArguments().getString("barcode");
        warehouseCode = getArguments().getString("warehouseCode");

        barcode.setText(barcodeNumber);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(barcodeNumber != null && !stackLocation.getText().toString().equals(""))
                            mListener.ScanDialogOkClicked(barcodeNumber, stackLocation.getText().toString(), warehouseCode);

                    }
                });

        return dialog.create();

    }

    public interface ScanDialog{
        public void ScanDialogOkClicked(String serialNumber, String stackLocation, String warehouseCode);
        public void ScanDialogCancelClicked();
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
