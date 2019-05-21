package com.example.achingovo.inventory.Utilities.InternetDialog;

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

public class InternetDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.internet_turned_off_dialog, null);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view).setCancelable(false);

        dialog.setCancelable(false);

        return dialog.create();

    }

}
