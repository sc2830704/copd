package com.ntust.mitlab.copdwalk;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.Model.ExerciseHelper;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.Model.EnvCriteria;
import com.ntust.mitlab.copdwalk.util.MyException;
import com.ntust.mitlab.copdwalk.util.MyShared;
import com.ntust.mitlab.copdwalk.Service.BluetoothLeService;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.StepService.SensorListener;
import com.ntust.mitlab.copdwalk.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.Characterstic_SPO2_UUID;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.UUID_DATA_WRITE;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.BT_CONNECTED_FAIL;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.BT_IS_CONNECTED;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.BT_NOT_ENABLE;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.DEVICE_ADDR;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.NO_DEVICE;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.DEVICE_TYPE;
import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.TYPE;
import static com.ntust.mitlab.copdwalk.util.Util.DF_DateTime;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse, SensorEventListener  {
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
    private static final String ACTION_RESTART_SENSOR = "com.ntust.mitlab.RestartSensor";
    private final String TAG = "MainActivity_Debug";
    private final String TAG_Broadcast = "MainActivity_Broadcast";
    public final String ACTION_SYNC_DATA = "ACTION_SYNC_DATA";
    private static final String ACTION_INSERT_DAILY_DB = "ACTION_INSERT_DAILY_DB";
    private BluetoothLeService mBluetoothLeService;
    private Button btnStartAct, btnGoAct, btnSaveAct;
    private TextView tvSBP, tvDBP, tvSPO2, tvEHR, tvWatchState, tvSPO2State, tvStepWeek, tvUV,
            tvState,tvRunningTime, tvTmp, tvPM25,tvHumd ,tvUpdateTime, tvProgress, tvEnvStatus,tvAfterBP ,tvBeforeBP;
    private ImageView imageState;
    public static final int REQUEST_ENABLE_BT = 1;
    public final int STATE_NONE=0, STATE_PRETEST=1, STATE_WORKING=2, STATE_END=3, STATE_BAD_PRETEST=4,STATE_GOOD_PRETEST=5,
            STATE_BAD_ADVICE=6,STATE_AFTERTEST=7, STATE_BAD_AFTERTEST=8, STATE_GOOD_AFTERTEST=9;
    private final int MILL_SECONDS_MINUTES = 60*1000;
    private int stepGoal, steps_today, steps_week;
    private int currentPage;
    private NavigationView navigationView;
    private MyApp myApp;
    ViewFlipper vf;
    private HttpTask myAsyncTask;
    private Toolbar toolbar;
    private List<BluetoothGattService> mGattServices;
    float sbp,dbp,spo2,hr;
    private Timer timer, envTimer;
    private String deviceWatch, deviceSPO2;
    private float stepSize = 0.35f;
    private ProgressBar progressBar;
    private Date start_time, end_time;
    JSONArray dataJson = new JSONArray();
    private final int AdTextSize = 25;
    AlertDialog notifyAd;
    private ExerciseHelper eh;
    private boolean syncStepSenor = false;
    private Handler taskHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    updateUI(STATE_BAD_PRETEST);
                    endPreTest();
                    break;
                case 2:
                    tvBeforeBP.setText(String.format("舒張壓: %.1f, 收縮壓: %.1f", eh.dbpBefore, eh.sbpBefore));
                    updateUI(STATE_GOOD_PRETEST);
                    endPreTest();
                    break;
                case 3:
                    tvBeforeBP.setText(String.format("舒張壓: %.1f, 收縮壓: %.1f", eh.dbpBefore, eh.sbpBefore));
                    updateUI(STATE_BAD_ADVICE);
                    endPreTest();
                    break;
                case 4:
                    tvRunningTime.setText(clockParser(message.arg1));
                    break;
                case 5:
                    tvAfterBP.setText(String.format("舒張壓: %.1f, 收縮壓: %.1f", eh.dbpAfter, eh.sbpAfter));
                    updateUI(STATE_GOOD_AFTERTEST);
                    break;
                case 6:
                    updateUI(STATE_BAD_AFTERTEST);
                    break;
                case 7:
                    updateUI(STATE_BAD_AFTERTEST);
                    break;
                case 8:
                    if(!notifyAd.isShowing()){
                        notifyAd.setTitle("運動小幫手");
                        notifyAd.setMessage("您運動中的指尖血氧飽和度(SpO2)已低於安全標準(90%)，建議您暫停活動；休息10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動");
                        notifyAd.show();
                    }
                    break;
                case 9:
                    if(!notifyAd.isShowing()){
                        notifyAd.setTitle("運動小幫手");
                        notifyAd.setMessage("請確認您的手環是否正確穿戴");
                        notifyAd.show();
                    }

                    break;

            }
        }
    };
    private class PreTestTask extends TimerTask{
        int testTime;
        Message msg = new Message();
        @Override
        public void run() {
            //統計1分鐘內各項數值、sample rate = 5sec
            testTime = testTime+5;
            if(testTime >0 && testTime < 60){
                //統計spo2、hr、sbp、dbp數值
                eh.sample();
            }else{
                if(eh.isPreTestOk()){ //檢查前測sensor數值是否正常 (0為不正常)
                    eh.calculateAverageIn60s();
                    eh.savePreTestData();
                    if(eh.isOkToWork()){//sensor檢查通過，並且各項數值正常
                        msg.what=2;
                    }else{
                        //前測檢測不通過，體醒使用者不建議運動
                        msg.what=3;
                    }
                }
                else{
                    //ask redo pretest
                    msg.what=1;
                }
                taskHandler.sendMessage(msg);
                this.cancel();
            }
        }
    }
    private class ExerciseTask extends TimerTask{
        int time;
        Message msg;
        @Override
        public void run() {
            msg = new Message();

            time++;
            msg.what=4;
            msg.arg1=time;
            taskHandler.sendMessage(msg);
            JSONObject jobj = new JSONObject();
            if(time%10==0){
                Log.d("eh.hrs_5s",eh.hrs_5s+"");
                //每五秒統計一次hrs和spo2s
                if(!eh.hrs_5s.isEmpty())
                    eh.hrs_60.add(eh.getListAverage(eh.hrs_5s));
                if(!eh.spo2s_5s.isEmpty())
                    eh.spo2s_60.add(eh.getListAverage(eh.spo2s_5s));
                Calendar calendar = Calendar.getInstance();
                try {
                    jobj.put("datetime",calendar.getTimeInMillis());
                    if(eh.spo2s_5s.size()>0)
                        jobj.put("spo2",(int)eh.getListAverage(eh.spo2s_5s));//要放數字，不能放string
                    else
                        jobj.put("spo2",0);
                    if(eh.hrs_5s.size()>0)
                        jobj.put("hr",(int)eh.getListAverage(eh.hrs_5s));//要放數字，不能放string
                    else
                        jobj.put("hr",0);
                    dataJson.put(jobj);
                    eh.hrs_5s.clear();
                    eh.spo2s_5s.clear();
                    Log.d(TAG,"last data into json:"+jobj.toString());
                    Log.d(TAG,"data length: "+dataJson.length());
                } catch (JSONException e) {
                    Log.d(TAG,"JSON error "+e.getMessage());
                    e.printStackTrace();
                }
            }
            // 每60秒檢查spo2s
            // 如果是空的->沒戴好
            // 平均<90->提醒暫停運動
            if(time%60==0){
                Message msg = new Message();
                if(eh.spo2s_60.isEmpty()){
                    msg.what=9;
                    taskHandler.sendMessage(msg);
                }else if(eh.getListAverage(eh.spo2s_60)<90){
                    msg.what=8;
                    taskHandler.sendMessage(msg);
                }
//                if(getListAverage(hrs)<90)
                eh.hrs_60.clear();
                eh.spo2s_60.clear();
            }
        }
    }
    private class AfterTestTask extends TimerTask{
        int testTime;
        Message msg = new Message();
        @Override
        public void run() {
            //統計1分鐘內各項數值、sample rate = 5sec
            testTime = testTime+5;
            if(testTime >0 && testTime < 60){
                eh.sample();
            }else{
                //檢查前測sensor數值是否正常 (0為不正常)
                if(eh.isAfterTestOk()){
                    if(true){
                        eh.calculateAverageIn60s();
                        //sensor檢查通過，並且各項數值正常
                        eh.saveAfterTestData();
                        msg.what = 5;
                    }else{
                        //後測檢測不通過，重新進行
                        msg.what = 6;
                    }
                }
                else{
                    ////後測檢測不通過，重新進行
                    msg.what = 7;
                }
                taskHandler.sendMessage(msg);
                this.cancel();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate","onCreate");
        setContentView(R.layout.activity_main);
        //setup UI
        setUpToolbar();
        initialUI();
        //connect to BLE service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //取的Application層級服務
        myApp =  (MyApp)getApplication();
        //取得權限
        //取得系統定位服務
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            }
        else
            {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
            }
        int permission = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSIONS);
        }
        DBHelper dbHelper = DBHelper.getInstance(this);
        steps_today = dbHelper.getSteps(Util.getTodayStart(),Util.getTodayEnd());
        steps_week = dbHelper.getDailySteps(Util.getLastWeek(),Util.getTodayEnd());
        Log.d("steps_today",steps_today+"");
        setClock();
        //啟用計步service
        Intent intent = new Intent(this, SensorListener.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
        //註冊ble broadcast service
        registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter());
        eh = new ExerciseHelper();

        //檢查是否有支援陀螺儀的功能
        SensorManager sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            AlertDialog ad = new AlertDialog.Builder(this).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            //finish();
                        }
                    }).setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

            setDialogParam(ad);
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }


    }
    private String clockParser(int secs){
        int sec = secs%60;
        int min = secs/60;
        int hour = 0;
        if(min>=60){
            min = min - (secs/3600) * 60;
            hour = secs/3600;
        }
        String time="";
        if(hour!=0)
            time = String.format("%02d", hour);
        return  time + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
    private void getEnv() {
        //檢查envid是否同步到雲端
        String isEnvSync = MyShared.getData(MainActivity.this, "isEnvSync");
        if(isEnvSync!=null && isEnvSync.equals("false")){
            tvUpdateTime.setText("裝置尚未同步成功，請重新啟動APP");
            return;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        String id = MyShared.getData(this,"id");
        if(netInfo!= null && netInfo.isConnected()){
            myAsyncTask = new HttpTask("GET",null, "/env/getbyuser",id);
            myAsyncTask.setCallback(this);
            myAsyncTask.execute();
        }else{
            tvUpdateTime.setText("無法連線到網路");
        }
    }
    public void initialUI(){
        vf = findViewById(R.id.viewFlipper);
        btnStartAct = vf.findViewById(R.id.btnStartAct);
        btnStartAct.setOnClickListener(btnClickListener);
        btnGoAct = vf.findViewById(R.id.btnGoAct);
        btnGoAct.setOnClickListener(btnClickListener);
        btnSaveAct = vf.findViewById(R.id.btnSaveAct);
        btnSaveAct.setOnClickListener(btnClickListener);
        tvDBP = vf.findViewById(R.id.tvDBP);
        tvSBP = vf.findViewById(R.id.tvSBP);
        tvSPO2 = vf.findViewById(R.id.tvSPO2);
        tvEHR = vf.findViewById(R.id.tvEHR);
        tvWatchState = vf.findViewById(R.id.tvWatchState);
        tvSPO2State = vf.findViewById(R.id.tvSPO2State);
        tvStepWeek = vf.findViewById(R.id.tvStepWeek);
        tvState = vf.findViewById(R.id.tvState);
        tvRunningTime = vf.findViewById(R.id.tvRunningTime);
        tvTmp = vf.findViewById(R.id.tvTmp);
        tvPM25 = vf.findViewById(R.id.tvPM25);
        tvUV = vf.findViewById(R.id.tvUV);
        tvHumd = vf.findViewById(R.id.tvHumd);
        tvUpdateTime = vf.findViewById(R.id.tvUpdateTime);
        tvProgress = vf.findViewById(R.id.tvProgress);
        tvEnvStatus = vf.findViewById(R.id.tvStatus);
        tvAfterBP = vf.findViewById(R.id.tvAfterBP);
        tvBeforeBP = vf.findViewById(R.id.tvBeforeBP);
        imageState = vf.findViewById(R.id.imageState);
        tvWatchState.setText("尚未連線");
        tvSPO2State.setText("尚未連線");
        tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.md_red_700))));
        tvWatchState.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.md_red_700))));
        progressBar = vf.findViewById(R.id.progressBar);
        notifyAd = new AlertDialog.Builder(MainActivity.this)
                .create();

        currentPage = 0;
        btnSaveAct.setEnabled(false);
    }
    private void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setTitle("主頁");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        TextView tvHello = navigationView.getHeaderView(0).findViewById(R.id.tvHello);
        tvHello.setText(MyShared.getData(this,"name")+" 您好");
    }
    public Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnStartAct :
                    if(eh.workState==STATE_NONE)
                        startExercise();
                    else if(eh.workState==STATE_PRETEST){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("取消運動?")
                                .setMessage("按下\"是\"取消")
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        timer.cancel();
                                        endPreTest();
                                    }
                                })
                                .show();

                        setDialogParam(ad);
                    }
                    else if(eh.workState==STATE_WORKING){
                        AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("結束運動?")
                                .setMessage("按下\"是\"，結束並進入後測")
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        timer.cancel();
                                        endExercise();
                                    }
                                })
                                .show();

                        setDialogParam(ad);
                    }
                    break;
                case R.id.btnGoAct:
                    currentPage = 1;
                    vf.setDisplayedChild(1);
                    toolbar.setTitle(R.string.activity);
                    navigationView.setCheckedItem(R.id.nav_event);
                    break;
                case R.id.btnSaveAct:
                    saveActivity();
                    break;
            }
        }
    };
    private void saveActivity() {
        DBHelper dbHelper = DBHelper.getInstance(MainActivity.this);
        int step = dbHelper.getSteps(start_time.getTime(), end_time.getTime());
        JSONObject bp = new JSONObject();
        JSONObject before =  new JSONObject();
        JSONObject after =  new JSONObject();
        try {
            after.put("sbp",eh.sbpAfter);
            after.put("dbp",eh.dbpAfter);
            after.put("hr",eh.hrAfeter);
            after.put("spo2",eh.hrAfeter);
            before.put("sbp",eh.sbpBefore);
            before.put("dbp",eh.dbpBefore);
            before.put("hr",eh.hrBefore);
            before.put("spo2",eh.spo2Before);
            bp.put("after",after).put("before",before);
            Log.d("saveActivity","after: "+after);
            Log.d("saveActivity","before: "+before);
            Log.d("saveActivity","bp: "+bp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int h_i_time = dbHelper.getHItime(start_time.getTime(), end_time.getTime());
        int distance = (int) (step*stepSize);
        dbHelper.saveActivity(Util.getToday(), step, bp.toString(), dataJson.toString(),
                h_i_time, distance, start_time.getTime(), end_time.getTime());
        btnSaveAct.setEnabled(false);
        JSONObject jobj = new JSONObject();
        String id = MyShared.getData(this,"id");
        try {
            jobj.put("uid", id);
            jobj.put("step", step);
            jobj.put("bp", bp);
            jobj.put("data", dataJson);
            jobj.put("distance", distance);
            jobj.put("h_i_time", h_i_time);
            jobj.put("start_time", DF_DateTime.format(start_time));
            jobj.put("end_time", DF_DateTime.format(end_time));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpTask task = new HttpTask("POST", jobj, "/activity/add", null);
        Log.d(TAG,"jobj:"+jobj);
        task.setCallback(this);
        task.execute();
        //String result = dbHelper.getActivity(Util.getToday());
        //Log.d(TAG,result);
    }


    private void startExercise() {
        //check device will return if any device disconnected
        if(!checkDevice()) return;
        final AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.dialog_ptretest_hint)
                .setTitle("開始運動")
                .show();
        ad.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreTest();
                ad.dismiss();
            }
        });


        // ## 增加一個alert dialog 提醒到數開始
    }
    private void startPreTest(){
        updateUI(STATE_PRETEST);
        timer = new Timer();
        timer.scheduleAtFixedRate(new PreTestTask(), 5000, 5000);
        eh.sbps_60 = new ArrayList<>();
        eh.dbps_60 = new ArrayList<>();
        eh.spo2s_60 = new ArrayList<>();
        eh.hrs_60 = new ArrayList<>();
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.bp," );
            if(MyApp.isSPO2Disable)
                return;
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void doExercise(){
        if(!checkDevice()) return;
        start_time = Calendar.getInstance().getTime();
        eh.spo2s_60 = new ArrayList<>();
        eh.hrs_60 = new ArrayList<>();
        dataJson = new JSONArray();
        timer = new Timer();
        timer.scheduleAtFixedRate(new ExerciseTask(), 3000, 1000);
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.hr," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.hr," );
            if(MyApp.isSPO2Disable)
                return;
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.stratForeground(); //啟動前台服務以降低APP被回收機率

    }
    private void endExercise(){
        end_time = Calendar.getInstance().getTime();
        timer.cancel();
        updateUI(STATE_END);
        mBluetoothLeService.sendData(deviceWatch,"menu.hr," );
        if(MyApp.isSPO2Disable)
            return;
        mBluetoothLeService.sendSPO2(deviceSPO2,"b");
        mBluetoothLeService.stopForeground(true); //結束foreground
    }
    private void endPreTest(){
        updateUI(STATE_NONE);
        mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
        if(MyApp.isSPO2Disable)
            return;
        mBluetoothLeService.sendSPO2(deviceSPO2,"b");
    }
    private void endAfterTest(){
        //the same with endPreTest
        endPreTest();
    }
    private void startAfterTest(){
        updateUI(STATE_AFTERTEST);
        eh.sbps_60 = new ArrayList<>();
        eh.dbps_60 = new ArrayList<>();
        eh.spo2s_60 = new ArrayList<>();
        eh.hrs_60 = new ArrayList<>();
        timer = new Timer();
        timer.scheduleAtFixedRate(new AfterTestTask(), 1000, 5000);
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.bp," );
            if(MyApp.isSPO2Disable)
                return;
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean checkDevice(){
        if(!mBluetoothLeService.initialize()){
            Toast.makeText(MainActivity.this,"請確認藍芽已開啟",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(MyShared.getData(MainActivity.this, DEVICE_TYPE.WATCH.toString()) == null){
            //Toast.makeText(MainActivity.this, " 請到'我的裝置'中設定", Toast.LENGTH_LONG).show();
            AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("手錶未設定")
                    .setMessage("到'我的裝置'中設定")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,DeviceActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            setDialogParam(ad);
            return false;
        }else if(tvWatchState.getText().equals("已斷線")){
            Toast.makeText(MainActivity.this,"裝置重新連線...",Toast.LENGTH_SHORT).show();
            connectDevice(DEVICE_TYPE.WATCH);
            tvWatchState.setText("連線中...");
            return false;
        }else if(tvWatchState.getText().equals("連線失敗")){
            Toast.makeText(MainActivity.this,"未偵測到裝置",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(MyApp.isSPO2Disable)
            return true;
        if(MyShared.getData(MainActivity.this, DEVICE_TYPE.SPO2.toString())==null){
            AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("血氧未設定")
                    .setMessage("到'我的裝置'中設定")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,DeviceActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            setDialogParam(ad);
            return false;
        }else if(tvSPO2State.getText().equals("已斷線")){
            Toast.makeText(MainActivity.this,"裝置重新連線...",Toast.LENGTH_SHORT).show();
            connectDevice(DEVICE_TYPE.SPO2);
            tvSPO2State.setText("連線中...");
//            tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.md_Green_600))));
            return false;
        }else if(tvSPO2State.getText().equals("連線中...")){
//            Toast.makeText(MainActivity.this,"未偵測到裝置",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void connectDevice(DEVICE_TYPE type){
        try {
            mBluetoothLeService.connectGatt(MyShared.getData(MainActivity.this, type.toString()));
        } catch (MyException e) {
            switch (e.getMessage()){
                case BT_NOT_ENABLE:
                    Toast.makeText(MainActivity.this,"藍芽未開啟",Toast.LENGTH_SHORT).show();
                    break;
                case NO_DEVICE:
                    Toast.makeText(MainActivity.this,"裝置尚未設定",Toast.LENGTH_SHORT).show();
                    break;
                case BT_CONNECTED_FAIL:
                    Toast.makeText(MainActivity.this,"裝置連線失敗，檢查裝置",Toast.LENGTH_SHORT).show();
                    break;
                case BT_IS_CONNECTED:
                    Toast.makeText(MainActivity.this,"重複連線:裝置已連線",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //continueDoDiscovery();
                } else {
//                    Toast.makeText(this,"未取得權限",Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(this)
                            .setMessage("禁止該權限將無法取得BLE裝置，請按\"是\"已重新啟用")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int permission = ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSIONS);
                                    }
                                }
                            })
                            .show();
                }
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyApp.isSPO2Disable",""+MyApp.isSPO2Disable);
        envTimer = new Timer();
        envTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                getEnv();
            }
        }, 0, MILL_SECONDS_MINUTES);
        deviceWatch=MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString());
        deviceSPO2= MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString());
        DBHelper dbHelper = DBHelper.getInstance(this);
        Log.d(TAG,"getAllSteps"+dbHelper.getAll());
        //tvStep.setText(String.valueOf(steps_today));
        readStepGoal();


    }
    private void setDialogParam(AlertDialog ad){
        TextView textView = (TextView) ad.findViewById(android.R.id.message);
        textView.setTextSize(AdTextSize);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
    }
    private void readStepGoal() {
        if(steps_week/7<5000)
            stepGoal = 7000;
        else if(steps_week/7>=5000 && steps_week/7<9000)
            stepGoal = 9000;
        else
            stepGoal = 9000;
        progressBar.setMax(stepGoal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(steps_today,true);
        }else{

            progressBar.setProgress(steps_today);
        }

        tvStepWeek.setText(String.valueOf(steps_week));
        tvProgress.setText(String.format("%d / %d",steps_today,stepGoal));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        stopService(new Intent(this, SensorListener.class));
        unregisterReceiver(broadcastReceiver);
        unbindService(mServiceConnection);

    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
        //unregisterReceiver(broadcastReceiver);
        envTimer.cancel();
    }
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Bluetooth LE Connect status
            Log.d(TAG, "Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            //將BLE Service加入application服務
            myApp.setBluetoothService(mBluetoothLeService);
            //初始化bluetooth設定，並檢查是否開啟
            if (mBluetoothLeService.initialize())
            {
                //對已設定的裝置進行連線
                if(MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString())!=null){
                    connectDevice(DEVICE_TYPE.SPO2);
                    tvSPO2State.setText("連線中...");
                }else{
                    tvSPO2State.setText("尚未設定");
                }
                if(MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString())!=null){
                    connectDevice(DEVICE_TYPE.WATCH);
                    tvWatchState.setText("連線中...");
                }else{
                    tvWatchState.setText("尚未設定");
                }
            }else{
                //呼叫intent開啟BT
                Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enabler, REQUEST_ENABLE_BT);

            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service Disconnected");
            mBluetoothLeService.closeGatt();
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String device = intent.getStringExtra(DEVICE_ADDR);
            String type = intent.getStringExtra(TYPE);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))
            {
                if(type.equals(DEVICE_TYPE.WATCH.toString())){
                    tvWatchState.setText("已連線");
                    tvWatchState.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_Green_600))));
                    Toast.makeText(MainActivity.this,"成功連線到ZoeWatch",Toast.LENGTH_SHORT).show();
                }else{
                    tvSPO2State.setText("已連線");
                    tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_Green_600))));
                    Toast.makeText(MainActivity.this,"成功連線到SPO2",Toast.LENGTH_SHORT).show();
                }
                invalidateOptionsMenu();
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                Log.v(TAG_Broadcast," BroadcastReceiver get action: " + action);
                String dev ="";
                if(type.equals(DEVICE_TYPE.WATCH.toString())){
                    dev = "Zoe手錶";
                    tvWatchState.setText("已斷線");
                    tvWatchState.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_red_700))));
                    Toast.makeText(MainActivity.this,"ZoeWatch斷線",Toast.LENGTH_SHORT).show();
                    updateUI(STATE_NONE);
                    if(timer!=null){
                        updateUI(STATE_NONE);
                        timer.cancel();
                    }
                }else{
                    dev = "血氧裝置";
                    tvSPO2State.setText("已斷線");
                    tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_red_700))));
                    Toast.makeText(MainActivity.this,"血氧裝置斷線",Toast.LENGTH_SHORT).show();
                    updateUI(STATE_NONE);
                    if(timer!=null){
                        updateUI(STATE_NONE);
                        timer.cancel();
                    }
                }
                if(eh.workState==STATE_PRETEST){
                    timer.cancel();
                    endPreTest();
                    tvState.setText(dev+"斷線，請確認連線後重新開始");
                }
            }
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                Log.v(TAG_Broadcast," BroadcastReceiver get action: " + action);
                // Get all GattService when system discovered services
                String address = intent.getStringExtra("address");

                mGattServices = mBluetoothLeService.getSupportedGattServices(address);
                // Read from GattServices
                if(mGattServices!=null){
                    startRead(address);
                    //Toast.makeText(MainActivity.this,"取得通信服務",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"服務錯誤請重新連線",Toast.LENGTH_LONG).show();
                }
            }
            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
            {
                Log.v(TAG_Broadcast," BroadcastReceiver get action: " + action);
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                //檢查state不是在STATE_NONE再處理資料
                if(eh.workState!=STATE_NONE){
                    if(type.equals(DEVICE_TYPE.WATCH.toString())){
                        processWatch(data);
                    }else{
                        processSPO2(data);
                    }
                }
            }
            else if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        reConnectDevice();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        tvSPO2State.setText("藍芽未開啟");
                        tvWatchState.setText("藍芽未開啟");
                        break;
                }
            }
        }

    };
    public void processSPO2(String data){
        tvSPO2.setText(data.trim());
          spo2 = Float.valueOf(data.trim());
        // calculate every 5 second data, this will be clear in task every 5 sec
        if(eh.workState==STATE_PRETEST || eh.workState==STATE_WORKING || eh.workState==STATE_AFTERTEST){
            if(spo2>0)
                eh.spo2s_5s.add(spo2);
        }
    }
    public void processWatch(String arg1){
        Float E_HR,P_HR;
        double PTT,ET,SLP,SYS,DIA;
        switch (arg1.split("=")[0]){
            case "E_HR":
                E_HR = Float.valueOf(arg1.split("=|,")[1]);
                tvEHR.setText(""+E_HR);
                hr = E_HR;
                if(eh.workState==STATE_PRETEST || eh.workState==STATE_WORKING || eh.workState==STATE_AFTERTEST){
                    if(hr>0)
                        eh.hrs_5s.add(hr);
                }
                break;
            case "P_HR":
                P_HR = Float.valueOf(arg1.split("=|,")[1]);
                tvEHR.setText(""+P_HR);
                hr = P_HR;
                if(eh.workState==STATE_PRETEST || eh.workState==STATE_WORKING || eh.workState==STATE_AFTERTEST){
                    if(hr>0)
                        eh.hrs_5s.add(hr);
                }
                break;
            case "DIA":
                DIA = Float.valueOf(arg1.split("=|,")[1]);
                tvDBP.setText(""+DIA);
                dbp = (int) DIA;
                if(eh.workState==STATE_PRETEST || eh.workState==STATE_WORKING || eh.workState==STATE_AFTERTEST){
                    if(dbp>0)
                        eh.dbps_5s.add(dbp);
//                    dbps_5s.add(dbp);
                }
                break;
            case "SYS":
                SYS = Float.valueOf(arg1.split("=|,")[1]);
                tvSBP.setText(""+SYS);
                sbp = (int) SYS;
                if(eh.workState==STATE_PRETEST || eh.workState==STATE_WORKING || eh.workState==STATE_AFTERTEST){
                    if(sbp>0)
                        eh.sbps_5s.add(sbp);
//                    sbps_5s.add(sbp);
                }
                break;
        }
    }
    private void updateUI(int i) {
        AlertDialog ad;
        switch (i){
            case STATE_NONE:
                eh.workState = STATE_NONE;
                btnStartAct.setEnabled(true);
                btnStartAct.setText("開始");
                btnStartAct.setCompoundDrawablesWithIntrinsicBounds(null, getApplicationContext().getDrawable(R.drawable.ic_start), null, null);
                tvSPO2.setText("-");
                tvEHR.setText("-");
                tvDBP.setText("-");
                tvSBP.setText("-");
                tvState.setText("");
                break;
            case STATE_PRETEST:
                eh.workState = STATE_PRETEST;
                tvBeforeBP.setText("-");
                tvAfterBP.setText("-");
                tvRunningTime.setText("");
                tvState.setText("開始進行前測");
                btnStartAct.setText("停止");
                btnStartAct.setCompoundDrawablesWithIntrinsicBounds(null, getApplicationContext().getDrawable(R.drawable.ic_stop), null, null);
                break;
            case STATE_WORKING:
                tvState.setText("運動量測中...");
                eh.workState = STATE_WORKING;
                btnStartAct.setText("停止");
                btnStartAct.setCompoundDrawablesWithIntrinsicBounds(null, getApplicationContext().getDrawable(R.drawable.ic_stop), null, null);
                break;
            case STATE_END:
                btnStartAct.setEnabled(false);
                tvState.setText("開始進行後測...");
                eh.workState = STATE_END;
                ad = new AlertDialog.Builder(MainActivity.this)
                .setTitle("開始進行後測")
                .setMessage("按下\"是\"，以開始進行後測")
                .setCancelable(false)
                .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startAfterTest();
                    }
                })
                .show();
                setDialogParam(ad);
                break;
            case STATE_BAD_PRETEST:
                btnStartAct.setEnabled(true);
                tvState.setText(eh.workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(eh.workMessage)
                        .setMessage("是否重新測量")
                        .setCancelable(false)
                        .setPositiveButton("是",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tvSPO2.setText("-");
                                tvEHR.setText("-");
                                tvDBP.setText("-");
                                tvSBP.setText("-");
                                startExercise();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUI(STATE_NONE);
                            }
                        })
                        .show();

                setDialogParam(ad);

                break;
            case STATE_GOOD_PRETEST:
                tvState.setText("前測OK");
                ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(eh.workMessage)
                        .setMessage("各項數值良好，可以進行運動")
                        .setCancelable(false)
                        .setPositiveButton("繼續運動",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                doExercise();
                                updateUI(STATE_WORKING);
                            }
                        })
                        .setNegativeButton("取消運動",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUI(STATE_NONE);
                            }
                        })
                        .show();
                setDialogParam(ad);
                break;
            case STATE_BAD_ADVICE:
                tvState.setText("運動前測:不適合運動 原因"+eh.workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("運動建議")
                    .setMessage(eh.warnningMessage)
                    .setCancelable(false)
                    .setPositiveButton("繼續運動",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doExercise();
                            updateUI(STATE_WORKING);
                        }
                    })
                    .setNegativeButton("取消運動",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateUI(STATE_NONE);
                        }
                    }).show();
                setDialogParam(ad);
                break;
            case STATE_AFTERTEST:
                tvSPO2.setText("-");
                tvEHR.setText("-");
                tvDBP.setText("-");
                tvSBP.setText("-");
                eh.workState = STATE_AFTERTEST;
                tvState.setText("開始後測");
                break;
            case STATE_BAD_AFTERTEST:
                btnStartAct.setEnabled(true);
                tvState.setText(eh.workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(eh.workMessage)
                    .setMessage("請重新測量")
                    .setCancelable(false)
                    .setPositiveButton("是",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startAfterTest();
                        }
                    })
                    .setNegativeButton("取消運動",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateUI(STATE_NONE);
                        }
                    })
                    .show();

                setDialogParam(ad);
                break;
            case STATE_GOOD_AFTERTEST:
                btnStartAct.setEnabled(true);
                btnStartAct.setText("開始");
                tvState.setText(eh.workMessage);
                endAfterTest();
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(eh.workMessage)
                    .setMessage("記錄資料")
                    .setCancelable(false)
                    .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //紀錄資料
                        ConnectivityManager cm =
                                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = cm.getActiveNetworkInfo();
                        if(netInfo!= null && netInfo.isConnected()){
                            saveActivity();
                        }else{
                            btnSaveAct.setEnabled(true);
                            Toast.makeText(MainActivity.this,"無法存取到網路，請稍後重試",Toast.LENGTH_LONG).show();
                        }

                    }})
                    .show();
                setDialogParam(ad);
                break;
        }
    }
    private void startRead(String address){
        for (BluetoothGattService GattService : mGattServices) {
            List<BluetoothGattCharacteristic> mGattCharacteristics = GattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : mGattCharacteristics) {
                String uuidStr = gattCharacteristic.getUuid().toString();
                if (uuidStr.equals(UUID_DATA_WRITE.toString())) {
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address, BluetoothLeService.DEVICE_TYPE.WATCH);
                        mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                        Log.d("Characterstic_WATCH","UUID_DATA_WRITE:"+uuidStr);
                }else if(uuidStr.equals(Characterstic_SPO2_UUID.toString())){
                    Log.d("Characterstic_SPO2_UUID","UUidStr:"+uuidStr);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address,BluetoothLeService.DEVICE_TYPE.SPO2);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                }
            }
        }
    }

    public void clearData(){
        MyShared.remove(this,DEVICE_TYPE.SPO2.toString());
        MyShared.remove(this,DEVICE_TYPE.WATCH.toString());
        MyShared.remove(this,DEVICE_TYPE.ENV.toString());
        MyShared.remove(this,"stepUnSave");
        MyShared.remove(this,"id");
        MyShared.remove(this,"pwd");
        MyShared.remove(this,"name");
        MyShared.remove(this,"age");
        MyShared.remove(this,"sex");
        MyShared.remove(this,"history");
        MyShared.remove(this,"history_other");
        MyShared.remove(this,"bmi");
        MyShared.remove(this,"drug");
        MyShared.remove(this,"drug_other");
        MyShared.remove(this,"DEVICE_TYPE.ENV.toString()");
        DBHelper dbHelper = DBHelper.getInstance(MainActivity.this);
        dbHelper.clearTable();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(currentPage==1){
            vf.setDisplayedChild(0);
            currentPage=0;
            navigationView.setCheckedItem(R.id.nav_main);
            toolbar.setTitle("主頁");
        }else if(eh.workState!=STATE_NONE)
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("離開")
                    .setMessage("關閉APP將失去所有進度，確定要離開?")
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .show();
        else{
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("確定退出?")
                    .setNegativeButton("退出APP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton("不", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            clearData();
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        // Handle navigation view item clicks here.
        if (id == R.id.nav_main) {
            if(currentPage!=0) {
                currentPage = 0;
                toolbar.setTitle(R.string.mainpage);
                vf.setDisplayedChild(0);
            }
        } else if (id == R.id.nav_event) {
            if(currentPage!=1){
                Log.d(TAG,"current "+currentPage);
                vf.setDisplayedChild(1);
                currentPage = 1;
                toolbar.setTitle(R.string.activity);
            }
        }  else if (id == R.id.nav_profile) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,UserActivity.class);
            startActivity(intent);
            return false;   //讓navigation選擇的保留在原本的項目

        } else if (id == R.id.nav_history) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,HistoryActivity.class);
            startActivity(intent);
            return false;   //讓navigation選擇的保留在原本的項目
        } else if (id == R.id.nav_education) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,HealthEducationActivity.class);
            startActivity(intent);
            return false;   //讓navigation選擇的保留在原本的項目
        }  else if (id == R.id.nav_device) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,DeviceActivity.class);
            startActivityForResult(intent,0);
            return false;   //讓navigation選擇的保留在原本的項目
        }
        return true;

    }
    public void setClock() {
//        String isSet = MyShared.getData(MainActivity.this, "clock");
//        if(isSet!=null &&isSet.equals(String.valueOf(Util.getToday()))){
//            return;
//        }else{
//            MyShared.setData(MainActivity.this, "clock", String.valueOf(Util.getToday()));
//        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //設定每天23:55:00上傳今日步數
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PlayReceiver.class);
        intent.setAction(ACTION_INSERT_DAILY_DB);
        PendingIntent pi = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //RTC_WAKEUP模式 關機狀態下也會強制喚醒
        //參數 setRepeating(喚醒模式, 第一次喚醒時間, 間隔)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        //取消alarm manger方法:alarmMgr.cancel(pendingIntent); ex:request code和class要和要取消的alarm一樣
    }
    @Override
    public void processFinish(int status, String result, String endPoint) {
        switch (endPoint){
            case "/activity/add":
                if(status==200){
                    Toast.makeText(MainActivity.this,"運動結果上傳成功",Toast.LENGTH_SHORT).show();
                    btnSaveAct.setEnabled(false);
                }else{
                    Toast.makeText(MainActivity.this,"錯誤: "+result,Toast.LENGTH_SHORT).show();
                    tvState.setText("網路錯誤請重新上傳");
                    btnSaveAct.setEnabled(true);
                }
                break;
            case "/env/getbyuser":
                if(status==601){
                    tvUpdateTime.setText("尚無資料");
                    break;
                }else if(status == 500){
                    tvUpdateTime.setText("服務異常");
                }

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jobj = (JSONObject) jsonArray.get(jsonArray.length()-1);
                    String temperature = (String) jobj.get("temperature");
                    String pm25 = (String) jobj.get("pm25");
                    String humidity = (String) jobj.get("humidity");
                    String uv = (String)jobj.get("uv");
                    tvPM25.setText(pm25);
                    tvTmp.setText(temperature);
                    tvHumd.setText(humidity);
                    tvUV.setText(uv);
                    tvUpdateTime.setText((String) jobj.get("datetime"));
                    EnvCriteria envCriteria = new EnvCriteria(temperature, pm25, humidity, uv);
                    tvEnvStatus.setText(envCriteria.getEnvCreiterMessage());
                    if(envCriteria.isOkToWork()){
                        imageState.setImageResource(R.drawable.ic_good);
                    }else{
                        imageState.setImageResource(R.drawable.ic_sad);
                    }
                }catch (JSONException e) {
                    tvUpdateTime.setText("網路錯誤");
                    Log.d(TAG,e.getMessage());
                    e.printStackTrace();
                }catch (Exception e) {
                    tvUpdateTime.setText(e.getMessage());
                    Log.d(TAG,e.getMessage());
                    e.printStackTrace();
                }
                break;
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!syncStepSenor){
            DBHelper.getInstance(this).saveCurrentSteps((int)event.values[0]);
            syncStepSenor=true;
        }
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        Calendar now = Calendar.getInstance();
        int currentStep = DBHelper.getInstance(this).getCurrentSteps(); //最後存的Senor的計數值
        int stepsToday = DBHelper.getInstance(this).getSteps(Util.getTodayStart(),now.getTimeInMillis()); //今天的步數
        Log.d("event.values[0] ",""+event.values[0] );
        Log.d("currentStep ",""+currentStep );
        Log.d("stepsToday ",""+stepsToday );
        //目前真正的steps = sensor目前值 - 最後sensor的計數值 + 今天已經紀錄的步數
        steps_today = (int) event.values[0] - currentStep + stepsToday;
        tvProgress.setText(String.format("%d / %d",steps_today,stepGoal));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                Log.d(TAG,"REQUEST_ENABLE_BT");
                if(resultCode == RESULT_OK){
                    //
                }else{
                    tvWatchState.setText("藍芽未開啟");
                    tvSPO2State.setText("藍芽未開啟");
                }
                break;
        }
    }

    private void reConnectDevice() {
        mBluetoothLeService.initialize();
        //等3秒確保藍芽已經開啟了
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //對已設定的裝置進行連線
        if(MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString())!=null){
            connectDevice(DEVICE_TYPE.SPO2);
            tvSPO2State.setText("連線中...");
        }else{
            tvSPO2State.setText("尚未設定");
        }
        if(MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString())!=null){
            connectDevice(DEVICE_TYPE.SPO2);
            tvSPO2State.setText("連線中...");
        }else{
            tvWatchState.setText("尚未設定");
        }
    }
}
