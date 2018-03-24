package com.ntust.mitlab.copdwalk;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.ntust.mitlab.copdwalk.Service.BluetoothLeService.DEVICE_TYPE;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.Model.LeDeviceListAdapter;
import com.ntust.mitlab.copdwalk.util.ApManager;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.util.MyException;
import com.ntust.mitlab.copdwalk.util.MyShared;
import com.ntust.mitlab.copdwalk.Service.BluetoothLeService;
import com.ntust.mitlab.copdwalk.Service.SshHelper;
import com.ntust.mitlab.copdwalk.Callback.SshCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ntust.mitlab.copdwalk.MainActivity.REQUEST_ENABLE_BT;

/**
 * Created by mitlab_raymond on 2017/10/3.
 */

public class DeviceActivity extends AppCompatActivity {
    private final String TAG = "DeviceActivity Debug";
    private Button btnWatch, btnSpo2, btnEnv;
    private String deviceAddr_Watch,deviceAddr_SPO2, deviceID_ENV;
    private TextView tvScan,tvWatch,tvEnv,tvSPO2;
    private ListView listBLEDevice;
    private View scanView;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 6000;
    private BluetoothLeService.DEVICE_TYPE deviceType;
    AlertDialog deviceListDialog;
    private BluetoothManager bluetoothManager;
    private MyApp myApp;
    private BluetoothLeService mBluetoothLeService;
    private String wifiSetupCommand, ssid, pwd;
    public static DEVICE_TYPE lastLeftDevice;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;
    EditText etSsid, etPwd;
//    public final String deviceWatch = "DEVICE_WATCH";
//    public final String deviceSPO2 = "DEVICE_SPO2";
//    public final String deviceENV = "DEVICE_ENV";
    private Handler uiHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                String device_id = msg.getData().getString(DEVICE_TYPE.ENV.toString());
                tvEnv.setText(device_id);
                btnEnv.setText("移除");
                MyShared.setData(DeviceActivity.this,DEVICE_TYPE.ENV.toString(),device_id);
                break;
            case 2:
                Toast.makeText(DeviceActivity.this,"環境盒子連線失敗，請確認是否已透過wifi連線到裝置",Toast.LENGTH_LONG).show();
                break;
        }
    }
};
    private SshCallBack sshCallBack =  new SshCallBack() {
        @Override
        public void processFinish(String response, String command) {
            switch (command){
                case "device unreachable":
                    Message errormsg = new Message();
                    errormsg.what=2;
                    uiHandler.sendMessage(errormsg);
                    break;
                case "Exception":
                    Log.d("SSH Exception",response);
                    break;
                case "OK":
                    //設定Env_Id
//                    String device_id = response.substring(2,response.length());//去掉多兩個多的空白字元
                    String device_id = response.substring(2,response.length()).trim(); //去掉多餘空白&換行
                    MyShared.setData(DeviceActivity.this, DEVICE_TYPE.ENV.toString(), device_id);
                    //prepare message for handler
                    Message msg = new Message();
                    msg.what=1;
                    Bundle bundle = new Bundle();
                    bundle.putString(DEVICE_TYPE.ENV.toString(),device_id);
                    msg.setData(bundle);
                    uiHandler.sendMessage(msg);
                    // 開啟hot spot for linkit 7688 DUO
                    try {
                        if(!ApManager.isApOn(DeviceActivity.this)){
                            Log.d("ApManager","opening ap");
                            if(ApManager.configApState(DeviceActivity.this))
                                Log.d("ApManager","open");
                            else;
                        }else{
                            Log.d("ApManager","ap is opened: " + ApManager.isApOn(DeviceActivity.this));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //update device id to cloud
                    updateDeviceID(device_id);

                    break;
            }
        }
    };

    private void updateDeviceID(String device_id) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", MyShared.getData(DeviceActivity.this, "id"));
            json.put("pwd", MyShared.getData(DeviceActivity.this, "pwd"));
            String fname = MyShared.getData(DeviceActivity.this, "name").split(" ")[0];
            String lname = MyShared.getData(DeviceActivity.this, "name").split(" ")[1];
            json.put("fname", fname);
            json.put("lname", lname);
            json.put("age", MyShared.getData(DeviceActivity.this, "age"));
            json.put("sex", MyShared.getData(DeviceActivity.this, "sex"));
            json.put("bmi", MyShared.getData(DeviceActivity.this, "bmi"));
            json.put("history", MyShared.getData(DeviceActivity.this, "history"));
            json.put("drug", MyShared.getData(DeviceActivity.this, "drug"));
            json.put("history_other", MyShared.getData(DeviceActivity.this, "history_other"));
            json.put("drug_other", MyShared.getData(DeviceActivity.this, "drug_other"));
            json.put(DEVICE_TYPE.ENV.toString(), device_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpTask httptask = new HttpTask("POST", json, "/user/update", null);
        Log.d("/user/update","json "+json);
        httptask.setCallback(new AsyncResponse() {
            @Override
            public void processFinish(int state, String result, String endPoint) {
                if(state==200)
                    Toast.makeText(DeviceActivity.this,"update sucess",Toast.LENGTH_SHORT).show();
                else{
                    //如果同步失敗,先存入MyShared
                    MyShared.setData(DeviceActivity.this,"isEnvSync","false");
                    Toast.makeText(DeviceActivity.this,"update failed, state:"+state,Toast.LENGTH_SHORT).show();
                    Log.d("update failed","state: "+state);
                    Log.d("update failed","result: "+result);
                }
            }
        });
        httptask.execute();


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setupToolbar();
        //handler for scan BT device
        mHandler = new Handler();
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter==null)
            finish();
        deviceAddr_Watch = MyShared.getData(this, DEVICE_TYPE.WATCH.toString());
        deviceAddr_SPO2 = MyShared.getData(this, DEVICE_TYPE.SPO2.toString());
        deviceID_ENV = MyShared.getData(this, DEVICE_TYPE.ENV.toString());
        initializeUI();
        myApp =  (MyApp)getApplication();
        mBluetoothLeService = myApp.getBluetoothService();

    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的裝置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    private void initializeUI() {
        scanView = getLayoutInflater().inflate(R.layout.list_device, null);
        deviceListDialog = new AlertDialog.Builder(this).setTitle("請選擇BLE裝置").setView(scanView).create();
        tvScan = scanView.findViewById(R.id.tv_scan);
        tvEnv = (TextView) findViewById(R.id.tvEnv);
        tvSPO2 = (TextView) findViewById(R.id.tvSPO2);
        tvWatch = (TextView) findViewById(R.id.tvWatch);
        btnWatch = (Button) findViewById(R.id.btnWatch);
        btnSpo2 = (Button) findViewById(R.id.btnSpo2);
        btnEnv = (Button) findViewById(R.id.connect_env_sensor);
        btnWatch.setOnClickListener(btnListener);
        btnSpo2.setOnClickListener(btnListener);
        btnEnv.setOnClickListener(btnListener);
        updateUI(5);
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        listBLEDevice = scanView.findViewById(R.id.listBLEDevice);
        listBLEDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //判斷使否還在掃描，如果還在掃描則取消掃描
                mHandler.removeCallbacksAndMessages(null);
                if(mScanning)
                    scanLeDevice(false);
                deviceListDialog.cancel();
                //取得已選擇裝置的相關資訊
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);

                //儲存選擇的裝置名稱與MAC位址
                switch (deviceType){
                    case WATCH:
                        deviceAddr_Watch = device.getAddress();
                        MyShared.setData(DeviceActivity.this,DEVICE_TYPE.WATCH.toString(),deviceAddr_Watch);
                        if(mBluetoothLeService!=null){
                            try {
                                mBluetoothLeService.connectGatt(deviceAddr_Watch);
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                        updateUI(0);
                        break;
                    case SPO2:
                        deviceAddr_SPO2 = device.getAddress();
                        updateUI(2);
                        MyShared.setData(DeviceActivity.this,DEVICE_TYPE.SPO2.toString(),deviceAddr_SPO2);
                        if(mBluetoothLeService!=null){
                            try {
                                mBluetoothLeService.connectGatt(deviceAddr_SPO2);
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case ENV:
                        MyShared.setData(DeviceActivity.this,DEVICE_TYPE.ENV.toString(),deviceID_ENV);
                        break;
                }
            }
        });
        listBLEDevice.setAdapter(mLeDeviceListAdapter);
        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)v;
                if(tv.getText().toString().equals("重新掃描裝置")){
                    tvScan.setText("中止掃描");
                    //清除BLE裝置列表
                    mLeDeviceListAdapter.clear();
                    //更新BLE列表顯示
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    //掃描BLE裝置
                    scanLeDevice(true);
                }
                else {
                    tvScan.setText("重新掃描裝置");
                    //中止掃描BLE裝置
                    scanLeDevice(false);
                }
            }
        });
    }
    public Button.OnClickListener btnListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnWatch:
                    deviceType = BluetoothLeService.DEVICE_TYPE.WATCH;
                    //button根據裝置設定狀況會有兩種行為
                    if(!isDeviceConnect(deviceType)){
                        //掃秒BLE裝置
                        if(mBluetoothAdapter.isEnabled()){
                            showScanDialog();
                            scanLeDevice(true);
                        }
                        else{
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }else{
                        //移除裝置並中斷連線
                        String addr = MyShared.getData(DeviceActivity.this, DEVICE_TYPE.WATCH.toString());
                        mBluetoothLeService.disconnectGatt(addr);
                        lastLeftDevice = DEVICE_TYPE.WATCH;
                        MyShared.remove(DeviceActivity.this,DEVICE_TYPE.WATCH.toString());
                        updateUI(1);
                    }
                    break;
                case R.id.btnSpo2:
                    deviceType = BluetoothLeService.DEVICE_TYPE.SPO2;
                    if(!isDeviceConnect(deviceType)){
                        if(mBluetoothAdapter.isEnabled()){
                            showScanDialog();
                            scanLeDevice(true);
                        }
                        else{
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }else{
                        String addr = MyShared.getData(DeviceActivity.this, DEVICE_TYPE.SPO2.toString());
                        mBluetoothLeService.disconnectGatt(addr);
                        lastLeftDevice = DEVICE_TYPE.SPO2;
                        MyShared.remove(DeviceActivity.this, DEVICE_TYPE.SPO2.toString());
                        updateUI(3);
                    }
                    break;
                case R.id.connect_env_sensor:
                    deviceType = DEVICE_TYPE.ENV;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.System.canWrite(DeviceActivity.this)) {
                                // Do stuff here
                                validateDevice();
                            }
                            else {
                                new AlertDialog.Builder(DeviceActivity.this)
                                        .setTitle("設定Wifi管理權限")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                                intent.setData(Uri.parse("package:" + DeviceActivity.this.getApplicationContext().getPackageName()));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .show();
                            }
                    }else{
                        validateDevice();
                    }
                    break;
            }
        }
    };
    private void validateDevice() {
        if(!isDeviceConnect(deviceType)){
            AlertDialog ad = new AlertDialog.Builder(DeviceActivity.this)
                    .setView(R.layout.dialog_env_setup)
                    .setTitle("設定手機熱點")
                    .setMessage("將自動開啟此熱點服務")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ssid = etSsid.getText().toString();
                            pwd = etPwd.getText().toString();
                            SshHelper sshHelper = new SshHelper("root", "mitlab" ,"192.168.100.1", 22);
                            //ssh 執行結果callback
                            sshHelper.setSshCallBack(sshCallBack);
                            wifiSetupCommand = "sh cmd.sh " + ssid + " " + pwd;
                            sshHelper.execute(wifiSetupCommand);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
            etSsid = ad.findViewById(R.id.ssid);
            etPwd = ad.findViewById(R.id.pwd);

            etSsid.setText(ApManager.getApID(DeviceActivity.this));
            etPwd.setText(ApManager.getKey(DeviceActivity.this));
        }else{
            tvEnv.setText("---");
            btnEnv.setText("驗證");
            MyShared.setData(DeviceActivity.this ,DEVICE_TYPE.OTHERS.toString(), "");
            MyShared.remove(DeviceActivity.this, DEVICE_TYPE.ENV.toString());
        }
    }

    private void updateUI(int step){
        switch (step){
            case 0:
                btnWatch.setText("移除");
                tvWatch.setText(deviceAddr_Watch);
                break;
            case 1:
                btnWatch.setText("配對");
                tvWatch.setText("---");
                break;
            case 2:
                btnSpo2.setText("移除");
                tvSPO2.setText(deviceAddr_SPO2);
                break;
            case 3:
                btnSpo2.setText("配對");
                tvSPO2.setText("---");
                break;
            case 5:
                if(deviceAddr_Watch!=null){
                    tvWatch.setText(deviceAddr_Watch);
                    btnWatch.setText("移除");
                }
                if(deviceAddr_SPO2!=null){
                    tvSPO2.setText(deviceAddr_SPO2);
                    btnSpo2.setText("移除");
                }
                if(deviceID_ENV!=null){
                    tvEnv.setText(deviceID_ENV);
                    btnEnv.setText("移除");
                }
        }
    }
    private void scanLeDevice(final boolean enable) {
        tvScan.setText("中止掃描");
        if (enable) {
            mLeDeviceListAdapter.clear();
            // Stops scanning after a pre-defined scan period.
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    tvScan.setText("重新掃描裝置");
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            tvScan.setText("重新掃描裝置");
        }
    }
    private void showScanDialog(){
        //設定關閉BLE掃描視窗後的工作
        deviceListDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //判斷使否還在掃描，如果還在掃描則取消掃描
                mHandler.removeCallbacksAndMessages(null);
                if(mScanning)
                    scanLeDevice(false);
            }
        });

        deviceListDialog.show();
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mLeDeviceListAdapter.getCount()==0) {
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                            else {
                                boolean find = false;
                                for (int i = 0; i < mLeDeviceListAdapter.getCount(); i++) {
                                    if (mLeDeviceListAdapter.getDevice(i).equals(device)) {
                                        find = true;
                                        break;
                                    }
                                }
                                if(find == false){
                                    mLeDeviceListAdapter.addDevice(device);
                                    mLeDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            };


    private boolean isDeviceConnect(BluetoothLeService.DEVICE_TYPE type) {
        switch (type){
            case WATCH:
                if(MyShared.getData(DeviceActivity.this,DEVICE_TYPE.WATCH.toString())!=null)
                    return true;
                break;
            case SPO2:
                if(MyShared.getData(DeviceActivity.this,DEVICE_TYPE.SPO2.toString())!=null)
                    return true;
                break;
            case ENV:
                if(MyShared.getData(DeviceActivity.this,DEVICE_TYPE.ENV.toString())!=null)
                    return true;
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                setResult(0);
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
