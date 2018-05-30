package com.ntust.mitlab.copdwalk;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.util.MyShared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class MeasurementActivity extends AppCompatActivity {
    private RadioGroup rgMmrc;
    private JSONObject jsonObject = new JSONObject();
    private int mmrc=-1, cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8;
    private RatingBar rt1, rt2, rt3, rt4, rt5, rt6,rt7,rt8;
    private Button btnSend,btnBack2mMRC,btnGo2CAT;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String endPoint_AddEvaluate="/evaluate/add";
    private String account;
    ViewFlipper vf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        setupToolbar();
        initialUI();
        if(getIntent().getStringExtra("account")!=null)
            account = getIntent().getStringExtra("account");
        else
            account = MyShared.getData(this,"id");
    }
    private void initialUI() {
        rgMmrc = vf.findViewById(R.id.rgMmrc);
        rt1 = vf.findViewById(R.id.rt1);
        rt2 = vf.findViewById(R.id.rt2);
        rt3 = vf.findViewById(R.id.rt3);
        rt4 = vf.findViewById(R.id.rt4);
        rt5 = vf.findViewById(R.id.rt5);
        rt6 = vf.findViewById(R.id.rt6);
        rt7 = vf.findViewById(R.id.rt7);
        rt8 = vf.findViewById(R.id.rt8);
        rt1.setStepSize(1);
        rt2.setStepSize(1);
        rt3.setStepSize(1);
        rt4.setStepSize(1);
        rt5.setStepSize(1);
        rt6.setStepSize(1);
        rt7.setStepSize(1);
        rt8.setStepSize(1);
        btnBack2mMRC = (Button) findViewById(R.id.btnBack2mMRC);
        btnGo2CAT = (Button) findViewById(R.id.btnGo2CAT);
        btnBack2mMRC.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.setDisplayedChild(0);
            }
        });
        btnGo2CAT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf.setDisplayedChild(1);
            }
        });
        btnSend = (Button) findViewById(R.id.send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeasurement();
            }
        });
        rgMmrc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb0:
                        mmrc = 0;
                        break;
                    case R.id.rb1:
                        mmrc = 1;
                    case R.id.rb2:
                        mmrc = 2;
                    case R.id.rb3:
                        mmrc = 3;
                    case R.id.rb4:
                        mmrc = 4;
                }
            }
        });
    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("COPD身體評估量表");
        setSupportActionBar(toolbar);
    }
    private void updateMeasurement(){
        if(mmrc==-1)
        {
            Toast.makeText(this ,"請填寫呼吸困難量表", Toast.LENGTH_SHORT).show();
            return;
        }
        setData();
        try {
            jsonObject.put("uid", account);
            jsonObject.put("mmrc", mmrc);
            jsonObject.put("cat1", (int) rt1.getRating());
            jsonObject.put("cat2", (int) rt2.getRating());
            jsonObject.put("cat3", (int) rt3.getRating());
            jsonObject.put("cat4", (int) rt4.getRating());
            jsonObject.put("cat5", (int) rt5.getRating());
            jsonObject.put("cat6", (int) rt6.getRating());
            jsonObject.put("cat7", (int) rt7.getRating());
            jsonObject.put("cat8", (int) rt8.getRating());
            jsonObject.put("datetime", sdf.format(Calendar.getInstance().getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpTask httpTask = new HttpTask("POST", jsonObject,endPoint_AddEvaluate, null);
        httpTask.setCallback(new AsyncResponse() {
            @Override
            public void processFinish(int state, String result, String endPoint) {
                Log.d("state",""+state);
                Log.d("result",""+result);
                switch (endPoint){
                    case "/evaluate/add":
                        if(state==200){
                            String id=MyShared.getData(MeasurementActivity.this,"id");
                            if (id==null)
                                Toast.makeText(MeasurementActivity.this, "註冊完成，請登入", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MeasurementActivity.this, "填寫完畢", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                            Toast.makeText(MeasurementActivity.this ,"系統錯誤請稍後重試", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        httpTask.execute();
    }
    public void setData() {
        int Score;
        Score = (int) rt1.getRating() + (int) rt2.getRating() + (int) rt3.getRating() + (int) rt4.getRating()
                + (int) rt5.getRating() + (int) rt6.getRating() + (int) rt7.getRating() + (int) rt8.getRating();
        Log.d("Score","Score="+Score);
        MyShared.setData(this,"mmRC_Score",Integer.toString(mmrc));
        MyShared.setData(this,"CAT_Score",Integer.toString(Score));
    }
    @Override
    public void onBackPressed() {
        //防止使用者離開
        if(MyShared.getData(MeasurementActivity.this,"id")!=null)
            super.onBackPressed();
    }
}
