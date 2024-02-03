package com.example.coftea.Cashier.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<String> mName;
    private final MutableLiveData<String> mNumber;

    public SettingsViewModel() {
        mName = new MutableLiveData<>();
        mName.setValue(""); // Initialize name with an empty string

        mNumber = new MutableLiveData<>();
        mNumber.setValue(""); // Initialize number with an empty string
    }

    public LiveData<String> getName() {
        return mName;
    }

    public LiveData<String> getNumber() {
        return mNumber;
    }

    public void setName(String name) {
        mName.setValue(name);
    }

    public void setNumber(String number) {
        mNumber.setValue(number);
    }
}