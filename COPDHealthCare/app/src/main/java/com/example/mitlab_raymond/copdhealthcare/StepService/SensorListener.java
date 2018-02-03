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

package com.example.mitlab_raymond.copdhealthcare.StepService;
import android.app.AlarmManager;
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
import com.example.mitlab_raymond.copdhealthcare.BuildConfig;
import com.example.mitlab_raymond.copdhealthcare.util.DBHelper;
import com.example.mitlab_raymond.copdhealthcare.util.MyShared;
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
    //step = 目前sensor的值 (每次sensor event皆會更新)
    //lastSteps = 最後一次timer更新的值 (timer每分鐘更新，lastStep會更新成目前sensor的值)
    //lastMinuteSteps = timer最後一次更新前，一分鐘內的總步數


    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(isSensorChange){
                lastMinuteSteps = steps - lastSteps;
                lastSteps = steps;
                isSensorChange = false;
                updateDB();
            }
        }
    };
    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
        if (BuildConfig.DEBUG) Logger.log(sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Logger.log("probably not a real value: " + event.values[0]);
            Log.d("SensorListener","not a step:"+steps);
            return;
        } else {
            steps = (int) event.values[0];
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
            //只有第一次回執行
            if(flag){
                lastSteps = steps;
                flag = false;
            }
        }
    }
    private void updateDB(){
        if(lastMinuteSteps>0){
            Log.d(TAG,"updateDB "+"lastMinuteSteps:"+lastMinuteSteps+" lastSteps"+lastSteps);
            DBHelper dbHelper = new DBHelper(this);
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
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, Math.min(Util.getTomorrow(),
                        System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR), PendingIntent
                        .getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));


        Timer timer = new Timer();
        if(!isTimerRunning){
            isTimerRunning = true;
            timer.scheduleAtFixedRate(task,200, INTERVAL_MINUTES);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Logger.log("SensorListener onCreate");
        reRegisterSensor();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (BuildConfig.DEBUG) Logger.log("sensor service task removed");
        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorListener.class), 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if (BuildConfig.DEBUG) Logger.log("SensorListener onDestroy");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
    }
    private void reRegisterSensor() {
        if (BuildConfig.DEBUG) Logger.log("re-register sensor listener");
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Logger.log("step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size());
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) {
                Log.d("SensorListener","GG");
                return; // emulator
            }
            Logger.log("default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }
        Log.d("SensorListener","Register");
        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
        Log.d("SensorListener","Register finish");
    }
}
