package com.logistics.riftvalley.Utilities.UploadPictures;

import android.app.IntentService;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;
import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Dao.PicturesDao;
import com.logistics.riftvalley.data.model.Entity.PicturesDB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Uploads extends Worker {

    PicturesDao picturesDao;

    AppDatabase database;

    // shows if there were any errors in trying to upload anything thus return Result.retry()
    boolean retry = false;

    List<PicturesDB> pictures;

    public Uploads(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("worker", "*|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|* WORKING *|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|**|*|*");
        database = AppDatabase.getDatabase(getApplicationContext());

        picturesDao = database.picturesDao();
        pictures = picturesDao.getPicturesNotUploaded();

        boolean uploaded;

        if(pictures.size() == 0)
            return Result.success();

        File compressedImg;

        for(PicturesDB picture: pictures){

            try {

                compressedImg = new Compressor(getApplicationContext()).compressToFile(new File(picture.getUri()));

                // MultipartBody.Part is used to send also the actual file name
                RequestBody imageString = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(convertToBase64(compressedImg.getAbsolutePath())));
                RequestBody docEntryNumber = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(SharedPreferencesClass.getDocEntryNumber()));
                RequestBody imageExtension = RequestBody.create(MediaType.parse("multipart/form-data"), compressedImg.getAbsolutePath().substring(compressedImg.getAbsolutePath().length() - 3));

                uploaded = RetrofitInstance.uploadPicturesToSAP(
                        SharedPreferencesClass.getCookie(), imageString, docEntryNumber, imageExtension
                );

                // If picture has been uploaded change the status to uploaded in the DB i.e upload = 2
                if(uploaded){
                    picture.setUploaded(2);
                    picturesDao.updatePicture(picture);
                }
                else{
                    retry = true;
                }

            } catch (IOException e) {
                retry = true;
                e.printStackTrace();
                continue;
                // return false;
            }

        }

        return null;

    }

    public String convertToBase64(String filePath){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;

    }


}
