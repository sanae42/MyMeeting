package com.example.mymeeting.map.ui.indoor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IndoorMapViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IndoorMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}