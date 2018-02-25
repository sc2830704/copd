package com.ntust.mitlab.copdwalk;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.util.Util;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.util.MyShared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and /navigationsystem bar) with user interaction.
 * 每次啟動時須同步部數，檢查myshare中的un-sync資料，未同步的
 *
 */
public class SplashActivity extends AppCompatActivity {

    private boolean isFinished = false;
    private long Mills_In_Days = 24*60*60*1000-1;
    private TextView tvVersion;
    private final String version = "版本V";
    private boolean debug=false;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        saveDaily();
        int SPLASH_TIME_OUT = 2500;
        final Intent intent = new Intent();
        if(debug){
            intent.setClass(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(MyShared.getData(SplashActivity.this,"id")!=null) {
            intent.setClass(SplashActivity.this, MainActivity.class);
            if(haveNetwork()){
                syncDaily();
                syncEnvDevice();
            }
        }
        else
            intent.setClass(SplashActivity.this, LoginActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
        tvVersion = findViewById(R.id.tvVersion);
        String v = String.format(version+"%.1f",Float.valueOf(BuildConfig.VERSION_NAME));
        tvVersion.setText(v);
    }
    private void syncEnvDevice(){
        String isEnvSync = MyShared.getData(SplashActivity.this, "isEnvSync");
        if(isEnvSync!=null && isEnvSync.equals("false")){
            String device_id = MyShared.getData(SplashActivity.this, "env_id");
            JSONObject json = new JSONObject();
            try {
                json.put("id", MyShared.getData(SplashActivity.this, "id"));
                json.put("pwd", MyShared.getData(SplashActivity.this, "pwd"));
                String fname = MyShared.getData(SplashActivity.this, "name").split(" ")[0];
                String lname = MyShared.getData(SplashActivity.this, "name").split(" ")[1];
                json.put("fname", fname);
                json.put("lname", lname);
                json.put("age", MyShared.getData(SplashActivity.this, "age"));
                json.put("sex", MyShared.getData(SplashActivity.this, "sex"));
                json.put("bmi", MyShared.getData(SplashActivity.this, "bmi"));
                json.put("history", MyShared.getData(SplashActivity.this, "history"));
                json.put("drug", MyShared.getData(SplashActivity.this, "drug"));
                json.put("history_other", MyShared.getData(SplashActivity.this, "history_other"));
                json.put("drug_other", MyShared.getData(SplashActivity.this, "drug_other"));
                json.put("env_id", device_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpTask httptask = new HttpTask("POST", json, "/user/update", null);
            httptask.setCallback(new AsyncResponse() {
                @Override
                public void processFinish(int state, String result, String endPoint) {
                    if(state==200){
                        Toast.makeText(SplashActivity.this,"update sucess",Toast.LENGTH_SHORT).show();
                        MyShared.setData(SplashActivity.this,"isEnvSync","true");
                    }
                    else{
                        //如果同步失敗,先存入MyShared
                        MyShared.setData(SplashActivity.this,"isEnvSync","false");
                    }
                }
            });
            httptask.execute();
        }

    }
    private void saveDaily() {
        DBHelper dbHelper = DBHelper.getInstance(SplashActivity.this);
        Long yesterday = Util.getYesterday();
        int steps = dbHelper.getSteps(yesterday, yesterday+Mills_In_Days);
        int h_i_time = dbHelper.getHItime(yesterday.longValue(), yesterday+Mills_In_Days);
        //步數為0就不要新增
        if(steps==0)
            return;
        boolean state = dbHelper.saveDaily(yesterday, steps, h_i_time, (int)(steps*0.35));
        Log.d("SplashActivity","新增昨天的每日步數:"+(state?"成功":"失敗"));
    }
    private void syncDaily() {
        //同步所有今日以前未同步的步數到雲端
        final DBHelper dbHelper = DBHelper.getInstance(SplashActivity.this);
        String data = dbHelper.getUnsyncDaily();
        Log.d("SplashActivity","getUnsyncDaily data: "+data);
        try {
            JSONArray unsync = new JSONArray(data);
            for(int i=0;i<unsync.length();i++){
                final JSONObject row = (JSONObject) unsync.get(i);
                final Long date = (Long) row.get("date");
                String id = MyShared.getData(SplashActivity.this,"id");
                row.remove("sync_flag");
                row.put("uid",id);
                row.put("date",df.format(date));
                row.put("updatetime",sdf.format(Util.now()));
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
//        String data = MyShared.getData(SplashActivity.this,"un-sync");
//        final ArrayList list = new Gson().fromJson(data, ArrayList.class);
////        DBHelper dbHelper = new DBHelper(SplashActivity.this);
//        if(list!=null){
//            for (final Object startTime:list) {
//                int steps = dbHelper.getSteps(((Double)startTime).longValue(), ((Double)startTime).longValue()+Mills_In_Days);
//                int h_i_time = dbHelper.getHItime(((Double)startTime).longValue(), ((Double)startTime).longValue()+Mills_In_Days);
//                JSONObject jobj = new JSONObject();
//                String id = MyShared.getData(SplashActivity.this,"id");
//                try {
//                    jobj.put("uid",id);
//                    jobj.put("step", steps);
//                    jobj.put("date", df.format(Util.getToday()));
//                    jobj.put("distance", steps*0.35);
//                    jobj.put("h_i_time", h_i_time);
//                    jobj.put("updatetime", sdf.format(Util.now()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                HttpTask httpTask = new HttpTask("POST",jobj,"/daily/add",null);
//                httpTask.setCallback(new AsyncResponse() {
//                    @Override
//                    public void processFinish(int state, String result, String endPoint) {
//                        if(state==200){
//                            Toast.makeText(SplashActivity.this,"同步成功",Toast.LENGTH_SHORT).show();
//                            list.remove(startTime);
//                            MyShared.setObject(SplashActivity.this,"un-sync",list);
////                            MyShared.remove(SplashActivity.this ,"un-sync");
//                        }
//                        else{
//                            Toast.makeText(SplashActivity.this,"同步失敗",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                httpTask.execute();
//            }
//        }
    }
    public boolean haveNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo!= null && netInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

}
