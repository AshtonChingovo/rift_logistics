package com.example.achingovo.inventory.Utilities.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.achingovo.inventory.App.MainActivity;
import com.example.achingovo.inventory.R;
import com.example.achingovo.inventory.Repository.Entity.Login;
import com.example.achingovo.inventory.Retrofit.RetrofitInstance;
import com.example.achingovo.inventory.Utilities.SharedPreferences.SharedPreferencesClass;

public class LoginActivity extends AppCompatActivity {

    final String COMPANY_DB = "NT_TEST2";

    Button login;
    EditText username;
    EditText password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // Set global context value
        SharedPreferencesClass.context = this;
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

                new LoginSAP().execute(new Login(username.getText().toString(), password.getText().toString(), COMPANY_DB));

            }
        });

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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isLoggedIn){
                finishAffinity();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{

                Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
            }
        }

    }

}
