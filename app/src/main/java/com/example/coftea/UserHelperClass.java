package com.example.coftea;

public class UserHelperClass {

    String name, mobileNo, password;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String mobileNo, String password) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
