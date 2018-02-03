package com.example.mitlab_raymond.copdhealthcare;

import android.app.Application;

import com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService;

/**
 * Created by mitlab_raymond on 2017/10/4.
 */

public class MyApp extends Application {
    BluetoothLeService mBluetoothService;
    private String myState;
    public BluetoothLeService getBluetoothService() {
        return mBluetoothService;
    }

    public void setBluetoothService(BluetoothLeService mBluetoothService) {
        this.mBluetoothService = mBluetoothService;
    }
    public String getState(){
        return myState;
    }
    public void setState(String s){
        myState = s;
    }
}
