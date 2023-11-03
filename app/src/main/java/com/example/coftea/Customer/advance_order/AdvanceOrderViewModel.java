package com.example.coftea.Customer.advance_order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdvanceOrderViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdvanceOrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is advance order fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
