package com.ntust.mitlab.copdwalk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.HttpTask;
import com.ntust.mitlab.copdwalk.util.MyShared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mitlab_raymond on 2017/12/12.
 */

public class UserActivity extends AppCompatActivity implements AsyncResponse {

    TextView tvName;
    ListView listView;
    ListAdapter listAdapter;
    RadioButton rbmale,rbfemale;
    CheckBox cbBerotec, cbBerodualN, cbCombivent, cbSeretide, cbSpiriva, cbAtrovent;
    CheckBox cbDexamethasone, cbHydrocortisone, cbMethylprednisolone, cbDonison, cbPrednisone;
    CheckBox cbHeartDisease, cbHypertension, cbDiabetes, cbArrhythmia, cbHeartFailure, cbStroke;

    private String account,password,fname,lname;
    private static String id,name, age, sex, bmi, drug, history, drug_other,history_other;
    private JSONArray drugJobj;

    private final int AdTextSize = 25;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        listView = (ListView) findViewById(R.id.listView);

        //取得帳號.密碼.原姓名
        account=MyShared.getData(this,"id");
        password=MyShared.getData(this,"pwd");
        String[] name = MyShared.getData(this,"name").split(" ");
        fname= name[0];
        lname= name[1];
       /* JSONObject object = (JSONObject) new JSONTokener(drug).nextValue();
        JSONArray locations = object.getJSONArray("listings");*/


        listView.setAdapter(getListItem());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long number ) {
                if (position != 0) {
                    switch (position){
                        case 1:
                            //name
                            final EditText etlname = new EditText(UserActivity.this);
                            etDialog(position,etlname);
                            final EditText etfname = new EditText(UserActivity.this);
                            etDialog(position-1,etfname);
                            break;
                        case 2:
                            //age
                            final EditText etage = new EditText(UserActivity.this);
                            etage.setInputType(InputType.TYPE_CLASS_NUMBER);
                            etDialog(position,etage);
                            break;
                        case 3:
                            //sex
                            rbDialog(position);
                            break;
                        case 4:
                            final EditText etbmi = new EditText(UserActivity.this);
                            etbmi.setInputType(InputType.TYPE_CLASS_NUMBER);
                            etDialog(position,etbmi);
                            break;
                        case 5:
                            //drug
                            cbdrugDialog(position);
                            break;
                        /*case 6:
                            //history
                            history=Content;
                            break;
                        case 7:
                            //drug_other
                            drug_other=Content;
                            break;
                        case 8:
                            //history_other
                            history_other=Content;
                            break;*/
                    }
//                Toast.makeText(UserActivity.this,id+","+position,Toast.LENGTH_SHORT).show();
                }
            }
        });
        setupToolbar();
    }

    private ListAdapter getListItem() {
        tvName = (TextView)findViewById(R.id.tvName);
        tvName.setText(MyShared.getData(this,"name"));

        List<HashMap<String , String>> list = new ArrayList<>();
        //使用List存入HashMap，用來顯示ListView上面的文字。
        id = MyShared.getData(this,"id");
        name = MyShared.getData(this,"name");
        age = MyShared.getData(this,"age");
        sex = MyShared.getData(this,"sex").equals("1")?"男":"女";
        bmi = MyShared.getData(this,"bmi");
        drug = parseJsonArray(MyShared.getData(this,"drug"));
        history = parseJsonArray(MyShared.getData(this,"history"));
        drug_other = parseJsonArray(MyShared.getData(this,"drug_other"));
        history_other = parseJsonArray(MyShared.getData(this,"history_other"));
        String[] title = new String[]{"帳號", "姓名" , "年齡" , "性別" ,"BMI","用藥","病史","其他用藥","其他病史"};
        String[] text  = new String[]{id, name, age, sex, bmi, drug, history, drug_other,history_other};
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

    private void etDialog(final int position , final EditText editText){
        String[] title = new String[]{"姓氏", "名字" , "年齡" , "性別" ,"BMI","用藥","病史","其他用藥","其他病史"};
        AlertDialog ad = new AlertDialog.Builder(UserActivity.this)
                .setTitle("修改" + title[position])
                .setMessage("按下\"是\"確認修改")
                .setView(editText)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Content = editText.getText().toString();
                        switch (position){
                            case 0:
                                fname=Content;
                                break;
                            case 1:
                                lname=Content;
                                break;
                            case 2:
                                age=Content;
                                break;

                            case 4:
                                bmi=Content;
                                break;
                        }
                        HttpPost();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

        setDialogParam(ad);
    }

    private void rbDialog(final int position ){
        String[] title = new String[]{"姓氏", "名字" , "年齡" , "性別" ,"BMI","用藥","病史","其他用藥","其他病史"};
        AlertDialog ad = new AlertDialog.Builder(UserActivity.this)
                .setTitle("修改" + title[position])
                .setMessage("按下\"是\"確認修改")
                .setView(R.layout.dialog_sexupdate)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (position){
                            case 3:
                                if(rbfemale.isChecked())
                                    sex = "0";
                                else if(rbmale.isChecked())
                                    sex = "1";
                                break;
                        }
                        HttpPost();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
        rbmale = ad.findViewById(R.id.male);
        rbmale.setChecked(true);
        rbfemale=ad.findViewById(R.id.female);
        setDialogParam(ad);
    }

    private void cbdrugDialog(final int position ){
        String[] title = new String[]{"姓氏", "名字" , "年齡" , "性別" ,"BMI","用藥","病史","其他用藥","其他病史"};
        AlertDialog ad = new AlertDialog.Builder(UserActivity.this)
                .setTitle("修改" + title[position])
                .setMessage("按下\"是\"確認修改")
                .setView(R.layout.dialog_drugupdate)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (position){
                            case 5:
                                drugJobj=getDrug();
                        }
                        HttpPost();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

        cbBerotec = (CheckBox) findViewById(R.id.cbBerotec);
        cbBerodualN = (CheckBox) findViewById(R.id.cbBerodualN);
        cbCombivent = (CheckBox) findViewById(R.id.cbCombivent);
        cbSeretide = (CheckBox) findViewById(R.id.cbSeretide);
        cbSpiriva = (CheckBox) findViewById(R.id.cbSpiriva);
        cbAtrovent = (CheckBox) findViewById(R.id.cbAtrovent);

        cbDexamethasone = (CheckBox) findViewById(R.id.cbDexamethasone);
        cbHydrocortisone = (CheckBox) findViewById(R.id.cbHydrocortisone);
        cbMethylprednisolone = (CheckBox) findViewById(R.id.cbMethylprednisolone);
        cbDonison = (CheckBox) findViewById(R.id.cbDonison);
        cbPrednisone = (CheckBox) findViewById(R.id.cbPrednisone);

        setDialogParam(ad);
    }

    private void setDialogParam(AlertDialog ad){
        TextView textView = (TextView) ad.findViewById(android.R.id.message);
        textView.setTextSize(AdTextSize);
        ad.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(AdTextSize);
        ad.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(AdTextSize);
    }

    private void HttpPost(){
        JSONArray historyJobj = getHistory();
        JSONArray drugJobj = getDrug();
        String otherDrug = getOtherDrug();
        String otherHisory = getOtherHistory();
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("id",account);
            jobj.put("pwd",password);
            jobj.put("fname",fname);
            jobj.put("lname",lname);
            jobj.put("age",age);
            jobj.put("sex",sex.toString());
            jobj.put("bmi",bmi);
            jobj.put("history", historyJobj.toString());
            jobj.put("drug", drugJobj.toString());
            jobj.put("history_other", otherHisory);
            jobj.put("drug_other", otherDrug);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpTask httpTask = new HttpTask("POST",jobj,"/user/update",null);
        httpTask.setCallback(UserActivity.this);
        httpTask.execute();
    }

    private JSONArray getHistory(){
        JSONArray jsonArray = new JSONArray();
        if(cbBerotec.isChecked())
            jsonArray.put("Berotec");
        if(cbBerodualN.isChecked())
            jsonArray.put("BerodualN");
        if(cbCombivent.isChecked())
            jsonArray.put("Combivent");
        if(cbSeretide.isChecked())
            jsonArray.put("Seretide");
        if(cbSpiriva.isChecked())
            jsonArray.put("Spiriva");
        if(cbAtrovent.isChecked())
            jsonArray.put("Atrovent");
        if(cbDexamethasone.isChecked())
            jsonArray.put("Dexamethasone");
        if(cbHydrocortisone.isChecked())
            jsonArray.put("Hydrocortisone");
        if(cbMethylprednisolone.isChecked())
            jsonArray.put("Methylprednisolone");
        if(cbDonison.isChecked())
            jsonArray.put("Donison");
        if(cbPrednisone.isChecked())
            jsonArray.put("Prednisone");
        //jsonArray.put(history);

        return jsonArray;
    }
    private JSONArray getDrug(){
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(drug);
        return jsonArray;
    }
    private String getOtherDrug(){
        if(!drug_other.toString().equals("")){
            return drug_other.toString();
        }
        return "";
    }
    private String getOtherHistory(){
        if(!history_other.toString().equals("")){
            return history_other.toString();
        }
        return "";
    }

    public void setData() {
        Log.d("id",""+id);
        MyShared.setData(this,"name", fname+" "+lname);
        MyShared.setData(this,"pwd", password);
        MyShared.setData(this,"age", age);
        MyShared.setData(this,"sex", sex);
        MyShared.setData(this,"bmi", bmi);
        MyShared.setData(this,"history", history);
        MyShared.setData(this,"history_other", history_other);
        MyShared.setData(this,"drug", drug);
        MyShared.setData(this,"drug_other", drug_other);
    }
    @Override
    public void processFinish(int state, final String result, String endPoint) {
        switch (endPoint){
            /*case "/user/checkid":
                if(state==602){
                    etAccount.setError("帳號已被註冊");
                    validateAccount = false;
                }else
                    validateAccount = true;
                break;*/
            case "/user/update":
                if(state==200){
//                    finishRegister(account,password);
                    new AlertDialog.Builder(UserActivity.this)
                            .setMessage("更新成功!")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setData();
                                }
                            })
                            .show();
                }
                else if(state==602){
                    new AlertDialog.Builder(UserActivity.this)
                            .setMessage("帳號已存在")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    Log.d("state"," "+state);
                }else{
                    new AlertDialog.Builder(UserActivity.this)
                            .setMessage("系統錯誤請稍後重試")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
                break;
        }
        Log.d("processFinish ", "endPoint "+endPoint);
        Log.d("processFinish ", "result "+result);
        Log.d("processFinish ", "state "+state);
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
