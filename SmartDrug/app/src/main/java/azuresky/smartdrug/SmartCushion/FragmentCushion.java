package azuresky.smartdrug.SmartCushion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import azuresky.smartdrug.MainActivity;
import azuresky.smartdrug.R;
import azuresky.smartdrug.util.ChatService;

public class FragmentCushion extends Fragment {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    public static final int BT_STATUS_DISCONNECTED = 0;
    public static final int BT_STATUS_CONNECTED = 1;
    public static int btStatus = 0;
    public static boolean isFormatCorrect = false;
    private String connectedDeviceName = null;
    private StringBuffer outStringBuffer;
    private ChatService chatService = null;
    private android.support.v7.widget.Toolbar toolBar;
    private static final int NOTIF_ID1 = 1;
    public EditText minute;
    public EditText hour,alarmText;
    public Button button;//開始設定
    public Button button2;//歸零
    public String alarm;
    public int visible=1;
    private View view;
    private Context context;
    private String state="尚未連線";
    private String result,hours,minutes;
    private String MyDateFormat = "yyyy-MM-dd HH:mm:ss";
    private int weight = 1;
    int minute_time = 0;
    int hour_time = 0;
    private Handler handler = new Handler(new Handler.Callback() {

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
                        case ChatService.STATE_NONE:
                            setStatus("智慧坐墊尚未連接");
                            btStatus = BT_STATUS_DISCONNECTED;
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.toString().equalsIgnoreCase("!"))
                        cushionNotify();

                    break;
                case MESSAGE_DEVICE_NAME:

                    connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(context,"Connected to " + connectedDeviceName,Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context,msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });


    public static final FragmentCushion newInstance(){

        FragmentCushion fc = new FragmentCushion();
        Bundle b = new Bundle();
        fc.setArguments(b);
        return fc;



    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            state = getArguments().getString("state");
            hours=getArguments().getString("hour");
            minutes=getArguments().getString("minute");
            Log.d("onCreate","state:"+state+hours+minutes);

        }



    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        view = layoutInflater.inflate(R.layout.cushion_layout, container, false);
        button = (Button)view.findViewById(R.id.button);
        button2=(Button)view.findViewById(R.id.button2);
        alarmText = (EditText)view.findViewById(R.id.alarm);
        button.setOnClickListener(start);
        button2.setOnClickListener(reset);
        alarmText.setOnClickListener(dateTextListener);
        alarmText.setKeyListener(null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("操作說明!");
        dialog.setMessage("請先點選右上角進行連線\n連線成功後即可開始設定時間!");
        dialog.setPositiveButton("確定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                });
        dialog.show();

        if (MainActivity.bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available",Toast.LENGTH_LONG).show();
            getActivity().finish();
            return null;
        }

        return view;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getArguments().putString("state", state);
        //setArguments(getArguments());

        Log.d("onDestroy", "hour:" + getArguments().getString("hour")+" minute:" + getArguments().getString("minute")+" state:" + getArguments().getString("state"));
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



    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras().getString(
                DeviceListActivity.DEVICE_ADDRESS);
        BluetoothDevice device = MainActivity.bluetoothAdapter.getRemoteDevice(address);
        chatService.connect(device, secure);
    }

    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId,
                                      KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };



    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart", "onStart");
        toolBar =  (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);

            toolBar.setSubtitle(state);
            toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
    public void onStop() {
        super.onStop();
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

        outStringBuffer = new StringBuffer("");
    }

    private void sendMessage(String message)  {
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            String str = new String(send, StandardCharsets.UTF_8);
            Log.d("Cushion",str);
            chatService.write(send);

            outStringBuffer.setLength(0);

        }
    }

    public void cushionNotify() {
        NotificationManager notiMgr = (NotificationManager)context.getSystemService(MainActivity.NOTIFICATION_SERVICE);
        NotificationCompat.Builder noti =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.a)
                        .setContentTitle("智慧坐墊")
                        .setContentText("坐很久囉該起來活動一下!")
                        .setAutoCancel(true);
        noti.setVibrate(new long[]{100, 500, 100, 500});
        noti.setLights(Color.RED, 3000, 1000);
        noti.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notiMgr.notify(0, noti.build());   // 送出提醒訊息

    }
    Button.OnClickListener start= new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String output = "";
            isFormatCorrect = true;

//            if(btStatus==BT_STATUS_DISCONNECTED)
//            {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                dialog.setTitle("尚未連線!");
//                dialog.setMessage("您還尚未連接智慧坐墊!");
//                dialog.setPositiveButton("確定",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialoginterface, int i) {
//                            }
//                        });
//                dialog.show();
//                return;
//            }
//
//            if(hour_time==0)
//            {
//                isFormatCorrect = false;
//                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                dialog.setTitle("格式錯誤!");
//                dialog.setMessage("請輸入小時數!若沒有則輸入0");
//                dialog.setPositiveButton("確定",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialoginterface, int i) {
//                            }
//                        });
//                dialog.show();
//            }
//            else
            if(minute_time==0&&hour_time==0)
            {
                isFormatCorrect = false;
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("格式錯誤!");
                dialog.setMessage("請輸入分鐘數!若沒有則輸入0");
                dialog.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                            }
                        });
                dialog.show();
            }
            else if(hour_time > 3)
            {
                isFormatCorrect = false;
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("格式錯誤!");
                dialog.setMessage("超過3小時或輸入格式錯誤!");
                dialog.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                            }
                        });
                dialog.show();
            }
        if(btStatus==BT_STATUS_CONNECTED && isFormatCorrect)
        {
                alarm = String.format("%d 小時 %d 分",hour_time,minute_time);
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat formatter = new SimpleDateFormat(MyDateFormat);
                Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
                String now = formatter.format(curDate);
                params.put("table", "cushion");
                params.put("col1", now);
                params.put("col2", alarm);
                try {
                    doPostAsyn("http://140.118.122.159/mitlab/update.php", getPostDataString(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                output = String.format("%d,%d,%d",weight,hour_time,minute_time);
                Log.d("Cushion Output" , output);
                sendMessage(output);
        }


        }

    };
    Button.OnClickListener reset = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if(btStatus==BT_STATUS_DISCONNECTED)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("尚未連線!");
                dialog.setMessage("您還尚未連接智慧坐墊!");
                dialog.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                            }
                        });
                dialog.show();
                return;
            }
            minute_time = 0;
            hour_time = 0;
            alarmText.setText("00:00");
            sendMessage(weight+",0,0");
        }


    };
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    private void showTimeDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,3,timePickListener, 0, 0, true);
        timePickerDialog.setTitle("選擇時間");
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }
    private TimePickerDialog.OnTimeSetListener timePickListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String s;
            hour_time = hourOfDay;
            minute_time = minute;

            if(minute<10)
                s = hourOfDay+" : "+"0"+minute;//breakfastText.setText();
            else
                s = hourOfDay+" : "+minute;
            alarmText.setText(s);
        }
    };
    private EditText.OnClickListener dateTextListener = new EditText.OnClickListener(){
        @Override
        public void onClick(View view) {
            showTimeDialog();
        }
    };
    //處裡同步工作的方法
    public void doPostAsyn(final String urlStr, final String params) throws Exception
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Please wait");
        dialog.setMessage("Uploading date...");
        dialog.show();
        new Thread()
        {
            public void run()
            {
                try
                {
                    result = doPost(urlStr, params);
                    Log.d("POST", result);
                    //執行完同步工作,handler送出一個msg來操作UI動作
                    Message msg = new Message();
                    msg.what = MESSAGE_TOAST;
                    Bundle b = new Bundle();
                    b.putString(TOAST, "上傳完成");
                    msg.setData(b);
                    handler.sendMessage(msg);
                    dialog.dismiss();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }.start();

    }
    public static String doPost(String url, String param) throws IOException {
        PrintWriter printWriter = null;
        BufferedReader bufReader = null;
        String result="";
        try
        {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            //--- 設定連線的屬性---
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            connection.setDoOutput(true);   //設定是否向http提出請求,預設為false
            connection.setDoInput(true);
            connection.setReadTimeout(10000);   //設定time-out時間
            connection.setConnectTimeout(10000); //設定time-out時間
            //--------------------
            if (param != null && !param.trim().equals(""))
            {
                // 透過PrintWriter處理資料流串接URLConnection獲取的輸出資料流，得到物件實體
                printWriter = new PrintWriter(connection.getOutputStream());
                // 將資料寫至輸出串流
                printWriter.print(param);
                // 強制寫出所有資料至串流中
                printWriter.flush();
            }
            // 透過BufferReader處理資料流串接InputStreamReader節點資料流讀取資料
            bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufReader.readLine()) != null)   //如果還有下一行就繼續讀取
            {
                result += line;
            }
        } catch (Exception e) {
            throw e;
        }
        // 關閉與釋放資源
        finally
        {
            try
            {
                if (printWriter != null)
                    printWriter.close();
                if (bufReader != null)
                    bufReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
