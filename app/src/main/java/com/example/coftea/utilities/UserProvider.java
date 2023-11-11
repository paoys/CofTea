package com.example.coftea.utilities;

import android.content.Intent;
import android.util.Pair;

import androidx.fragment.app.FragmentActivity;

public class UserProvider {

    private String name;
    private String mobileNo;
    private static UserProvider instance;
    public static synchronized UserProvider getInstance() {
        if (instance == null) {
            instance = new UserProvider();
        }
        return instance;
    }

//    First is Name
//    Second is MobileNo
    public Pair<String, String> getUser(){
        return new Pair<>(this.name, this.mobileNo);
    }

    public void setUser(String name, String mobileNo) {
        this.name = name;
        this.mobileNo = mobileNo;
    }
}
