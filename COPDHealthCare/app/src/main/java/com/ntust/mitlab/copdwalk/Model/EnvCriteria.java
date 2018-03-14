package com.ntust.mitlab.copdwalk.Model;

import android.util.Log;

/**
 * Created by mitlab_raymond on 2017/12/13.
 */

public class EnvCriteria {

    private Float temperature, pm25, humidity, uv;
    private String message;
    private final String TAG = "EnvCriteria";
    private boolean isOkToWork = false;
    public EnvCriteria(String temperature, String pm25, String humidity, String uv) {
        this.temperature = Float.parseFloat(temperature);
        this.pm25 = Float.valueOf((pm25));
        this.humidity = Float.valueOf((humidity));
        this.uv = Float.valueOf((uv));
    }
    public String getEnvCreiterMessage(){
        if(uv>=8){
            isOkToWork = false;
            message = "目前紫外線指數>8，請避免外出運動";
        }

        else if(uv>6 && uv<8){
            isOkToWork = false;
            message = "目前紫外線指數:" + uv + "，請減少外出運動";
        }
        else {
            message = "空氣品質良好，可外出運動";
            isOkToWork = true;
        }
        if(pm25>54){
            isOkToWork = false;
            message = "目前PM2.5>54，請避免外出運動";
        }

        else if(pm25>36 && pm25<53){
            isOkToWork = false;
            message = "目前PM2.5>" + pm25 + "，請減少外出運動";
        }
        else{
            message = "空氣品質良好，可外出運動";
            isOkToWork = true;
        }
        Log.d(TAG,message);
        return message;
    }
    public boolean isOkToWork(){
        return isOkToWork;
    }
}
