package com.ntust.mitlab.copdwalk;

import android.content.Intent;
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
import android.widget.Toast;

import com.ntust.mitlab.copdwalk.Model.RecyclerItemClickListener;
import com.ntust.mitlab.copdwalk.util.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mitlab_raymond on 2018/1/20.
 */

public class HistoryActivity extends AppCompatActivity {
    TextView tv;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupToolbar();
        initialUI();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                List<JSONObject> datas = getDataset();
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
        tv = (TextView)findViewById(R.id.tvText);
        mRecyclerView = (RecyclerView) findViewById(R.id.rcvHistory);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager ,there's another two is GridLayoutManager and taggeredGridLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(getDataset());
        mRecyclerView.setAdapter(mAdapter);

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

    public List<JSONObject> getDataset() {
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

}
