package com.logistics.riftvalley.ui.StockMovements.Sale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DispatchPicturesDialog extends DialogFragment {

    DispatchPicturesListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("You have unsaved pictures, do you wish to exit anyway")
                .setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setNegativeButton("EXIT ANYWAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.dispatchDialogExitAnywayClicked();
                    }
                }).setCancelable(false);

        return dialog.create();

    }

    public interface DispatchPicturesListener {
        void dispatchDialogSavePicturesClicked();
        void dispatchDialogExitAnywayClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (DispatchPicturesListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement ScannedBaleDialog");
        }
    }

}
