package com.ntust.mitlab.copdwalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class HistoryDetailActivity extends AppCompatActivity {
   TextView tv;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY, tvStartTime, tvTime, tvSteps, tvH_I_Time, tvDBPBefore, tvSBPBefore, tvDBPAfter, tvSBPAfter,tvDistance;
    private JSONObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        setupToolbar();
        initialUI();
        data = getActivityData(getIntent().getStringExtra("row"));
        if(data!=null)
            setUpUI(data);


    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("運動紀錄");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setUpUI(JSONObject data) {
        try {
            Long start_time = data.getLong("start_time");
            Long end_time = data.getLong("end_time");
            int h_i_time = data.getInt("h_i_time");
            int distance = data.getInt("distance");
            int steps = data.getInt("steps");
            JSONObject bp = new JSONObject(data.getString("bp"));
            JSONObject after = new JSONObject(bp.getString("after"));
            JSONObject before = new JSONObject(bp.getString("before"));
            int sbpAfter = (int) after.getDouble("sbp");
            int dbpAfter = (int) after.getDouble("dbp");
            int sbpBefore = (int) before.getDouble("sbp");
            int dbpBefore = (int) before.getDouble("dbp");
            tvStartTime.setText(getDate(start_time));
            tvTime.setText(getTime(start_time, end_time));
            tvH_I_Time.setText(String.valueOf(h_i_time));
            tvSteps.setText(String.valueOf(steps));
//            tvDistance.setText(String.valueOf(distance));
            tvDBPBefore.setText(String.valueOf(dbpBefore));
            tvSBPBefore.setText(String.valueOf(sbpBefore));
            tvDBPAfter.setText(String.valueOf(dbpAfter));
            tvSBPAfter.setText(String.valueOf(sbpAfter));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initialUI() {
        tvStartTime = findViewById(R.id.tvStartTime);
        tvTime = findViewById(R.id.tvTime);
        tvSteps = findViewById(R.id.tvSteps);
        tvH_I_Time = findViewById(R.id.tvH_I_Time);
        tvDBPBefore = findViewById(R.id.tvDBPBefore);
        tvSBPBefore = findViewById(R.id.tvSBPBefore);
        tvDBPAfter = findViewById(R.id.tvDBPAfter);
        tvSBPAfter = findViewById(R.id.tvSBPAfter);
        //tvDistance = findViewById(R.id.tvDistance);

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

    private String getTime(Long start_time, Long end_time) {
        long diffTime = (end_time-start_time)/1000;
        int secs = (int) (diffTime % 60);
        int mins = (int) (diffTime / 60);
        int hours = mins/60;
        mins = mins%60;
        String time = String.format("%02d:%02d:%02d", hours, mins, secs);
        return time;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            Toast.makeText(HistoryDetailActivity.this,"刪除",Toast.LENGTH_SHORT).show();
            return true;
        }else if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
