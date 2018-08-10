package com.ntust.mitlab.copdwalk;

import android.content.Intent;
import android.graphics.Color;
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
 * Created by Huang on 2018/7/8.
 */

public class MeasurementDetailActivity extends AppCompatActivity {
    TextView empty_view;
    private RecyclerView mRecyclerView;
    private MeasurementDetailActivity.MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurementdetail);
        setupToolbar();
        initialUI();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                List<JSONObject> datas = getMeasurementDataset();
                JSONObject row = datas.get(position);
                Intent intent = new Intent();
                intent.setClass(MeasurementDetailActivity.this, MeasurementScoreActivity.class);
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
        mRecyclerView = findViewById(R.id.rcvMeasurement);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager ,there's another two is GridLayoutManager and taggeredGridLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MeasurementDetailActivity.MyAdapter(getMeasurementDataset());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("個人量表歷史紀錄");
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
    public List<JSONObject> getMeasurementDataset() {
        ArrayList<JSONObject> list = new ArrayList();
        DBHelper dbHelper = DBHelper.getInstance(MeasurementDetailActivity.this);
        String data = dbHelper.getAllMeasurement();
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

    public class MyAdapter extends RecyclerView.Adapter<MeasurementDetailActivity.MyAdapter.ViewHolder> {
        private List<JSONObject> mData;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvmmrc, tvcat, tvDate;
            public ViewHolder(View v) {
                super(v);
                tvmmrc =  v.findViewById(R.id.tvmmrc);
                tvcat =  v.findViewById(R.id.tvcat);
                tvDate =  v.findViewById(R.id.tvDate);
            }
        }
        public MyAdapter(List<JSONObject> data) {
            mData = data;
        }
        @Override
        public MeasurementDetailActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardview_measurement, parent, false);
            MeasurementDetailActivity.MyAdapter.ViewHolder vh = new MeasurementDetailActivity.MyAdapter.ViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(MeasurementDetailActivity.MyAdapter.ViewHolder holder, int position) {
            int mmrc, cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, CATScore;
            Long update_time;
            try {
                Log.d("update_time",""+mData.get(position).get("update_time"));
                mmrc = (Integer) mData.get(position).get("mmrc");
                cat1 = (Integer) mData.get(position).get("cat1");
                cat2 = (Integer) mData.get(position).get("cat2");
                cat3 = (Integer) mData.get(position).get("cat3");
                cat4 = (Integer) mData.get(position).get("cat4");
                cat5 = (Integer) mData.get(position).get("cat5");
                cat6 = (Integer) mData.get(position).get("cat6");
                cat7 = (Integer) mData.get(position).get("cat7");
                cat8 = (Integer) mData.get(position).get("cat8");
                update_time = (Long) mData.get(position).get("update_time");
                CATScore = cat1+ cat2+ cat3+ cat4+ cat5+ cat6+ cat7+ cat8;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            holder.tvmmrc.setText(String.valueOf(mmrc));
            holder.tvcat.setText(String.valueOf(CATScore));
            holder.tvDate.setText(getDate(update_time));


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
