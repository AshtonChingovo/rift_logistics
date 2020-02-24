package com.logistics.riftvalley.ui.StockMovements.Sale.PicturesPackage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.Utilities.UploadPictures.Uploads;
import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Dao.PicturesDao;
import com.logistics.riftvalley.Utilities.ExitDialog;
import com.logistics.riftvalley.Utilities.UploadPictures.InternetBroadcastReceiver;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;
import com.logistics.riftvalley.ui.StockMovements.Sale.SalesPresenter;
import com.logistics.riftvalley.ui.StockMovements.Sale._PicturesView;
import com.logistics.riftvalley.ui.StockMovements.Sale._SalesPresenter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PicturesView extends AppCompatActivity implements ExitDialog.ExitSave, _PicturesView {

    Uri photoURI;
    File photoFile;
    TextView label;

    FloatingActionButton snap;
    RecyclerView recyclerView;
    ImageView img;

    Toolbar toolbar;

    long unixDate;
    String date;

    /** List that stores all pics **/
    public static List<PicturesDB> imagesList = new ArrayList<>();

    final int CAMERA_PERMISSIONS_REQUEST = 1;

    // Reference to Presenter
    _SalesPresenter salesPresenter = new SalesPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures);

        checkPermissions();

        toolbar = findViewById(R.id.toolbar);
        snap = findViewById(R.id.snap);
        img = findViewById(R.id.img);
        label = findViewById(R.id.label);
        recyclerView = findViewById(R.id.recyclerView);

        snap.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(imagesList.size() == 10)
                    Toast.makeText(PicturesView.this, "Maximum number of pictures allowed reached", Toast.LENGTH_SHORT).show();
                else
                    StartCamera();
            }
        });

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        // initialize view in Presenter
        salesPresenter.initializeView(this);

        salesPresenter.getPictures(this);

    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Pic taken successfully & added to List
        Log.i("URI--IMAGE", "" + photoURI);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            addPicToGallery(photoFile);

            if(PicturesView.imagesList.size() < 10){
                date = new SimpleDateFormat("dd MMMM yyyy").format(new Date());

                // unixDate initially set to 0 and will be added from a report's unixDate
                unixDate = 0;

                imagesList.add(new PicturesDB(
                        SharedPreferencesClass.getDocEntryNumber(),
                        photoFile.getAbsolutePath(),
                        date,
                        0, 0));

                // refresh recycler view
                recyclerView.getAdapter().notifyDataSetChanged();

                StartCamera();

            }
            else
                Toast.makeText(this, "Maximum number of pictures allowed reached", Toast.LENGTH_SHORT).show();

        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            StartCamera();
        }

    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void StartCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            photoFile = generateImageFile();

            if (photoFile != null) {
                Log.d("StartCamera", "photoFile not Empty");
                photoURI = FileProvider.getUriForFile(this, "com.logistics.riftvalley.inventory.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 1);
            }
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public File generateImageFile() {
        File image = null;
        long unixDate = System.currentTimeMillis() / 1000L;

        String dateTimeStamp = new SimpleDateFormat("dd-M-yyyy HH:mm:ss").format(new Date());
        String imageName = "RVC_" + unixDate + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        image = new File(
                storageDir,
                imageName + ".jpg"
        );

        Log.i("Exists", " size :: " + image.length());

        return image;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();

        if(imagesList.size() > 0)
            label.setVisibility(View.GONE);
        else{
            label.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public void onBackPressed() {
        if(imagesList.size() > 0)
            salesPresenter.updatePictures(this, imagesList);
        else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if(imagesList.size() > 0)
                    salesPresenter.updatePictures(this, imagesList);
                else
                    finish();
                break;
            case R.id.save:
                if(imagesList.size() > 0) {
                    salesPresenter.savePictures(this, imagesList);
                    item.setVisible(false);

                    // InternetBroadcastReceiver.StartReportUploading(this);

                }
                else{
                    Toast.makeText(this, "No pictures found to save", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    public void exitSave() {

    }

    @Override
    public void exitNoSave() {
        finish();
    }

    @Override
    public void picturesList(List<PicturesDB> picturesList) {
        imagesList = picturesList;

        if(imagesList.size() > 0)
            label.setVisibility(View.GONE);
        else
            label.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public void isSavePicturesOperationSuccessful(boolean isSuccessful) {
        if(isSuccessful){
            Toast.makeText(this, "Pictures saved", Toast.LENGTH_SHORT).show();
            // clear list
            imagesList.clear();
            finish();
        }
        else
            Toast.makeText(this, "Sorry operation failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void isUpdatePicturesOperationSuccessful(boolean isSuccessful) {
        if(isSuccessful){
            Toast.makeText(this, "Pictures saved to drafts", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
            Toast.makeText(this, "Sorry operation failed", Toast.LENGTH_SHORT).show();

    }

    /** Recycler View code **/
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.image, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return imagesList.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView caption;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.img);
            caption = itemView.findViewById(R.id.caption);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PicturesView.this, ViewPicture.class);
                    intent.putExtra("index", getAdapterPosition());
                    startActivity(intent);
                }
            });
        }

        public void bind(int position) {
            //this.caption.setText(caption);

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            Glide.with(getBaseContext())
                    .load(imagesList.get(position).getUri())
                    .apply(options)
                    .into(image);

        }
    }

    private void addPicToGallery(File image) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(image.getAbsolutePath());
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    public boolean checkPermissions(){

        String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,  PERMISSIONS, CAMERA_PERMISSIONS_REQUEST);
        }
        else{
            Log.i("CAMERA", "Granted");
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    return;
                } else {
                    // permission denied.
                    //display dialog and close Activity
                    //finish();
                }
                return;
            }

        }
    }

}