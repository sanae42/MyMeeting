package com.example.mymeeting.map.ui.outdoor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OutdoorMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OutdoorMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}