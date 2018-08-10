package com.ntust.mitlab.copdwalk;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 */
public class HistoryDetailActivity extends AppCompatActivity {
   TextView tv;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY, tvStartTime, tvTime, tvSteps, tvH_I_Time, tvDBPBefore, tvSBPBefore, tvDBPAfter, tvSBPAfter,tvHRAfter,tvHRbefore,
            tvSPO2After,tvSPO2before,tvDistance;
    private JSONObject actData;
    private LineChartView chart;
    private LineChartData chartData;
    private int numberOfPoints=120;
    private int numberOfLines = 1;
    float[][] randomNumbersTab = new float[2][numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = false;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;
    private boolean pointsHaveDifferentColor;
    private boolean hasGradientToTransparent = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        setupToolbar();
        initialUI();
        actData = getActivityData(getIntent().getStringExtra("row"));
        if(actData!=null)
            setUpUI(actData);


    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("運動紀錄");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setUpUI(JSONObject data) {
        JSONArray exeData = new JSONArray();
        try {
            Long start_time = data.getLong("start_time");
            Long end_time = data.getLong("end_time");
            JSONObject bp = new JSONObject(data.getString("bp"));
            JSONObject after = new JSONObject(bp.getString("after"));
            JSONObject before = new JSONObject(bp.getString("before"));
            exeData = new JSONArray(data.getString("data"));
            int h_i_time = data.getInt("h_i_time");
            int distance = data.getInt("distance");
            int steps = data.getInt("steps");
            int sbpAfter = (int) after.getDouble("sbp");
            int dbpAfter = (int) after.getDouble("dbp");
            int HRAfter = (int) after.getDouble("hr");
            int SPO2After = (int) after.getDouble("spo2");
            int sbpBefore = (int) before.getDouble("sbp");
            int dbpBefore = (int) before.getDouble("dbp");
            int HRBefore = (int) before.getDouble("hr");
            int SPO2Before = (int) before.getDouble("spo2");
            tvStartTime.setText(getDate(start_time));
            tvTime.setText(getTime(start_time, end_time));
            tvH_I_Time.setText(String.valueOf(h_i_time));
            tvSteps.setText(String.valueOf(steps));
//            tvDistance.setText(String.valueOf(distance));
            tvDBPBefore.setText(String.valueOf(dbpBefore));
            tvSBPBefore.setText(String.valueOf(sbpBefore));
            tvDBPAfter.setText(String.valueOf(dbpAfter));
            tvSBPAfter.setText(String.valueOf(sbpAfter));
            tvHRAfter.setText(String.valueOf(HRAfter));
            tvSPO2After.setText(String.valueOf(SPO2After));
            tvHRbefore.setText(String.valueOf(HRBefore));
            tvSPO2before.setText(String.valueOf(SPO2Before));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            generateData(exeData);
        } catch (JSONException e) {
            Toast.makeText(HistoryDetailActivity.this,"opps 出錯ㄌ",Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
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
        tvHRAfter=findViewById(R.id.tvHRAfter);
        tvHRbefore=findViewById(R.id.tvHRBefore);
        tvSPO2After=findViewById(R.id.tvSPO2After);
        tvSPO2before=findViewById(R.id.tvSPO2Before);
        //tvDistance = findViewById(R.id.tvDistance);
        chart = findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

    }
    private void generateData(JSONArray exeData) throws JSONException, ParseException {

        ArrayList<Line> lines = new ArrayList<>();  //要塞進chart的資料
        ArrayList<PointValue> hr = new ArrayList<>();   //hr的資料
        ArrayList<PointValue> spo2 = new ArrayList<>(); //spo2的資料
        ArrayList<AxisValue> axisValues = new ArrayList<>(); // x軸座標和間格
        for(int i=0; i<exeData.length();i++){
            JSONObject obj = (JSONObject) exeData.get(i);
            hr.add(new PointValue(i, (float) obj.getDouble("hr")));
            spo2.add(new PointValue(i, (float) obj.getDouble("spo2")));

            if(exeData.length()>5 && i%(exeData.length()/5)==0){
                axisValues.add(new AxisValue(i).setLabel(sdf.format(new Date(obj.getLong("datetime")))));            }
            else
                axisValues.add(new AxisValue(i).setLabel(""));

        }
        Line line = new Line(hr);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        if (pointsHaveDifferentColor){
            line.setPointColor(ChartUtils.COLORS[(0 + 1) % ChartUtils.COLORS.length]);
        }
        lines.add(line);

        line = new Line(spo2);
        line.setColor(ChartUtils.COLORS[1]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        if (pointsHaveDifferentColor){
            line.setPointColor(ChartUtils.COLORS[(1 + 1) % ChartUtils.COLORS.length]);
        }
        lines.add(line);


        chartData = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            Axis axisSPO2 = new Axis().setHasLines(true);
            Axis axisHR = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("時間");
                axisX.setTextColor(Color.BLACK);
                axisSPO2.setName("SPO2");
                axisSPO2.setTextColor(Color.BLACK);
                axisHR.setName("HR");
                axisHR.setTextColor(Color.BLACK);
            }
            chartData.setAxisXBottom(axisX);
            chartData.setAxisYLeft(axisSPO2);
            chartData.setAxisYRight(axisHR);
        } else {
            chartData.setAxisXBottom(null);
            chartData.setAxisYLeft(null);
        }

        chartData.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(chartData);

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
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
