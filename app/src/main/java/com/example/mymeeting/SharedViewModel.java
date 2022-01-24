package com.example.mymeeting;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> log = new MutableLiveData<Boolean>();

    public void login() {
        log.setValue(true);
    }

    public void logout() {
        log.setValue(false);
    }

    public MutableLiveData<Boolean> getLog() {
        return log;
    }


}
