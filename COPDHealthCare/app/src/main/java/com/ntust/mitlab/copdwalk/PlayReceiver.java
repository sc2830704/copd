package com.ntust.mitlab.copdwalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.util.MyShared;
import com.ntust.mitlab.copdwalk.StepService.Logger;
import com.ntust.mitlab.copdwalk.StepService.SensorListener;
import com.ntust.mitlab.copdwalk.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ntust.mitlab.copdwalk.util.Util.DF_Date;
import static com.ntust.mitlab.copdwalk.util.Util.DF_DateTime;

/**
 * Created by mitlab_raymond on 2017/11/15.
 */

public class PlayReceiver extends BroadcastReceiver {
    private static final String ACTION_SYNC_DATA = "ACTION_SYNC_DATA";
    private static final String ACTION_INSERT_DAILY_DB = "ACTION_INSERT_DAILY_DB";
    private static final String ACTION_RESTART_SENSOR = "com.ntust.mitlab.RestartSensor";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction()!=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            if (android.support.compat.BuildConfig.DEBUG) Logger.log("booted");
            //restore un-save steps saved in share prefs, and clear it
            String stepUnSave = MyShared.getData(context,"stepUnSave");
            MyShared.setData(context,"stepUnSave","0");
            Logger.log("stepUnSave:"+stepUnSave);
            DBHelper dbHelper = DBHelper.getInstance(context);
            dbHelper.saveCurrentSteps(0);
            dbHelper.addToLastEntry(Integer.valueOf(stepUnSave));
            dbHelper.close();
            Toast.makeText(context,"COPD-復原儲存步數: "+stepUnSave+"步",Toast.LENGTH_LONG).show();
            context.startService(new Intent(context, SensorListener.class));
        }
        else if(intent.getAction()!=null && intent.getAction().equals(ACTION_INSERT_DAILY_DB)){
            saveDaily(context);
            final DBHelper dbHelper = DBHelper.getInstance(context);
            if(haveNetwork(context))
                syncDaily(context);
        }
        else if(intent.getAction()!=null && intent.getAction().equals(ACTION_RESTART_SENSOR)){
            Log.d("PlayReceicer","重啟計步器服務");
            context.startService(new Intent(context, SensorListener.class));
        }
    }
    private void saveDaily(Context context) {
        long Mills_In_Days = 24*60*60*1000;
        DBHelper dbHelper = DBHelper.getInstance(context);
        Long yesterday = Util.getYesterday();
        int steps = dbHelper.getSteps(yesterday, yesterday+Mills_In_Days);
        int h_i_time = dbHelper.getHItime(yesterday.longValue(), yesterday+Mills_In_Days);
        //步數為0就不要新增
        if(steps==0)
            return;
        boolean state = dbHelper.saveDaily(yesterday, steps, h_i_time, (int)(steps*0.35));
        Log.d("PlayReceiver","新增昨天的每日步數:"+(state?"成功":"失敗"));
    }
    private void syncDaily(Context context) {
        //同步所有今日以前未同步的步數到雲端
        final DBHelper dbHelper = DBHelper.getInstance(context);
        String data = dbHelper.getUnsyncDaily();
        try {
            JSONArray unsync = new JSONArray(data);
            for(int i=0;i<unsync.length();i++){
                final JSONObject row = (JSONObject) unsync.get(i);
                final Long date = (Long) row.get("date");
                String id = MyShared.getData(context ,"id");
                row.remove("sync_flag");
                row.put("uid",id);
                row.put("date",DF_Date.format(date));
                row.put("updatetime",DF_DateTime.format(Util.now()));
                HttpTask httpTask = new HttpTask("POST",row,"/daily/add",null);
                httpTask.setCallback(new AsyncResponse() {
                    @Override
                    public void processFinish(int state, String result, String endPoint) {
                        Log.d("SplashActivity","state: "+state);
                        if(state==200){
                            dbHelper.updateDaily(date,1);
                        }
                        else{
                        }
                    }
                });
                httpTask.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean haveNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!= null && netInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

}
