package com.example.achingovo.inventory.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DeleteImageDialog extends DialogFragment {

    DeleteImage deleteImage;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Action")
                .setMessage("Delete Image?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteImage.deleteImage();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteImage.cancel();

                    }
                });

        return dialog.create();

    }

    public interface DeleteImage{
        void deleteImage();
        void cancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            deleteImage = (DeleteImage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement Drafts");
        }
    }

}
