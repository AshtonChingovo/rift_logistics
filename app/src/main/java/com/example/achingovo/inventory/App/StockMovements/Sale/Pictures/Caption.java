package com.example.achingovo.inventory.App.StockMovements.Sale.Pictures;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Repository.Entity.DispatchPictures;
import com.example.achingovo.inventory.Utilities.DeleteImageDialog;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Caption extends AppCompatActivity implements DeleteImageDialog.DeleteImage{

    ImageView img;
    ImageView delete;
    String imageCaption;
    EditText caption;
    String photoURI;
    long unixDate;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Button save = findViewById(R.id.save);
        caption = findViewById(R.id.caption);
        img = findViewById(R.id.img);
        delete = findViewById(R.id.delete);

        imageCaption = caption.getText().toString();
        photoURI = getIntent().getStringExtra("photoURI");

        RequestOptions options = new RequestOptions();
        options.fitCenter();

        Glide.with(this)
                .load(photoURI)
                .apply(options)
                .into(img);

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if(Pictures.imagesList.size() < 10){
                    date = new SimpleDateFormat("dd MMMM yyyy").format(new Date());

                    // unixDate initially set to 0 and will be added from a report's unixDate
                    unixDate = 0;

                    //GrowerId and visit Id are corrected in the Pictures in Activity
                    //DispatchPictures(@NonNull String growerNumber, @NonNull String uri, @NonNull String date, @NonNull String caption, @NonNull int uploaded) {
                    Pictures.imagesList.add(new DispatchPictures(photoURI, date,
                            SharedPreferencesClass.getSalesOrderCustomerName(), 0));

                    Log.i("ImagePath:", "" + photoURI);

                    Intent intent = new Intent();
                    intent.putExtra("caption", imageCaption);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else
                    Toast.makeText(Caption.this, "Maximum number of pictures allowed reached", Toast.LENGTH_SHORT).show();

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

        File image = new File(photoURI);
        image.delete();

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));

        finish();
    }

    @Override
    public void cancel() {

    }
}
