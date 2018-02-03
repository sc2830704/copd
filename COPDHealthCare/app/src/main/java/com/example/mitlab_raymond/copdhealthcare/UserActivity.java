package com.example.mitlab_raymond.copdhealthcare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mitlab_raymond.copdhealthcare.util.MyShared;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mitlab_raymond on 2017/12/12.
 */

public class UserActivity extends AppCompatActivity {

    TextView tvName;
    ListView listView;
    ListAdapter listAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(getListItem());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(UserActivity.this,id+","+position,Toast.LENGTH_SHORT).show();
            }
        });
        setupToolbar();
    }

    private ListAdapter getListItem() {
        tvName = (TextView)findViewById(R.id.tvName);
        tvName.setText(MyShared.getData(this,"name"));

        List<HashMap<String , String>> list = new ArrayList<>();
        //使用List存入HashMap，用來顯示ListView上面的文字。
        String id = MyShared.getData(this,"id");
        String name = MyShared.getData(this,"name");
        String age = MyShared.getData(this,"age");
        String sex = MyShared.getData(this,"sex").equals("1")?"男":"女";
        String bmi = MyShared.getData(this,"bmi");
        String drug = parseJsonArray(MyShared.getData(this,"drug"));
        String history = parseJsonArray(MyShared.getData(this,"history"));
        String[] title = new String[]{"帳號", "姓名" , "年齡" , "性別" ,"BMI","用藥","病史"};
        String[] text  = new String[]{id, name, age, sex, bmi, drug, history};
        for(int i = 0 ; i < title.length ; i++){
            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("title" , title[i]);
            hashMap.put("text" , text[i]);
            //把title , text存入HashMap之中
            list.add(hashMap);
            //把HashMap存入list之中
        }
        return listAdapter = new SimpleAdapter(this,list, R.layout.listview_user,new String[]{"title" , "text"},new int[]{R.id.text1 , R.id.text2});

    }
    private String parseJsonArray (String jArray){
        String result = "";
        try {
            JSONArray jsonArray = new JSONArray(jArray);
            //取前3樣物件，以逗號分隔，剩餘的以...表示
            for(int i=0; i<jsonArray.length(); i++){
                if(i>=3){
                    result += " ...";
                    break;
                }else if(i==0)
                    result += jsonArray.getString(i);
                else
                    result += ", "+jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("個人資料");
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

}
