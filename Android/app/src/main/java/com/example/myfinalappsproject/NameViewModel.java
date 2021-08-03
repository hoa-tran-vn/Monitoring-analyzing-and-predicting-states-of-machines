package com.example.myfinalappsproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class NameViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MyGanttChartObject>> mutableLiveData;

    public MutableLiveData<ArrayList<MyGanttChartObject>> getMutableLiveData() {
        if (null == mutableLiveData){
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }
}
