package com.example.achingovo.inventory.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ExitDialog extends DialogFragment {

    public ExitSave exitSave;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Action")
                .setTitle("Exit")
                .setMessage("Leave without saving?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exitSave.exitNoSave();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exitSave.exitSave();
                    }
                });
        return dialog.create();
    }

    public interface ExitSave{
        public void exitSave();
        public void exitNoSave();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            exitSave = (ExitSave) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ExitSave");
        }
    }



}
