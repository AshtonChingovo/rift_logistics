package com.example.achingovo.inventory.Utilities.Login;

public class Login {


    public String UserName;
    public String Password;
    public String CompanyDB;

    public Login(String userName, String password, String companyDB) {
        UserName = userName;
        Password = password;
        CompanyDB = companyDB;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCompanyDB() {
        return CompanyDB;
    }

    public void setCompanyDB(String companyDB) {
        CompanyDB = companyDB;
    }

}
