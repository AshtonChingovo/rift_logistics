package com.example.achingovo.inventory.App.StockMovements.Sale.Pictures;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Repository.DB.AppDatabase;
import com.example.achingovo.inventory.Repository.Dao.DispatchPicturesDao;
import com.example.achingovo.inventory.Repository.Entity.DispatchPictures;
import com.example.achingovo.inventory.Utilities.ExitDialog;
import com.example.achingovo.inventory.Utilities.UploadReports.InternetBroadcastReceiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pictures extends AppCompatActivity implements ExitDialog.ExitSave {

    Uri photoURI;
    File photoFile;

    TextView label;

    FloatingActionButton snap;
    RecyclerView recyclerView;
    ImageView img;
    Toolbar toolbar;

    /** List that stores all pics **/
    public static List<DispatchPictures> imagesList = new ArrayList<>();

    final int CAMERA_PERMISSIONS_REQUEST = 1;

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
                    Toast.makeText(Pictures.this, "Maximum number of pictures allowed reached", Toast.LENGTH_SHORT).show();
                else
                    StartCamera();
            }
        });

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Pic taken successfully & added to List

        Log.i("URI--IMAGE", "" + photoURI);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            addPicToGallery(photoFile);
            Intent intent = new Intent(this, Caption.class);
            intent.putExtra("photoURI", photoFile.getAbsolutePath());
            startActivityForResult(intent, 2);

            Log.i("URI--IMAGE", "" + photoURI);

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
                photoURI = FileProvider.getUriForFile(this, "com.example.achingovo.inventory.android.fileprovider", photoFile);
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

        Log.i("Exists", image.getPath());

        return image;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(imagesList.size() > 0)
            label.setVisibility(View.GONE);
        else
            label.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(imagesList.size() > 0){
            DialogFragment dialog = new ExitDialog();
            dialog.show(getSupportFragmentManager(), "Dialog");
        }
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
                if(imagesList.size() > 0){
                    DialogFragment dialog = new ExitDialog();
                    dialog.show(getSupportFragmentManager(), "Dialog");
                }
                else
                    finish();
                return true;
            case R.id.save:
                new AddToDB().execute(imagesList);
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
                    Intent intent = new Intent(Pictures.this, ViewPicture.class);
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

    public class AddToDB extends AsyncTask<List<DispatchPictures>, Void, Void> {
        long[] val;

        @Override
        protected Void doInBackground(List<DispatchPictures>... reportPictures) {

            DispatchPicturesDao dispatchPicturesDao = AppDatabase.getDatabase(getApplicationContext()).dispatchPicturesDao();

            if(Pictures.imagesList.size() <= 0)
                finish();
            else{

                val = dispatchPicturesDao.insertReportPictures(imagesList);
                Pictures.imagesList.clear();
                Log.i("DispatchPictures", "" + val.length);
                finish();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
            if(val.length > 0){
                InternetBroadcastReceiver.StartReportUploading(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Pictures saved", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Failed to save pictures", Toast.LENGTH_SHORT).show();

        }
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