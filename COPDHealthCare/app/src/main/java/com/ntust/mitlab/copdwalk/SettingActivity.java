package com.ntust.mitlab.copdwalk;
import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ntust.mitlab.copdwalk.util.MyShared;
public class SettingActivity extends AppCompatActivity {
    private Switch switchSPO2;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setUpToolbar();
        switchSPO2 = findViewById(R.id.swSPO2);
        switchSPO2.setChecked(Boolean.valueOf(MyShared.getData(this,"switchSPO2"))); //根據上次設定初始化switch狀態
        switchSPO2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new AlertDialog.Builder(SettingActivity.this)
                            .setTitle("警告")
                            .setMessage("開啟此功能將停用血氧手環")
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //目前是在onDestory中更新
                                }
                            })
                            .show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("is spo2 disable",""+switchSPO2.isChecked());
        MyShared.setData(this,"switchSPO2",String.valueOf(switchSPO2.isChecked())); //儲存switch狀態
        MyApp.isSPO2Disable = switchSPO2.isChecked(); //設定application變數
    }
    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("設定");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
