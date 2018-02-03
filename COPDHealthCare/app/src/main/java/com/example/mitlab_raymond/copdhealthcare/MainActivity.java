package com.example.mitlab_raymond.copdhealthcare;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.example.mitlab_raymond.copdhealthcare.Callback.AsyncResponse;
import com.example.mitlab_raymond.copdhealthcare.util.DBHelper;
import com.example.mitlab_raymond.copdhealthcare.Model.EnvCriteria;
import com.example.mitlab_raymond.copdhealthcare.util.MyException;
import com.example.mitlab_raymond.copdhealthcare.util.MyShared;
import com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService;
import com.example.mitlab_raymond.copdhealthcare.Service.HttpTask;
import com.example.mitlab_raymond.copdhealthcare.StepService.SensorListener;
import com.example.mitlab_raymond.copdhealthcare.StepService.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static com.example.mitlab_raymond.copdhealthcare.Model.GattAttributes.Characterstic_SPO2_UUID;
import static com.example.mitlab_raymond.copdhealthcare.Model.GattAttributes.CommandUUID;
import static com.example.mitlab_raymond.copdhealthcare.Model.GattAttributes.SerialPortUUID;
import static com.example.mitlab_raymond.copdhealthcare.Model.GattAttributes.UUID_DATA_WRITE;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.BT_CONNECTED_FAIL;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.BT_IS_CONNECTED;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.BT_NOT_ENABLE;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.DEVICE_ADDR;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.NO_DEVICE;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.DEVICE_TYPE;
import static com.example.mitlab_raymond.copdhealthcare.Service.BluetoothLeService.TYPE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse, SensorEventListener  {
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
    private final String TAG = "MainActivity_Debug";
    private final String TAG_Broadcast = "MainActivity_Broadcast";
    private BluetoothLeService mBluetoothLeService;
    private Button btnStartAct, btnGoAct, btnSaveAct;
    private TextView tvSBP, tvDBP, tvSPO2, tvEHR, tvWatchState, tvSPO2State, tvStepWeek, tvUV,
            tvState,tvRunningTime, tvTmp, tvPM25,tvHumd ,tvUpdateTime, tvProgress, tvEnvStatus, tvText;
    private ImageView imageState;
    public static final int REQUEST_ENABLE_BT = 1;
    public int workState=0;
    private String workMessage;
    public final int STATE_NONE=0, STATE_PRETEST=1, STATE_WORKING=2, STATE_END=3, STATE_BAD_PRETEST=4,STATE_GOOD_PRETEST=5,
            STATE_BAD_ADVICE=6,STATE_AFTERTEST=7, STATE_BAD_AFTERTEST=8, STATE_GOOD_AFTERTEST=9;
    private final int MILLSECONDS_MINUTES = 60*1000;
    private NavigationView navigationView;
    private MyApp myApp;
    ViewFlipper vf;
    private HttpTask myAsyncTask;
    private Toolbar toolbar;
    int steps_today, steps_week;
    private List<BluetoothGattService> mGattServices;
    private ArrayList<Integer> sbps, dbps, spo2s, hrs, sbps_5s, dbps_5s, spo2s_5s, hrs_5s;
    int sbp,dbp,spo2,hr;
    int sbpBefore, dbpBefore, sbpAfter, dbpAfter;
    private Timer timer, envTimer;
    private String deviceWatch, deviceSPO2;
    private float stepSize = 0.35f;
    private ProgressBar progressBar;
    private int stepGoal;
    private int currentPage;
    private Date start_time, end_time;
    int avgSpo2, avgSbp, avgDbp, avgHR;
    JSONArray dataJson = new JSONArray();
    private final int AdTextSize = 25;
    Integer[] title = {R.string.mainpage,R.string.activity,R.string.personalInfo,R.string.history};
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat stf = new SimpleDateFormat("hh:mm:ss");
    private Handler taskHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    updateUI(STATE_BAD_PRETEST);
                    endPreTest();
                    break;
                case 2:
                    updateUI(STATE_GOOD_PRETEST);
                    endPreTest();
                    break;
                case 3:
                    updateUI(STATE_BAD_ADVICE);
                    endPreTest();
                    break;
                case 4:
                    tvRunningTime.setText(clockParser(message.arg1));
                    break;
                case 5:
                    updateUI(STATE_GOOD_AFTERTEST);
                    break;
                case 6:
                    updateUI(STATE_BAD_AFTERTEST);
                    break;
                case 7:
                    updateUI(STATE_BAD_AFTERTEST);
                    break;
                case 8:
                     new AlertDialog.Builder(MainActivity.this)
                            .setTitle("運動小幫手")
                            .setMessage("您的血氧濃度過低(<90)，提醒您暫時停止運動")
                            .show();
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
                if(!hrs_5s.isEmpty())
                    hrs.add(getListAverage(hrs_5s));
                if(!spo2s_5s.isEmpty())
                    spo2s.add(getListAverage(spo2s_5s));
                if(!dbps_5s.isEmpty())
                    dbps.add(getListAverage(dbps_5s));
                if(!sbps_5s.isEmpty())
                    sbps.add(getListAverage(sbps_5s));
                hrs_5s.clear();
                spo2s_5s.clear();
                dbps_5s.clear();
                sbps_5s.clear();
            }else{
                //檢查前測sensor數值是否正常 (0為不正常)
                if(isPreTestOk()){
                    if(isOkToWork()){
                        //sensor檢查通過，並且各項數值正常
                        sbpBefore = avgSbp;
                        dbpBefore = avgDbp;
                        msg.what = 2;
                    }else{
                        //前測檢測不通過，體醒使用者不建議運動
                        msg.what = 3;
                    }
                }
                else{
                    //ask redo pretest
                    msg.what = 1;
                }
                taskHandler.sendMessage(msg);
                this.cancel();
            }
        }
    }

    private int getListAverage(ArrayList<Integer> list) {
        int average=0;
        for(int data:list){
            average += data;
        }
        average = average/list.size();
        return average;

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
            if(time%5==0){
                if(!hrs_5s.isEmpty())
                    hrs.add(getListAverage(hrs_5s));
                else
                    hrs.add(0);
                if(!spo2s_5s.isEmpty())
                    spo2s.add(getListAverage(spo2s_5s));
                else
                    spo2s.add(0);
                Calendar calendar = Calendar.getInstance();
                try {
                    jobj.put("datetime",calendar.getTimeInMillis());
                    jobj.put("spo2",getListAverage(spo2s_5s));
                    jobj.put("hr",getListAverage(hrs_5s));
                    dataJson.put(jobj);
                    hrs_5s.clear();
                    spo2s_5s.clear();
                    Log.d(TAG,dataJson.toString());
                    Log.d(TAG,"data length: "+dataJson.length());
                } catch (JSONException e) {
                    Log.d(TAG,"JSON error "+e.getMessage());
                    e.printStackTrace();
                }
            }
            if(time%60==0){
                if(getListAverage(spo2s)<90){
                    Message msg_s = new Message();
                    msg_s.what=8;
                    taskHandler.sendMessage(msg_s);
                }
//                if(getListAverage(hrs)<90)
                hrs.clear();
                spo2s.clear();
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
                //統計spo2、hr、sbp、dbp數值
                if(!hrs_5s.isEmpty())
                    hrs.add(getListAverage(hrs_5s));
                if(!spo2s_5s.isEmpty())
                    spo2s.add(getListAverage(spo2s_5s));
                if(!dbps_5s.isEmpty())
                    dbps.add(getListAverage(dbps_5s));
                if(!sbps_5s.isEmpty())
                    sbps.add(getListAverage(sbps_5s));
                hrs_5s.clear();
                spo2s_5s.clear();
                dbps_5s.clear();
                sbps_5s.clear();
            }else{
                //檢查前測sensor數值是否正常 (0為不正常)
                if(isPreTestOk()){
                    if(true){
                        //sensor檢查通過，並且各項數值正常
                        workMessage = "恭喜您運動完成";
                        sbpAfter = avgSbp;
                        dbpAfter = avgDbp;
                        msg.what = 5;
                    }else{
                        //後測檢測不通過，重新進行
                        workMessage = "後測標準?";
                        msg.what = 6;
                    }
                }
                else{
                    ////後測檢測不通過，重新進行
                    workMessage = "後測失敗請重新測量";
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
        setContentView(R.layout.activity_main);
        //setup UI
        setUpToolbar();
        initialUI();
        currentPage = 0;
        btnSaveAct.setEnabled(false);
        //connect to BLE service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        myApp =  (MyApp)getApplication();
        //取得權限
        int permission = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSIONS);
        }

        DBHelper dbHelper = DBHelper.getInstance(this);
        steps_today = dbHelper.getSteps(Util.getTodayStart(),Util.getTodayEnd());
        steps_week = dbHelper.getSteps(Util.getLastWeek(),Util.getTodayEnd());
        Log.d(TAG,"getTodayStart:"+Util.getTodayStart());
        Log.d(TAG,"getTodayEnd:"+Util.getTodayEnd());
        setClock();
        //doDailyUpdate();
        startService(new Intent(this, SensorListener.class));
        spo2s_5s = new ArrayList<>();
        hrs_5s = new ArrayList<>();
        sbps_5s = new ArrayList<>();
        dbps_5s = new ArrayList<>();
    }
    private String clockParser(int secs){
        int sec = secs%60;
        int min = secs/60;
        int hour = 0;
        if(min>=60){
            min = min - (secs/3600) * 60;
            hour = secs/3600;
        }
        return String.format("%02d", hour) +":"+ String.format("%02d", min) + ":" + String.format("%02d", sec);
    }
    private void doDailyUpdate() {
        //檢查是oldstep不為0則進行上傳，並設成0
        //update step
        if(Integer.valueOf(MyShared.getData(MainActivity.this,"oldstep","0")) > -1){
            JSONObject jobj = new JSONObject();
            try {
                jobj.put("id","1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myAsyncTask = new HttpTask("POST",jobj,"/env/add",null);
            myAsyncTask.setCallback(this);
            myAsyncTask.execute();
        }
    }
    private void getEnv() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!= null && netInfo.isConnected()){
            String id = "a001";
            myAsyncTask = new HttpTask("GET",null, "/env/getbyuser",id);
            myAsyncTask.setCallback(this);
            myAsyncTask.execute();
        }else{
            tvUpdateTime.setText("無法連線到網路");
        }
    }
    public void initialUI(){
        vf = (ViewFlipper)findViewById(R.id.viewFlipper);
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
        tvText = vf.findViewById(R.id.tvText);
        imageState = vf.findViewById(R.id.imageState);
        tvWatchState.setText("尚未連線");
        tvSPO2State.setText("尚未連線");
        progressBar = vf.findViewById(R.id.progressBar);

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
        navigationView.setCheckedItem(0);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnStartAct :
                    if(workState==STATE_NONE)
                        startExercise();
                    else if(workState==STATE_PRETEST){
                        timer.cancel();
                        endPreTest();
                    }
                    else if(workState==STATE_WORKING){
                        timer.cancel();
                        endExercise();
                    }
                    break;
                case R.id.btnGoAct:
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
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        int step = dbHelper.getSteps(start_time.getTime(), end_time.getTime());
        JSONObject bp = new JSONObject();
        JSONObject bpBefore =  new JSONObject();
        JSONObject bpAfter =  new JSONObject();
        try {
            bpAfter.put("sbp",sbpAfter);
            bpAfter.put("dbp",dbpAfter);
            bpBefore.put("sbp",sbpBefore);
            bpBefore.put("dbp",dbpBefore);
            bp.put("after",bpAfter).put("before",bpBefore);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int h_i_time = dbHelper.getHItime(start_time.getTime(), end_time.getTime());
        int distance = (int) (step*stepSize);
        dbHelper.saveActivity(Util.getToday(), step, bp.toString(), dataJson.toString(),
                h_i_time, distance, start_time.getTime(), end_time.getTime());
        btnSaveAct.setEnabled(false);
        JSONObject jobj = new JSONObject();
        String id ="a001";
        try {
            jobj.put("uid", id);
            jobj.put("step", step);
            jobj.put("bp", bp);
            jobj.put("data", dataJson);
            jobj.put("distance", distance);
            jobj.put("h_i_time", h_i_time);
            jobj.put("start_time", sdf.format(start_time));
            jobj.put("end_time", sdf.format(end_time));
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
    private boolean isOkToWork(){
        avgSpo2=0; avgSbp=0; avgDbp=0; avgHR=0;
        for(int data:spo2s){
            avgSpo2 += data;
        }
        avgSpo2 = avgSpo2/spo2s.size();
        for(int data:sbps){
            avgSbp += data;
        }
        avgSbp = avgSbp/sbps.size();
        for(int data:dbps){
            avgDbp += data;
        }
        avgDbp = avgDbp/dbps.size();
        for(int data:hrs){
            avgHR += data;
        }
        avgHR = avgHR/hrs.size();
        if(avgSpo2<92){
            workMessage = "血氧濃度過低(<92%)";
            return false;
        }
        if(avgHR>100){
            workMessage = "心率過高(>100bpm)";
            return false;
        }else if(avgHR<60){
            workMessage = "心率過高(<60bpm)";
            return false;
        }
        if(avgSbp>159 ){
            workMessage = "收縮壓過高(>159mmHG)";
            return false;
        }else if(avgSbp<90){
            workMessage = "收縮壓過低(<90159mmHG)";
            return false;
        }
        if(avgDbp>99){
            workMessage = "舒張壓過高(>99159mmHG)";

        }else if(avgDbp<60){
            workMessage = "舒張壓過低(<60159mmHG)";
            return false;
        }
        return true;
    }
    private boolean isPreTestOk(){
        if(sbps.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(dbps.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(spo2s.isEmpty()){
            workMessage = "血氧測量不完整";
            return false;
        }
        if(hrs.isEmpty()){
            workMessage = "心率測量不完整";
            return false;
        }
        workMessage = "前測完成";
        return true;
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
        sbps = new ArrayList<>();
        dbps = new ArrayList<>();
        spo2s = new ArrayList<>();
        hrs = new ArrayList<>();
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.bp," );
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void doExercise(){
        if(!checkDevice()) return;
        start_time = Calendar.getInstance().getTime();
        spo2s = new ArrayList<>();
        hrs = new ArrayList<>();
        dataJson = new JSONArray();
        timer = new Timer();
        timer.scheduleAtFixedRate(new ExerciseTask(), 5000, 1000);
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.hr," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.hr," );
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void endExercise(){
        end_time = Calendar.getInstance().getTime();
        mBluetoothLeService.sendData(deviceWatch,"menu.hr," );
        mBluetoothLeService.sendSPO2(deviceSPO2,"b");
        timer.cancel();
        updateUI(STATE_END);
    }
    private void endPreTest(){
        mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
        mBluetoothLeService.sendSPO2(deviceSPO2,"b");
        updateUI(STATE_NONE);
    }
    private void endAfterTest(){
        //the same with endPreTest
        endPreTest();
    }
    private void startAfterTest(){
        updateUI(STATE_AFTERTEST);
        sbps = new ArrayList<>();
        dbps = new ArrayList<>();
        spo2s = new ArrayList<>();
        hrs = new ArrayList<>();
        timer = new Timer();
        timer.scheduleAtFixedRate(new AfterTestTask(), 5000, 5000);
        try {
            mBluetoothLeService.sendData(deviceWatch,"menu.bp," );
            Thread.sleep(300);
            mBluetoothLeService.sendData(deviceWatch,"icon.bp," );
            mBluetoothLeService.sendSPO2(deviceSPO2,"a");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean checkDevice(){
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
            TextView textView = (TextView) ad.findViewById(android.R.id.message);
            textView.setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
            return false;
        }else if(!tvWatchState.getText().equals("已連線")){
            Toast.makeText(MainActivity.this,"裝置搜尋中...",Toast.LENGTH_SHORT).show();
            return false;
        }
//        else if(!mBluetoothLeService.isCharAvaiable(MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString()), DEVICE_TYPE.WATCH)){
//            Toast.makeText(MainActivity.this,"與裝置同步中...",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if(MyShared.getData(MainActivity.this, DEVICE_TYPE.SPO2.toString())==null){
            //Toast.makeText(MainActivity.this, "血氧裝置尚未設定 請到'我的裝置'中設定", Toast.LENGTH_LONG).show();
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
            TextView textView = (TextView) ad.findViewById(android.R.id.message);
            textView.setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
            return false;
        }else if(!tvSPO2State.getText().equals("已連線")){
            Toast.makeText(MainActivity.this,"裝置搜尋中...",Toast.LENGTH_SHORT).show();
            return false;
        }
//        else if(!mBluetoothLeService.isCharAvaiable(MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString()), DEVICE_TYPE.SPO2)){
//            Toast.makeText(MainActivity.this,"與裝置同步中...",Toast.LENGTH_SHORT).show();
//            return false;
//        }
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
                    Toast.makeText(this,"未取得權限",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        envTimer = new Timer();
        envTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                getEnv();
            }
        }, 0, MILLSECONDS_MINUTES);
        deviceWatch=MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString());
        deviceSPO2= MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString());
        DBHelper dbHelper = DBHelper.getInstance(this);
        Log.d(TAG,"getCurrentSteps"+dbHelper.getAll());
        //tvStep.setText(String.valueOf(steps_today));


        tvStepWeek.setText(String.valueOf(steps_week));
        registerReceiver(broadcastReceiver, makeGattUpdateIntentFilter());
        readStepGoal();

        //檢查是否有支援陀螺儀的功能
        SensorManager sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            AlertDialog ad = new AlertDialog.Builder(this).setTitle(R.string.no_sensor)
                    .setMessage(R.string.no_sensor_explain)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            finish();
                        }
                    }).setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
            TextView textView = (TextView) ad.findViewById(android.R.id.message);
            textView.setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
            ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }
    }

    private void readStepGoal() {
        if(steps_week/7<5000)
            stepGoal = 7000;
        else if(steps_week/7>=5000 && steps_week/7<9000)
            stepGoal = 9000;
        else
            stepGoal = 9000;
        Log.d(TAG,"steps_week "+steps_week);
        Log.d(TAG,"stepGoal "+stepGoal);
        progressBar.setMax(stepGoal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(steps_today,true);
        }else{

            progressBar.setProgress(steps_today);
        }

        tvProgress.setText(String.format("%d / %d",steps_today,stepGoal));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        unbindService(mServiceConnection);
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
        unregisterReceiver(broadcastReceiver);
        envTimer.cancel();
    }
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Bluetooth LE Connect status
            Log.d(TAG, "Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            if (!mBluetoothLeService.initialize())
            {
                Log.e(TAG_Broadcast, "Unable to initialize Bluetooth");
                finish();
            }else{
                Log.e(TAG_Broadcast, " Bluetooth initialize~");
            }
            //將BLE Service加入application服務
            myApp.setBluetoothService(mBluetoothLeService);
            // Automatically connects to the device upon successful start-up initialization.
            if(MyShared.getData(MainActivity.this,DEVICE_TYPE.SPO2.toString())!=null)
                connectDevice(DEVICE_TYPE.SPO2);
            if(MyShared.getData(MainActivity.this,DEVICE_TYPE.WATCH.toString())!=null)
                connectDevice(DEVICE_TYPE.WATCH);
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
                //Log.v(TAG_Broadcast," BroadcastReceiver get action: " + action);
                //todo connected UI
                if(type.equals(DEVICE_TYPE.WATCH.toString())){
                    tvWatchState.setText("已連線");
                    tvWatchState.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_Green_600))));
                    Toast.makeText(MainActivity.this,"成功連線到ZoeWatch",Toast.LENGTH_SHORT).show();
                }else{
                    tvSPO2State.setText("已連線");
                    tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_Green_600))));
                    Toast.makeText(MainActivity.this,"成功連線到SPO2",Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(MainActivity.this,"連線成功: "+intent.getStringExtra("address"),Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            }
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
            {
                Log.v(TAG_Broadcast," BroadcastReceiver get action: " + action);
                String dev ="";
                if(type.equals(DEVICE_TYPE.WATCH.toString())){
                    dev = "Zoe手環";
                    tvWatchState.setText("尚未連線");
                    tvWatchState.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_red_700))));
                    Toast.makeText(MainActivity.this,"ZoeWatch斷線",Toast.LENGTH_SHORT).show();
                }else{
                    dev = "血氧裝置";
                    tvSPO2State.setText("尚未連線");
                    tvSPO2State.setTextColor(Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context, R.color.md_red_700))));
                    Toast.makeText(MainActivity.this,"血氧裝置斷線",Toast.LENGTH_SHORT).show();
                }
                if(workState==STATE_PRETEST){
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
                if(type.equals(DEVICE_TYPE.WATCH.toString())){
                    processWatch(data);
                }else{
                    processSPO2(data);
                }
            }
            else  if(BT_CONNECTED_FAIL.equals(action)){

            }
        }

    };
    public void processSPO2(String data){
        tvSPO2.setText(data.trim());
          spo2 = Integer.valueOf(data.trim());
        // calculate every 5 second data, this will be clear in task every 5 sec
        if(workState==STATE_PRETEST || workState==STATE_WORKING || workState==STATE_AFTERTEST){
            if(spo2>0)
                spo2s_5s.add(spo2);
        }
    }
    public void processWatch(String arg1){
        int E_HR,P_HR;
        double PTT,ET,SLP,SYS,DIA;
        switch (arg1.split("=")[0]){
            case "E_HR":
                E_HR = Integer.valueOf(arg1.split("=|,")[1]);
                tvEHR.setText(""+E_HR);
                hr = E_HR;
                if(workState==STATE_PRETEST || workState==STATE_WORKING || workState==STATE_AFTERTEST){
                    if(hr>0)
                        hrs_5s.add(hr);
                }
                break;
            case "P_HR":
                P_HR = Integer.valueOf(arg1.split("=|,")[1]);
                tvEHR.setText(""+P_HR);
                hr = P_HR;
                if(workState==STATE_PRETEST || workState==STATE_WORKING || workState==STATE_AFTERTEST){
                    if(hr>0)
                        hrs_5s.add(hr);
                }
                break;
            case "DIA":
                DIA = Double.valueOf(arg1.split("=|,")[1]);
                tvDBP.setText(""+DIA);
                dbp = (int) DIA;
                if(workState==STATE_PRETEST || workState==STATE_WORKING || workState==STATE_AFTERTEST){
                    if(dbp>0)
                        dbps_5s.add(dbp);
                }
                break;
            case "SYS":
                SYS = Double.valueOf(arg1.split("=|,")[1]);
                tvSBP.setText(""+SYS);
                sbp = (int) SYS;
                if(workState==STATE_PRETEST || workState==STATE_WORKING || workState==STATE_AFTERTEST){
                    if(sbp>0)
                        sbps_5s.add(sbp);
                }
                break;
        }
    }
    private void updateUI(int i) {
        TextView textView;
        AlertDialog ad;
        switch (i){
            case STATE_NONE:
                workState = STATE_NONE;
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
                workState = STATE_PRETEST;
                tvState.setText("開始進行前測");
                btnStartAct.setText("停止");
                btnStartAct.setCompoundDrawablesWithIntrinsicBounds(null, getApplicationContext().getDrawable(R.drawable.ic_stop), null, null);
                break;
            case STATE_WORKING:
                tvState.setText("運動量測中...");
                workState = STATE_WORKING;
                btnStartAct.setText("停止");
                btnStartAct.setCompoundDrawablesWithIntrinsicBounds(null, getApplicationContext().getDrawable(R.drawable.ic_stop), null, null);
                break;
            case STATE_END:
                btnStartAct.setEnabled(false);
                tvState.setText("開始進行後測...");
                tvText.setText("start: "+ stf.format(start_time)+"  ,end: "+stf.format(end_time));
                workState = STATE_END;
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

                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
                break;
            case STATE_BAD_PRETEST:
                btnStartAct.setEnabled(true);
                tvState.setText(workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(workMessage)
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
                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);

                break;
            case STATE_GOOD_PRETEST:
                tvState.setText("前測OK");
                ad = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(workMessage)
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
                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
                break;
            case STATE_BAD_ADVICE:
                tvState.setText("運動前測:不適合運動 原因"+workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("運動建議")
                    .setMessage("提醒您，因為您的"+workMessage+"，如要從事運動，請諮詢醫師意見!")
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
                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
                break;
            case STATE_AFTERTEST:
                tvSPO2.setText("-");
                tvEHR.setText("-");
                tvDBP.setText("-");
                tvSBP.setText("-");
                workState = STATE_AFTERTEST;
                tvState.setText("開始後測");
                break;
            case STATE_BAD_AFTERTEST:
                btnStartAct.setEnabled(true);
                tvState.setText(workMessage);
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(workMessage)
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
                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
                break;
            case STATE_GOOD_AFTERTEST:
                btnStartAct.setEnabled(true);
                btnStartAct.setText("開始");
                btnSaveAct.setEnabled(true);
                tvState.setText(workMessage);
                endAfterTest();
                ad = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(workMessage)
                    .setMessage("記錄資料")
                    .setCancelable(false)
                    .setPositiveButton("是",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //紀錄資料
                    }})
                    .show();
                textView = (TextView) ad.findViewById(android.R.id.message);
                textView.setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
                ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
                break;
        }
    }
    private void startRead(String address){
        for (BluetoothGattService GattService : mGattServices) {
            List<BluetoothGattCharacteristic> mGattCharacteristics = GattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : mGattCharacteristics) {
                String uuidStr = gattCharacteristic.getUuid().toString();
                if (UUID_DATA_WRITE.toString().equals(uuidStr)) {
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address, BluetoothLeService.DEVICE_TYPE.WATCH);
                        mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                }
                else if(uuidStr.equals(SerialPortUUID.toString())){
                    Log.d(TAG_Broadcast,"SerialPortUUID:"+uuidStr);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address,BluetoothLeService.DEVICE_TYPE.SPO2);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                }
                else if(uuidStr.equals(CommandUUID)){
                    Log.d(TAG_Broadcast,"CommandUUID:"+uuidStr);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address,BluetoothLeService.DEVICE_TYPE.SPO2);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                }else if(uuidStr.equals(Characterstic_SPO2_UUID.toString())){
                    Log.d("Characterstic_SPO2_UUID","UUidStr:"+uuidStr);
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true,address,BluetoothLeService.DEVICE_TYPE.SPO2);
                    mBluetoothLeService.readCharacteristic(gattCharacteristic,address);
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(currentPage==1){
            navigationView.setCheckedItem(0);
            vf.setDisplayedChild(0);
            currentPage=0;
            navigationView.setCheckedItem(R.id.nav_main);
            toolbar.setTitle("主頁");
        }else{
            super.onBackPressed();
        }
//        else if(lastPageIndex > -1){
//            vf.setDisplayedChild(lastPageIndex);
//            navigationView.setCheckedItem(lastPageIndex);
//            lastPageIndex = -1;
//        }else{
//            super.onBackPressed();
//        }



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
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            clearData();
            startActivity(intent);
            finish();
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
            startActivityForResult(intent, 0);  //return value

        } else if (id == R.id.nav_history) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,HistoryActivity.class);
            startActivityForResult(intent, 0);  //return value
        }  else if (id == R.id.nav_device) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,DeviceActivity.class);
            startActivityForResult(intent, 0);  //return value
            return false;
        }
        return true;

    }
    public void setClock() {
//        String isSet = MyShared.getData(MainActivity.this, String.valueOf(Util.getToday()));
//        if(isSet!=null &&isSet.equals("Y")){
//            return;
//        }else{
//            MyShared.setData(MainActivity.this, String.valueOf(Util.getToday()),"Y");
//        }
        //設定每天23:55:00上傳今日步數
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PlayReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);
        //參數 setRepeating(喚醒模式, 第一次喚醒時間, 間隔)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        //取消alarm manger方法:alarmMgr.cancel(pendingIntent); ex:request code和class要和要取消的alarm一樣
    }
    @Override
    public void processFinish(int status, String result, String endPoint) {
        switch (endPoint){
            case "/daily/add":
                MyShared.setData(MainActivity.this,"oldstep","0");
                break;
            case "/activity/add":
                Log.d(TAG,"status: "+status);
                Log.d(TAG,"result: "+result);
                Toast.makeText(MainActivity.this,"運動結果上傳成功",Toast.LENGTH_SHORT).show();
                break;
            case "/env/getbyuser":
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
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        Calendar now = Calendar.getInstance();
        int currentStep = DBHelper.getInstance(this).getCurrentSteps();
        int stepsToday = DBHelper.getInstance(this).getSteps(Util.getTodayStart(),now.getTimeInMillis());
        //目前真正的steps = sensor目前值 - db最後存的值 + 今天已經紀錄的步數
        steps_today = (int) event.values[0] - currentStep + stepsToday;
        tvProgress.setText(String.format("%d / %d",steps_today,stepGoal));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void clearData(){
        MyShared.remove(this,DEVICE_TYPE.SPO2.toString());
        MyShared.remove(this,DEVICE_TYPE.WATCH.toString());
        MyShared.remove(this,DEVICE_TYPE.ENV.toString());
        MyShared.remove(this,"oldstep");
        MyShared.remove(this,"stepUnSave");
        MyShared.remove(this,"id");
        MyShared.remove(this,"name");
        MyShared.remove(this,"age");
        MyShared.remove(this,"sex");
        MyShared.remove(this,"history");
        MyShared.remove(this,"bmi");
        MyShared.remove(this,"drug");
        MyShared.remove(this,"env_id");
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        dbHelper.clearTable();
    }
}
