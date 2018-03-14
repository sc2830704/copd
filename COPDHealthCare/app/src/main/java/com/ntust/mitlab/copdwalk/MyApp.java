package com.ntust.mitlab.copdwalk;

import android.app.Application;

import com.ntust.mitlab.copdwalk.Service.BluetoothLeService;

/**
 * Created by mitlab_raymond on 2017/10/4.
 */

public class MyApp extends Application {
    BluetoothLeService mBluetoothService;
    public static boolean isSPO2Disable = false;
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
