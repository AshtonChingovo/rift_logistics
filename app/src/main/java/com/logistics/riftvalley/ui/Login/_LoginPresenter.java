package com.logistics.riftvalley.ui.Login;

import com.logistics.riftvalley.data.model.Entity.Login;

public interface _LoginPresenter {

    void initializeLoginActivityView(_LoginActivityView loginActivityView);

    void login(Login login);

    void loginResponse(boolean isLoggedIn);

}
