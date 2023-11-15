package com.example.coftea.Customer.checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheckoutViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CheckoutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is advance order fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
