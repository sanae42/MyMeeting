package com.example.mymeeting.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//        TODO：******************************************
//        TODO：*************这个类目前已不使用*************
//        TODO：******************************************
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> log = new MutableLiveData<Boolean>();
    private final MutableLiveData<Integer> needRefreshData = new MutableLiveData<Integer>();

    public void login() {
        log.setValue(true);
    }

    public void logout() {
        log.setValue(false);
    }

    public MutableLiveData<Boolean> getLog() {
        return log;
    }

    public void refreshData(){
        Integer v = needRefreshData.getValue();
        needRefreshData.setValue(++v);
    }


}
