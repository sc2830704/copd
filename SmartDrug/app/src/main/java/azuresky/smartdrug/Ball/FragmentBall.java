package azuresky.smartdrug.Ball;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import azuresky.smartdrug.MainActivity;
import azuresky.smartdrug.R;
import azuresky.smartdrug.SmartCushion.DeviceListActivity;
import azuresky.smartdrug.SmartCushion.FragmentCushion;
import azuresky.smartdrug.util.ChatService;

/**
 * Created by game on 2017/9/1.
 */

public class FragmentBall extends Fragment {
    private String TAG = "FragmentBall Debug";
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    public static final int BT_STATUS_DISCONNECTED = 0;
    public static final int BT_STATUS_CONNECTED = 1;
    public static final int ACCELEROMETER_TOTAL_ADD = 10;
    private float mLastX;                    //x軸體感(Sensor)偏移
    private float mLastY;                    //y軸體感(Sensor)偏移
    private float mLastZ;                    //z軸體感(Sensor)偏移
    private double mSpeed;                 //甩動力道數度
    private static final int SPEED_SHRESHOLD = 2000;
    //觸發間隔時間
    private static final int UPTATE_INTERVAL_TIME = 70;
    private static final int SHAKE_INTERVAL_TIME = 2000;
    private long mLastUpdateTime,mLastShakeTime;           //觸發時間
    private boolean isAThrow = false;
    private int total,hit;
    public static final String TOAST = "toast";
    public static final String DEVICE_NAME = "device_name";
    public static int btStatus = 0;
    private String connectedDeviceName;
    private String state;
    private View view;
    private Button reset, start, pause;
    private TextView counter, score;
    private Context context;
    private Toolbar toolBar;
    private ChatService chatService;
    private SensorManager mSensorManager;   //體感(Sensor)使用管理
    private Sensor mSensor;                 //體感(Sensor)類別
    private Timer timer;
    private Handler handler = new Handler(new Handler.Callback()
    {

        @Override
        public boolean handleMessage(Message msg ) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to,connectedDeviceName));
                            btStatus = BT_STATUS_CONNECTED;
                            break;
                        case ChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case ChatService.STATE_LISTEN:
                        case ChatService.STATE_DISCONNECTED:
                            updateUI(4);
                        case ChatService.STATE_NONE:
                            setStatus("丟球裝置尚未連接");

                            btStatus = BT_STATUS_DISCONNECTED;
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String data = readMessage.toString();
                    updateMessage(data);
                    Log.d("MESSAGE_READ","data: "+data);

                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(context,"Connected to " + connectedDeviceName,Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context,msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case ACCELEROMETER_TOTAL_ADD:
                    updateUI(1);
            }
            return false;
        }
    });

    private void updateMessage(String data) {

        if(data.equals("1")){
            hit = hit+1;
            updateUI(1);
        }


    }

    Button.OnClickListener btnListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.start:
                    if(isBTConnected()){
                        sendMessage("start");
                        updateUI(5);
                        timer = new Timer();
                        timer.schedule(getTask(),0,1000);
                        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("裝置未連線")
                                .setMessage("是否連線藍芽裝置")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                                        startActivityForResult(serverIntent, FragmentCushion.REQUEST_CONNECT_DEVICE_SECURE);
                                    }
                                })
                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builder.create().show();
                    }
                    break;
                case R.id.reset:
                    hit = 0;
                    total = 0;
                    timer.cancel();
                    updateUI(4);
                    sendMessage("end");
                    mSensorManager.unregisterListener(mSensorListener,mSensor);
                    break;
                case R.id.pause:
                    if(pause.getText().equals("pause")){
                        updateUI(2);
                        mSensorManager.unregisterListener(mSensorListener,mSensor);
                    }
                    else{
                        updateUI(3);
                        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
                    }

                    break;
            }
        }
    };

    private TimerTask getTask() {
        TimerTask oneSecTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what=1;
                myHandler.sendMessage(msg);
            }
        };
        return oneSecTask;
    }

    private SensorEventListener mSensorListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent mSensorEvent)
        {
            //當前觸發時間
            long mCurrentUpdateTime = System.currentTimeMillis();

            //觸發間隔時間 = 當前觸發時間 - 上次觸發時間
            long mTimeInterval = mCurrentUpdateTime - mLastUpdateTime;
            long shakeInterval = mCurrentUpdateTime - mLastShakeTime;
            //若觸發間隔時間< 70 則return;
            if (mTimeInterval < UPTATE_INTERVAL_TIME) return;

            mLastUpdateTime = mCurrentUpdateTime;

            //取得xyz體感(Sensor)偏移
            float x = mSensorEvent.values[0];
            float y = mSensorEvent.values[1];
            float z = mSensorEvent.values[2];

            //甩動偏移速度 = xyz體感(Sensor)偏移 - 上次xyz體感(Sensor)偏移
            float mDeltaX = x - mLastX;
            float mDeltaY = y - mLastY;
            float mDeltaZ = z - mLastZ;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            //體感(Sensor)甩動力道速度公式
            mSpeed = Math.sqrt(mDeltaX * mDeltaX + mDeltaY * mDeltaY + mDeltaZ * mDeltaZ)/ mTimeInterval * 10000;

            //若體感(Sensor)甩動速度大於等於甩動設定值則進入 (達到甩動力道及速度)

            if (mSpeed >= SPEED_SHRESHOLD)
            {
                //達到搖一搖甩動後要做的事情
                if(shakeInterval > SHAKE_INTERVAL_TIME) {
                    mLastShakeTime = mCurrentUpdateTime;
                    isAThrow = false;                            //避免連續計數
                    total = total + 1;                        //基底+1
                    Message message = new Message();
                    //傳送訊息1
                    message.what = ACCELEROMETER_TOTAL_ADD;                       //計數顯示
                    handler.sendMessage(message);
                    Log.d(TAG, "Shaking 1 time");
                    mSpeed=0;
                }else{
                    Log.d(TAG,"During Interval "+shakeInterval);
                }

            }
        }

        public void onAccuracyChanged(Sensor sensor , int accuracy)
        {
        }
    };
    public FragmentBall() {
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ball,container,false);
        return view;
    }
    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            //if(message.what==1)
                //Log.d("what",1+"");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            hit = savedInstanceState.getInt("hit");
            total = savedInstanceState.getInt("total");
        }
        timer = new Timer();
        connectedDeviceName = null;
        state = "尚未連線";
        //取得體感(Sensor)服務使用權限
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        //取得手機Sensor狀態設定
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //註冊體感(Sensor)甩動觸發Listener
        //mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        start = (Button)view.findViewById(R.id.start);
        reset = (Button) view.findViewById(R.id.reset);
        pause = (Button) view.findViewById(R.id.pause);
        counter = (TextView) view.findViewById(R.id.counter);
        score = (TextView) view.findViewById(R.id.score);
        start.setOnClickListener(btnListener);
        reset.setOnClickListener(btnListener);
        pause.setOnClickListener(btnListener);
        updateUI(0);
    }


    private void updateUI(int step){

        switch (step){
            //initial
            case 0:
                counter.setText("0 / 0");
                score.setText("0");
                pause.setEnabled(false);
                reset.setEnabled(false);
                break;
            //update counter & score
            case 1:
                counter.setText(String.format("%d / %d",hit,total));
                score.setText(String.format("%d",(hit*10)));
                break;
            //plus
            case 2:
                pause.setText("continue");
                break;
            //continue
            case 3:
                pause.setText("pause");
                break;
            //reset
            case 4:
                start.setEnabled(true);
                reset.setEnabled(false);
                pause.setEnabled(false);
                counter.setText("0 / 0");
                score.setText("0");
                break;
            //start
            case 5:
                start.setEnabled(false);
                reset.setEnabled(true);
                pause.setEnabled(true);
                break;
        }
    }
    private final void setStatus(int resId) {
        // final ActionBar actionBar = getSupportActionBar();
        toolBar.setSubtitle(resId);
    }
    private final void setStatus(CharSequence subTitle) {
        //final ActionBar actionBar = getSupportActionBar();
        state = subTitle.toString();
        toolBar.setSubtitle(subTitle);
    }
    private void setupChat() {
        chatService = new ChatService(context, handler);
        //outStringBuffer = new StringBuffer("");
    }
    private void sendMessage(String message)  {
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            String str = new String(send, StandardCharsets.UTF_8);
            Log.d("Cushion",str);
            chatService.write(send);
            //outStringBuffer.setLength(0);

        }
    }
    public void ensureDiscoverable() {
        if (MainActivity.bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(
                DeviceListActivity.DEVICE_ADDRESS);
        BluetoothDevice device = MainActivity.bluetoothAdapter.getRemoteDevice(address);
        chatService.connect(device, secure);
    }
    private boolean isBTConnected(){
        if(btStatus == BT_STATUS_CONNECTED)
            return true;
        else
            return false;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "onStart");
        toolBar =  (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolBar.setSubtitle(state);
        toolBar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.disconnect:
                        chatService.disconnect();
                        return true;
                    case R.id.Connect:
                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                        startActivityForResult(serverIntent, FragmentCushion.REQUEST_CONNECT_DEVICE_SECURE);
                        return true;
                    case R.id.discoverable:
                        ensureDiscoverable();
                        return true;
                }
                return false;
            }
        });

        if (!MainActivity.bluetoothAdapter.isEnabled()) {
            Log.d("onStart if","onStart if");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {

            Log.d("onStart else if","onStart else if");
            if (chatService == null)
                setupChat();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "requestCode:" + requestCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(context, R.string.bt_not_enabled_leaving,Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }
    @TargetApi(23)
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.context = activity;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("hit",hit);
        outState.putInt("total",total);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorListener);
    }
}
