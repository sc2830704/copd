package com.example.mitlab_raymond.copdhealthcare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mitlab_raymond.copdhealthcare.Callback.AsyncResponse;
import com.example.mitlab_raymond.copdhealthcare.util.DBHelper;
import com.example.mitlab_raymond.copdhealthcare.util.MyShared;
import com.example.mitlab_raymond.copdhealthcare.Service.HttpTask;
import com.example.mitlab_raymond.copdhealthcare.StepService.Logger;
import com.example.mitlab_raymond.copdhealthcare.StepService.SensorListener;
import com.example.mitlab_raymond.copdhealthcare.StepService.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by mitlab_raymond on 2017/11/15.
 */

public class PlayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        Toast.makeText(context,"onReceive",Toast.LENGTH_SHORT).show();
        if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (android.support.compat.BuildConfig.DEBUG) Logger.log("booted");
            //restore un-save steps saved in share prefs
            String stepUnSave = MyShared.getData(context,"stepUnSave");
            Logger.log("stepUnSave:"+stepUnSave);

            DBHelper dbHelper = DBHelper.getInstance(context);
            dbHelper.saveCurrentSteps(0);
            dbHelper.addToLastEntry(Integer.valueOf(stepUnSave));
            dbHelper.close();

            Toast.makeText(context,"COPD-復原儲存步數: "+stepUnSave+"步",Toast.LENGTH_LONG).show();
            context.startService(new Intent(context, SensorListener.class));
        }else{
            Toast.makeText(context,"資料同步...",Toast.LENGTH_LONG).show();
            DBHelper dbHelper = new DBHelper(context);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            int steps = dbHelper.getSteps(Util.getTodayStart(),Util.getTodayEnd());
            int h_i_time = dbHelper.getHItime(Util.getTodayStart(),Util.getTodayEnd());
            JSONObject jobj = new JSONObject();
            String id = MyShared.getData(context,"id");
            try {
                jobj.put("uid",id);
                jobj.put("step", steps);
                jobj.put("date", df.format(Util.getToday()));
                jobj.put("distance", steps*0.35);
                jobj.put("h_i_time", h_i_time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpTask httpTask = new HttpTask("POST",jobj,"/daily/add",null);
            httpTask.setCallback(new AsyncResponse() {
                @Override
                public void processFinish(int state, String result, String endPoint) {
                    if(state==200)
                        Toast.makeText(context,"同步成功",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"同步失敗",Toast.LENGTH_SHORT).show();
                }
            });
            httpTask.execute();

        }




    }


}
