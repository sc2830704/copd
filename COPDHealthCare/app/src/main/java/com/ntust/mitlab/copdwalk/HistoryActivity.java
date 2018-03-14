package com.ntust.mitlab.copdwalk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ntust.mitlab.copdwalk.Model.RecyclerItemClickListener;
import com.ntust.mitlab.copdwalk.util.DBHelper;
import com.ntust.mitlab.copdwalk.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by mitlab_raymond on 2018/1/20.
 */

public class HistoryActivity extends AppCompatActivity {
    TextView empty_view;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    /*************Chart Attribute*********/
    private static final int DEFAULT_DATA = 0;
    private static final int SUBCOLUMNS_DATA = 1;
    private static final int STACKED_DATA = 2;
    private static final int NEGATIVE_SUBCOLUMNS_DATA = 3;
    private static final int NEGATIVE_STACKED_DATA = 4;
    private ColumnChartView chart;
    private ColumnChartData data;
    private boolean hasAxes = true;
    private boolean hasLabels = true;
    private boolean hasLabelForSelected = true;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupToolbar();
        initialUI();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                List<JSONObject> datas = getHistoryDataset();
                JSONObject row = datas.get(position);
                Intent intent = new Intent();
                intent.setClass(HistoryActivity.this, HistoryDetailActivity.class);
                intent.putExtra("row",row.toString());
                startActivity(intent);
            }

            @Override public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));


    }

    private void initialUI() {
        empty_view = findViewById(R.id.empty_view);
        /*****設定recycle view屬性****/
        mRecyclerView = findViewById(R.id.rcvHistory);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager ,there's another two is GridLayoutManager and taggeredGridLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(getHistoryDataset());
        mRecyclerView.setAdapter(mAdapter);

        /*****設定column屬性****/
        chart = findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);
        Viewport v = new Viewport(chart.getMaximumViewport());
        v.left =110;
        chart.setCurrentViewport(v);
        chart.setMaximumViewport(v);
        if (getHistoryDataset().isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }

        generateDefaultData();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("歷史紀錄");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
    private void generateDefaultData() {
        DBHelper dbHelper = DBHelper.getInstance(HistoryActivity.this);
        ArrayList<Integer> stepList = new ArrayList<>();
        ArrayList<AxisValue> axisValues = new ArrayList<>();
        int numColumns = 7;
        //取得每周的步數資料
        for(int i=0; i<numColumns; i++){
            int step = dbHelper.getDailySteps(Util.getDateStart(i-7),Util.getDateStart(i-6));
            stepList.add(step);
            //設定x軸label
            if(i==6)
                axisValues.add(new AxisValue(i).setLabel("昨天"));
            else if(i==5)
                axisValues.add(new AxisValue(i).setLabel("前天"));
            else
                axisValues.add(new AxisValue(i).setLabel(sdf.format(Util.getDateStart(i-7))));
        }
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 7 columns.
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(stepList.get(i), ChartUtils.darkenColor(R.color.md_BlueGrey_800))); //can add with many subcolumns
            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        data = new ColumnChartData(columns);
        data.setValueLabelBackgroundAuto(false);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            Axis axisY = new Axis().setHasLines(true);

            axisX.setName("日期");
            axisX.setTextColor(Color.BLACK);
            axisY.setName("步數");
            axisY.setTextColor(Color.BLACK);
            axisY.setMaxLabelChars(5);
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setColumnChartData(data);

    }
    public List<JSONObject> getHistoryDataset() {
        ArrayList<JSONObject> list = new ArrayList();
        DBHelper dbHelper = DBHelper.getInstance(HistoryActivity.this);
        String data = dbHelper.getAllActivity();
        Log.d("data",data);
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<JSONObject> mData;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTime, tvStep, tvHITime,tvDistance, tvDate;
            public ViewHolder(View v) {
                super(v);
                tvTime =  v.findViewById(R.id.tvTime);
                tvStep =  v.findViewById(R.id.tvStep);
                tvHITime =  v.findViewById(R.id.tvHITime);
                tvDistance =  v.findViewById(R.id.tvDistance);
                tvDate =  v.findViewById(R.id.tvDate);
            }
        }
        public MyAdapter(List<JSONObject> data) {
            mData = data;
        }
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_history, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int h_i_time,steps,distance;
            String  tvTime;
            Long start_time, end_time;
            try {
                Log.d("start_time",""+mData.get(position).get("start_time"));
                Log.d("end_time",""+mData.get(position).get("end_time"));
                distance = (Integer) mData.get(position).get("distance");
                steps = (Integer) mData.get(position).get("steps");
                h_i_time = (Integer) mData.get(position).get("h_i_time");
                start_time = (Long) mData.get(position).get("start_time");
                end_time = (Long) mData.get(position).get("end_time");
                tvTime = getTime(start_time, end_time);
            } catch (JSONException e) {
                distance = 0;
                steps = 0;
                h_i_time =  0;
                tvTime = "-";
                e.printStackTrace();
                return;
            }
            holder.tvTime.setText(tvTime);
            holder.tvStep.setText(String.valueOf(steps));
            holder.tvHITime.setText(String.valueOf(h_i_time));
            holder.tvDistance.setText(String.valueOf(distance));
            holder.tvDate.setText(getDate(start_time));


        }
        @Override
        public int getItemCount() {
            return mData.size();
        }
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
    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
//            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
