package com.example.mitlab_raymond.copdhealthcare;

import android.content.Intent;
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

import com.example.mitlab_raymond.copdhealthcare.Callback.AsyncResponse;
import com.example.mitlab_raymond.copdhealthcare.util.MyShared;
import com.example.mitlab_raymond.copdhealthcare.Service.HttpTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mitlab_raymond on 2017/12/5.
 */

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    EditText etAccount, etPassword;
    Button btnLogin;
    TextView tvForgetPwd,tvRegister;
    ProgressBar progressBar;
    public final static int REGISTER_CODE = 100;
    private boolean debug = true;
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
                }else
                    login(account, pwd);
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

    }
    private void login(String account, String pwd) {
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
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
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
                        setupProfile(result);
                        Intent intent = new Intent();
                        intent.setClass(this,MainActivity.class);
                        startActivity(intent);
                        this.finish();
                        progressBar.setVisibility(View.INVISIBLE);
                    }else if(status==604){
                        Toast.makeText(this,"帳號密碼錯誤",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(this,"系統錯誤",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
    }

    public void setupProfile(String data) {
        try {
            JSONObject jobj = new JSONObject(data);
            Log.d("id",""+jobj.get("id").toString());
            MyShared.setData(this,"id", jobj.getString("id"));
            MyShared.setData(this,"name", jobj.getString("fname")+jobj.getString("lname"));
            MyShared.setData(this,"age", jobj.getString("age"));
            MyShared.setData(this,"sex", jobj.getString("sex"));
            MyShared.setData(this,"history", jobj.getString("history"));
            MyShared.setData(this,"bmi", jobj.getString("bmi"));
            MyShared.setData(this,"drug", jobj.getString("drug"));
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
