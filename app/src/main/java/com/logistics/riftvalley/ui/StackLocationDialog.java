package com.logistics.riftvalley.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import static com.logistics.riftvalley.Utilities.PublicStaticVariables.*;

public class StackLocationDialog extends DialogFragment {

    public static String aisleCode;

    TextView stackLocationCode;
    TextView stackLocation;

    RadioGroup radioGroupOptions;
    RadioButton overflow;

    StackLocation mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.stack_location_dialog, null);

        // stackLocationCode = view.findViewById(R.id.stackLocationCode);
        stackLocation = view.findViewById(R.id.location);
        radioGroupOptions = view.findViewById(R.id.radioGroupOptions);
        overflow = view.findViewById(R.id.overflow);

        switch(SharedPreferencesClass.getWarehouseCode()){
            case "LOGBAY10":
                aisleCode = "Bay 10";
                break;
            case "LOGNAW":
                aisleCode = "New Ardbennie";
                break;
            case "LOGVOS1":
                aisleCode = "Vostermans East";
                break;
            case "LOGVOS2":
                aisleCode = "Vostermans West";
                break;
        }

        // stackLocationCode.setText(aisleCode);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.StackLocationOkClicked(stackLocation.getText().toString());
                    }
                });

        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.overflow){
                    mListener.StackLocationOkClicked(OVERFLOW_AREA);
                    dismiss();
                }
                else if(checkedId == R.id.receivingArea){
                    mListener.StackLocationOkClicked(RECEIVING_AREA);
                    dismiss();
                }

            }
        });



        return dialog.create();

    }

    public interface StackLocation{
        void StackLocationOkClicked(String stackLocation);
        void StackLocationReopenDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (StackLocation) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement StackLocationDialog");
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.StackLocationReopenDialog();
    }
}
