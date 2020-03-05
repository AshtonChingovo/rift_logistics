package com.logistics.riftvalley.ui.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logistics.riftvalley.ui.HomeDashboardActivity;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity implements _LoginActivityView {

    final String COMPANY_DB = "NT_TEST";

    Button login;
    EditText username;
    EditText password;
    CheckBox showPassword;
    ProgressBar progressBar;

    // ImageView base64String;

    // Reference to Presenter
    final _LoginPresenter loginPresenter = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        showPassword = findViewById(R.id.showPassword);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // initialize view in LoginPresenter
        loginPresenter.initializeLoginActivityView(this);

        // set username and password
        username.setText("dbs_config2");
        password.setText("Mqwert18!");

        // Set global context value
        SharedPreferencesClass.context = getApplicationContext();
        SharedPreferencesClass.setSharePreference();

        // base64String = findViewById(R.id.base64String);
        // convertToBase64(new File("/data/data/com.logistics.riftvalley/cache/images/RVC_1582616728_.jpg"));

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                login.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                // login
                loginPresenter.login(new Login(username.getText().toString(), password.getText().toString(), COMPANY_DB));

                // new LoginSAP().execute(new Login(username.getText().toString(), password.getText().toString(), COMPANY_DB));

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void loginSuccessful() {
        finishAffinity();
        Intent intent = new Intent(LoginActivity.this, HomeDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginFailed() {
        Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
        login.setVisibility(View.VISIBLE);
    }


    public String convertToBase64(File fileName){

        try {

            // "/storage/sdcard0/Pictures/RVC_1268807616_.jpg"

            // give your image file url in mCurrentPhotoPath
            // Bitmap bitmap = BitmapFactory.decodeFile("/data/data/com.logistics.riftvalley/cache/images/RVC_1582627626_.jpg");

/*            InputStream inputStream = new FileInputStream(fileName);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();

            String imageString = Base64.encodeToString(bytes, Base64.DEFAULT);

            // base64String.setText(imageString);*/

/*
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // In case you want to compress your image, here it's at 40%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
*/

/*        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/RVC_1268807616_.jpg");
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);*/

            InputStream ims = getContentResolver().openInputStream(Uri.fromFile(new File("/storage/sdcard0/Pictures/RVC_1268807616_.jpg")));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(ims);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Log.d("ImageString", " image path :: " + fileName.getAbsolutePath());
            Log.d("ImageString", "URI" + Uri.fromFile(new File("/storage/sdcard0/Pictures/RVC_1268807616_.jpg")));
            Log.d("ImageString", imageString.trim());

            imageBytes = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            // base64String.setImageBitmap(decodedImage);

        return imageString;

        }
        catch (Exception e){
            Log.d("ImageString", " image error :: " + e.toString());
        }

        return null;

    }

}
