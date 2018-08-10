package com.ntust.mitlab.copdwalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Huang on 2018/7/18.
 */


public class MeasurementScoreActivity extends AppCompatActivity {
    private JSONObject actData;
    private TextView tvTime, tvmmRCScore, tvcat1, tvcat2, tvcat3, tvcat4, tvcat5, tvcat6, tvcat7, tvcat8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_score);
        setupToolbar();
        initialUI();
        actData = getActivityData(getIntent().getStringExtra("row"));
        if(actData!=null)
            setUpUI(actData);


    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("量表分數紀錄");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setUpUI(JSONObject data) {
        try {
            Long update_time = data.getLong("update_time");
            int mmrc = data.getInt("mmrc");
            int cat1 = data.getInt("cat1");
            int cat2 = data.getInt("cat2");
            int cat3 = data.getInt("cat3");
            int cat4 = data.getInt("cat4");
            int cat5 = data.getInt("cat5");
            int cat6 = data.getInt("cat6");
            int cat7 = data.getInt("cat7");
            int cat8 = data.getInt("cat8");
            tvTime.setText(getDate(update_time));
            switch (String.valueOf(mmrc)){
                case "0":
                    tvmmRCScore.setText("０級：我只有在激烈運動時才感覺到呼吸困難。");
                    break;
                case "1":
                    tvmmRCScore.setText("１級：我在平路快速行走或上小斜坡時感覺呼吸短促。");
                    break;
                case "2":
                    tvmmRCScore.setText("２級：我在平路時即會因呼吸困難而走得比同齡的朋友慢，或是我以正常步調走路時必須停下來才能呼吸。");
                    break;
                case "3":
                    tvmmRCScore.setText("３級：我在平路約行走 100 公尺或每隔幾分鐘就需停下來呼吸。");
                    break;
                case "4":
                    tvmmRCScore.setText("４級：我因為呼吸困難而無法外出，或是穿脫衣物時感到呼吸困難。");
                    break;
                default:
                    tvmmRCScore.setText("請重新填寫量表");
                    break;
            }
            tvcat1.setText(String.valueOf(cat1)+"分");
            tvcat2.setText(String.valueOf(cat2)+"分");
            tvcat3.setText(String.valueOf(cat3)+"分");
            tvcat4.setText(String.valueOf(cat4)+"分");
            tvcat5.setText(String.valueOf(cat5)+"分");
            tvcat6.setText(String.valueOf(cat6)+"分");
            tvcat7.setText(String.valueOf(cat7)+"分");
            tvcat8.setText(String.valueOf(cat8)+"分");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initialUI() {

        tvTime = findViewById(R.id.tvTime);
        tvmmRCScore = findViewById(R.id.tvmmRCScore);
        tvcat1 = findViewById(R.id.tvcat1);
        tvcat2 = findViewById(R.id.tvcat2);
        tvcat3 = findViewById(R.id.tvcat3);
        tvcat4 = findViewById(R.id.tvcat4);
        tvcat5 = findViewById(R.id.tvcat5);
        tvcat6 = findViewById(R.id.tvcat6);
        tvcat7 = findViewById(R.id.tvcat7);
        tvcat8 = findViewById(R.id.tvcat8);

    }
    private JSONObject getActivityData(String row) {
        try {
            return new JSONObject(row);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getDate(Long timemills) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        return sdf.format(new Date(timemills));
    }
}
