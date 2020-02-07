package com.logistics.riftvalley.ui.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.logistics.riftvalley.App.HomeDashboardActivity;
import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.Entity.Login;
import com.logistics.riftvalley.Retrofit.RetrofitInstance;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

public class LoginActivity extends AppCompatActivity implements _LoginActivityView {

    final String COMPANY_DB = "NT_TEST";

    Button login;
    EditText username;
    EditText password;
    ProgressBar progressBar;

    // Reference to Presenter
    final _LoginPresenter loginPresenter = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
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

    public class LoginSAP extends AsyncTask<Login, Void, Void> {

        String responseVal;
        boolean isLoggedIn = false;

        @Override
        protected Void doInBackground(Login... values) {

            responseVal = RetrofitInstance.login(values[0]);

            if(responseVal != null){
                if(SharedPreferencesClass.writeCookie(responseVal)){
                    isLoggedIn = true;
                    SharedPreferencesClass.writeCredentials(username.getText().toString(), password.getText().toString(), COMPANY_DB);
                    return null;
                }
            }

            isLoggedIn = false;

            return null;

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isLoggedIn){

            }
            else{

            }
        }

    }

}
