package com.example.coftea.Database;

public class UserHelperClass {

    private String name;
    private String mobileNo;
    private String hashedPassword; // Add a field for the hashed password

    public UserHelperClass() {
        // Default constructor required for Firebase
    }

    public UserHelperClass(String name, String mobileNo, String hashedPassword) {
        this.name = name;
        this.mobileNo = mobileNo;
        this.hashedPassword = hashedPassword;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
