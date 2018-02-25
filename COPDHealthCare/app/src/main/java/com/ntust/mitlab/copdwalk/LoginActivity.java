package com.ntust.mitlab.copdwalk;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.util.MyShared;
import com.ntust.mitlab.copdwalk.util.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mitlab_raymond on 2017/12/5.
 */

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    EditText etAccount, etPassword;
    Button btnLogin;
    TextView tvForgetPwd,tvRegister;
    ProgressBar progressBar;
    public final static int REGISTER_CODE = 100;
    private boolean debug = BuildConfig.DEBUG;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialUI();
        if(debug){
            etAccount.setText("a001");
            etPassword.setText("a001");
        }

    }
    private void initialUI() {
        etAccount = (EditText) findViewById(R.id.etAccount);
        etPassword = (EditText) findViewById(R.id.etPwd);
        tvForgetPwd = (TextView) findViewById(R.id.tvForgetPwd);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        btnLogin = (Button) findViewById(R.id.btLogin);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REGISTER_CODE);
            }
        });
        tvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"尚未開放查詢",Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this,IntroduceActivity.class);
//                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString();
                String pwd = etPassword.getText().toString();
                if(account.equals("")||pwd.equals("")){
                    Toast.makeText(LoginActivity.this,"請輸入帳號密碼",Toast.LENGTH_SHORT).show();
                }else{
                    ConnectivityManager cm =
                            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if(netInfo!= null && netInfo.isConnected()){
                        login(account, pwd);
                    }else{
                        Toast.makeText(LoginActivity.this,"無法存取到網路，請稍後重試",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

    }
    private void login(String account, String pwd) {
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("id",account);
            jobj.put("pwd",pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpTask httpTask = new HttpTask("POST",jobj,"/user/login",null);
        httpTask.setCallback(this);
        httpTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void processFinish(int status, String result, String endPoint) {
            switch (endPoint){
                case "/user/login":
                    if(status==200){
                        Log.d("result",result);
                        setupProfile(result);
                        syncData();
//                        Intent intent = new Intent();
//                        intent.setClass(this,MainActivity.class);
//                        startActivity(intent);
//                        this.finish();
//                        progressBar.setVisibility(View.INVISIBLE);
                    }else if(status==604){
                        Toast.makeText(this,"帳號密碼錯誤",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(this,"系統錯誤，請稍後重試",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    break;
                case "/activity/getbyuser":
                    if(status==200){
                        Log.d("result",result);
                        setupActivity(result);
                    }else if(status==601){
                        //無activity資料
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(this,"系統錯誤，請稍後重試",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    Intent intent = new Intent();
                    intent.setClass(this,MainActivity.class);
                    startActivity(intent);
                    this.finish();
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                case "/daily/getbyuser":
                    break;
            }
    }

    private void syncData() {
        String id = MyShared.getData(this,"id");
        HttpTask httpTask = new HttpTask("GET",null,"/activity/getbyuser",id);
        httpTask.setCallback(this);
        httpTask.execute();
    }
    private void syncDaily(){
        String id = MyShared.getData(this,"id");
        HttpTask httpTask = new HttpTask("GET",null,"/activity/getbyuser",id);
        httpTask.setCallback(this);
        httpTask.execute();

    }

    private void setupActivity(String result) {
        String step, bp, data, distance, h_i_time;
        Long start_time, end_time;
        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jobj;
            for(int i=0; i<jsonArray.length(); i++) {
                jobj = jsonArray.getJSONObject(i);

                Log.d("setupActivity","jobj"+jobj.toString());

                step = jobj.get("step").toString();
                bp = jobj.get("bp").toString();
                data = jobj.get("data").toString();
                distance = jobj.get("distance").toString();
                h_i_time = jobj.get("h_i_time").toString();
                start_time = sdf.parse(jobj.get("start_time").toString()).getTime();
                end_time = sdf.parse(jobj.get("end_time").toString()).getTime();

                DBHelper dbHelper = DBHelper.getInstance(this);
                dbHelper.saveActivity(start_time, Integer.parseInt(step), bp, data,
                        Integer.parseInt(h_i_time), Integer.parseInt(distance), start_time, end_time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void setupProfile(String data) {
        try {
            JSONObject jobj = new JSONObject(data);
            Log.d("id",""+jobj.get("id").toString());
            MyShared.setData(this,"id", jobj.getString("id"));
            MyShared.setData(this,"pwd", jobj.getString("pwd"));
            MyShared.setData(this,"name", jobj.getString("fname")+" "+jobj.getString("lname"));
            MyShared.setData(this,"age", jobj.getString("age"));
            MyShared.setData(this,"sex", jobj.getString("sex"));
            MyShared.setData(this,"bmi", jobj.getString("bmi"));
            MyShared.setData(this,"history", jobj.getString("history"));
            MyShared.setData(this,"history_other", jobj.getString("history_other"));
            MyShared.setData(this,"drug", jobj.getString("drug"));
            MyShared.setData(this,"drug_other", jobj.getString("drug_other"));
            MyShared.setData(this,"env_id", jobj.getString("env_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REGISTER_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //login(data.getStringExtra("account"),data.getStringExtra("password"));
                Toast.makeText(LoginActivity.this,"註冊完成，請登入",Toast.LENGTH_LONG).show();
            }else{

            }
        }
    }
}
