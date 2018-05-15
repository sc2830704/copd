package com.ntust.mitlab.copdwalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ntust.mitlab.copdwalk.util.MyShared;

/**
 * Created by Huang on 2018/5/11.
 */

public class UserMeasurementActivity extends AppCompatActivity {

    private String CAT_Score;

    Button btn30, btn20, btn10, btn0, btnMeasurement;
    TextView tvScore;

    Button.OnClickListener btnListener = new Button.OnClickListener(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn30:
                    intent.setClass(UserMeasurementActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_measurement_artilce_30);
                    bundle.putString("title","分數大於30");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btn20:
                    intent.setClass(UserMeasurementActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_measurement_artilce_20);
                    bundle.putString("title","分數大於20");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btn10:
                    intent.setClass(UserMeasurementActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_measurement_artilce_10);
                    bundle.putString("title","分數介於10到20");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btn0:
                    intent.setClass(UserMeasurementActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_measurement_artilce_0);
                    bundle.putString("title","分數小於10");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnMeasurement:
                    intent.setClass(UserMeasurementActivity.this,MeasurementActivity.class);
                    startActivity(intent);
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_measurement);
        setUpToolbar();
        getdata();
        tvScore = findViewById(R.id.tvScore);
        btn30 = findViewById(R.id.btn30);
        btn20 = findViewById(R.id.btn20);
        btn10 = findViewById(R.id.btn10);
        btn0 = findViewById(R.id.btn0);
        btnMeasurement = findViewById(R.id.btnMeasurement);
        btn30.setOnClickListener(btnListener);
        btn20.setOnClickListener(btnListener);
        btn10.setOnClickListener(btnListener);
        btn0.setOnClickListener(btnListener);
        btnMeasurement.setOnClickListener(btnListener);
        tvScore.setText(CAT_Score);
    }
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CAT量表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    private void getdata() {
        CAT_Score=MyShared.getData(this,"CAT_Score");
        Log.d("Score","Score="+CAT_Score);
    }
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        getdata();
        tvScore.setText(CAT_Score);
        // Activity being restarted from stopped state
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

}
