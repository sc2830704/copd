/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntust.mitlab.copdwalk.StepService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import com.ntust.mitlab.copdwalk.BuildConfig;
import com.ntust.mitlab.copdwalk.MainActivity;
import com.ntust.mitlab.copdwalk.PlayReceiver;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.util.MyShared;
import com.ntust.mitlab.copdwalk.util.Util;
import com.ntust.mitlab.copdwalk.R;

/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener extends Service implements SensorEventListener {

    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private final static long INTERVAL_MINUTES = 60*1000;
    private static int steps, lastSteps=0, lastMinuteSteps=-1;
    private final String TAG = "SensorListener";
    private boolean isSensorChange = false;
    private boolean isTimerRunning = false;
    private boolean flag = true;
    private static final String ACTION_RESTART_SENSOR = "com.ntust.mitlab.RestartSensor";
    //steps = 目前sensor的總共步數 (此數值會不斷累加，直到OS重啟)
    //lastSteps = 一分鐘前的總共步數 (timer每分鐘更新，lastStep會更新成目前sensor的值)
    //lastMinuteSteps = 近一分鐘內的步數
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(isSensorChange){ //檢查是否sensor是否有變動
                lastMinuteSteps = steps - lastSteps;    //最後一分鐘內的步數 = 目前sensor的步數 - 前一次更新的步數
                lastSteps = steps;  //更新一分鐘前的步數為現在的步數
                isSensorChange = false;
                updateDB();
            }
        }
    };
    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
//        Toast.makeText(SensorListener.this,"onSensorChanged: "+event.values[0],Toast.LENGTH_SHORT).show();
        if (event.values[0] > Integer.MAX_VALUE) {
            Log.d("SensorListener","not a step:"+steps);
            return;
        } else {
            steps = (int) event.values[0];
            if(flag){
                lastSteps = steps;
                flag = false;
            }
            //用來檢查重開機 lastSteps>event.values[0]，表示重開過
            if(lastSteps>event.values[0]){
                lastSteps=0;
                Log.d("SensorListener", "triger! lastStep: " + lastSteps);
            }

            //save un-save data in share prefs
            int stepUnSave = steps-lastSteps;
            Log.d(TAG,"stepUnSave:"+stepUnSave);
            MyShared.setData(this,"stepUnSave",String.valueOf(stepUnSave));

            isSensorChange = true;
            //只有第一次執行

        }
    }
    private void updateDB(){
        if(lastMinuteSteps>0){
            Log.d(TAG,"updateDB "+"lastMinuteSteps:"+lastMinuteSteps+" lastSteps"+lastSteps);
            DBHelper dbHelper = DBHelper.getInstance(this);
            dbHelper.saveSteps(Calendar.getInstance().getTimeInMillis(),lastMinuteSteps);
            dbHelper.saveCurrentSteps(lastSteps);
            //clear un-save steps in share prefs
            MyShared.setData(SensorListener.this,"stepUnSave","0");
        }
    }
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        // restart service every hour to save the current step count
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(ACTION_RESTART_SENSOR);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 3, serviceIntent ,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), INTERVAL_MINUTES, pendingIntent);

        Timer timer = new Timer();
        if(!isTimerRunning){
            isTimerRunning = true;
            timer.scheduleAtFixedRate(task, 200, INTERVAL_MINUTES);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        reRegisterSensor();
        //設定sensorlistener為前景對象
        Notification.Builder builder = new Notification.Builder(this);//新建Notification.Builder对象
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentTitle("COPD Walk");
        builder.setContentText("正在紀錄您的步數...");
        builder.setSmallIcon(R.drawable.ic_lungs);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

//        startForeground(1,notification);//讓service變成前景對象，顯示在狀態欄中
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Restart service in 500 ms
        Log.d(TAG,"onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        //only called from system kill the service
        //not called if you swipe application to shutdown the application
        super.onDestroy();
        Log.d(TAG,"onDestroy");

        if(lastMinuteSteps>0){
            Log.d(TAG,"updateDB "+"lastMinuteSteps:"+lastMinuteSteps+" lastSteps"+lastSteps);
            DBHelper dbHelper = DBHelper.getInstance(this);
            dbHelper.saveSteps(Calendar.getInstance().getTimeInMillis(),lastMinuteSteps);
            dbHelper.saveCurrentSteps(lastSteps);
            //clear un-save steps in share prefs
            MyShared.setData(SensorListener.this,"stepUnSave","0");
        }

        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESTART_SENSOR);
        sendBroadcast(broadcastIntent);
        Log.d(TAG,"sendBroadcast finish");
    }
    private void reRegisterSensor() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) {
                Log.d("SensorListener","GG");
                return; // emulator
            }
        }
        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
    }
}
