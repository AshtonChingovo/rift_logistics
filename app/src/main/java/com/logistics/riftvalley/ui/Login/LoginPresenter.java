package com.logistics.riftvalley.ui.Login;

import android.util.Log;

import com.logistics.riftvalley.data.DataManager;
import com.logistics.riftvalley.data._DataManager;
import com.logistics.riftvalley.data.model.Entity.Login;

public class LoginPresenter implements _LoginPresenter{

    // Reference to DataManager
    _DataManager dataManager = new DataManager();

    // Reference to _LoginActivityView
    _LoginActivityView loginActivityView;

    // initialize presenter in the DataManager class
    public LoginPresenter() {
        dataManager.initializeLoginPresenter(this);
    }

    @Override
    public void initializeLoginActivityView(_LoginActivityView loginActivityView) {
        this.loginActivityView = loginActivityView;
    }

    @Override
    public void login(Login login) {
        Log.d("DataManagerLogin", " ** login presenter ** ");
        dataManager.login(login);
    }

    @Override
    public void loginResponse(boolean isLoggedIn) {
        Log.d("DataManagerLogin", " ** loginResponse ** ");
        if(isLoggedIn)
            loginActivityView.loginSuccessful();
        else
            loginActivityView.loginFailed();
    }

}
