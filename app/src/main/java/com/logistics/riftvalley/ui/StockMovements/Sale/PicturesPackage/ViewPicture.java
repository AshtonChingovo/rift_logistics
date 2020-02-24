package com.logistics.riftvalley.ui.StockMovements.Sale.PicturesPackage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.DeleteImageDialog;
import com.logistics.riftvalley.ui.StockMovements.Sale.SalesPresenter;
import com.logistics.riftvalley.ui.StockMovements.Sale._SalesPresenter;

import java.io.File;

public class ViewPicture extends AppCompatActivity implements DeleteImageDialog.DeleteImage{

    ConstraintLayout operationsPanel;

    ImageView delete;
    ImageView mainImage;

    boolean operationsPanelClicked = true;

    int index;

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_picture);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        operationsPanel = findViewById(R.id.operationsPanel);
        delete = findViewById(R.id.delete);
        mainImage = findViewById(R.id.img);

        // ----- Standardise index variable -----
        index = getIntent().getIntExtra("index", 0);

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        Glide.with(getBaseContext())
                .load(PicturesView.imagesList.get(index).getUri())
                .apply(options)
                .into(mainImage);

        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                operationsPanelClicked = !operationsPanelClicked;

                if(operationsPanelClicked)
                    operationsPanel.setVisibility(View.VISIBLE);
                else
                    operationsPanel.setVisibility(View.GONE);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });
    }

    //TODO: write code to clear Glide cache & confirm image deletion
    public void delete(){
        DialogFragment dialog = new DeleteImageDialog();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void deleteImage() {

        File image = new File(PicturesView.imagesList.get(index).getUri());

        if(PicturesView.imagesList.get(index).getId() != 0){
            salesPresenter.delete(PicturesView.imagesList.get(index));
        }

        if(image.delete() == true){
            PicturesView.imagesList.remove(index);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));
        }

        finish();

    }

    @Override
    public void cancel() {

    }

}
