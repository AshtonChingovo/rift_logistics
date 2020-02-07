package com.logistics.riftvalley.ui.StockMovements.Transfers;

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

public class InterWarehouseDialog extends DialogFragment {

    SelectedOption mListener;
    TextView checkIn;
    TextView checkOut;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.inter_warehouse_dialog, null);

        checkIn = view.findViewById(R.id.checkIn);
        checkOut = view.findViewById(R.id.checkOut);

        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SelectedOptionClicked("Check-In");
            }
        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SelectedOptionClicked("Check-Out");
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return dialog.create();

    }

    public interface SelectedOption{
        void SelectedOptionClicked(String selectedOption);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (SelectedOption) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement InterWarehouseDialog");
        }
    }

}
