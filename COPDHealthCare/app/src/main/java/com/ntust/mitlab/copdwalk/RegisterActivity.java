package com.ntust.mitlab.copdwalk;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;
import com.ntust.mitlab.copdwalk.util.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {
    Button btnNext, btnOK, btnBack, btnCancel, btnBack2Drug, btnGo2History;
    EditText  etAccount, etPassword, etPasswordCheck, etLname ,etFname, etAge, etBMI;
    CheckBox cbBerotec, cbBerodualN, cbCombivent, cbSeretide, cbSpiriva, cbAtrovent;
    CheckBox cbDexamethasone, cbHydrocortisone, cbMethylprednisolone, cbDonison, cbPrednisone;
    CheckBox cbHeartDisease, cbHypertension, cbDiabetes, cbArrhythmia, cbHeartFailure, cbStroke;
    EditText etDrugOther, etHistoryOther;
    NumberPicker numberPicker;
    RadioButton radioMale, radioFemale;
    String account, password, passwordCheck,lname, fname,age , sex, bmi;
    Boolean debug = true;
    ViewFlipper vf;
    private boolean validateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        setupToolbar();
        initialUI();
        initialCB();
        if(debug){
            etAccount.setText("abc");
            etPassword.setText("1");
            etPasswordCheck.setText("1");
            etFname.setText("rao");
            etLname.setText("ray");
            etAge.setText("20");
            etBMI.setText("20");
            radioMale.setChecked(debug);
        }

    }

    private void initialCB() {
        cbHeartDisease = (CheckBox) findViewById(R.id.cbHeartDisease);
        cbHypertension = (CheckBox) findViewById(R.id.cbHypertension);
        cbDiabetes = (CheckBox) findViewById(R.id.cbDiabetes);
        cbArrhythmia = (CheckBox) findViewById(R.id.cbArrhythmia);
        cbHeartFailure = (CheckBox) findViewById(R.id.cbHeartFailure);
        cbStroke = (CheckBox) findViewById(R.id.cbStroke);

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
        etDrugOther = findViewById(R.id.etDrugOther);
        etHistoryOther = findViewById(R.id.etHistoryOther);

    }

    private void validateForm() {
        account = etAccount.getText().toString();
        password = etPassword.getText().toString();
        passwordCheck = etPasswordCheck.getText().toString();
        lname = etLname.getText().toString();
        fname  = etFname.getText().toString();
        age = etAge.getText().toString();
        sex = null;
        bmi = etBMI.getText().toString();

        Log.d("pwd",password);
        Log.d("fname",fname);
        Log.d("lname",lname);
        if(radioFemale.isChecked())
            sex = "0";
        else if(radioMale.isChecked())
            sex = "1";

        if(account.equals("")){
            Log.d("val","val");
            etAccount.setError("帳號不能為空白");
            return;
        }else if(!password.equals(passwordCheck)){
            etPassword.setError("確認密碼不同");
            etPasswordCheck.setError("確認密碼不同");
            return;
        }else if(password.equals("")){
            etPassword.setError("密碼不可為空白");
            return;
        }else if(passwordCheck.equals("")){
            etPasswordCheck.setError("密碼不可為空白");
            return;
        }else if(fname.equals("")) {
            etFname.setError("請輸入姓氏");
            return;
        }else if(lname.equals("")) {
            etFname.setError("請輸入名字");
            return;
        }else if(age.equals("")){
            etAge.setError("請輸入年紀");
            return;
        }else if(sex==null){
            Toast.makeText(RegisterActivity.this,"請選擇性別",Toast.LENGTH_SHORT).show();
            return;
        }else if(bmi.equals("")){
            etBMI.setError("請輸入BMI");
            return;
        }else if(Float.parseFloat(bmi)>50){
            etBMI.setError("請輸入正確的BMI");
            return;
        }else if(!validateAccount){
            etAccount.setError("請輸入新的帳號");
            return;
        }
        vf.setDisplayedChild(1);
    }

    private JSONArray getHistory(){
        JSONArray jsonArray = new JSONArray();
        if(cbHeartDisease.isChecked())
            jsonArray.put("HeartDisease");
        if(cbHypertension.isChecked())
            jsonArray.put("Hypertension");
        if(cbDiabetes.isChecked())
            jsonArray.put("Diabetes");
        if(cbArrhythmia.isChecked())
            jsonArray.put("Arrhythmia");
        if(cbHeartFailure.isChecked())
            jsonArray.put("HeartFailure");
        if(cbStroke.isChecked())
            jsonArray.put("Stroke");

        return jsonArray;
    }
    private JSONArray getDrug(){
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
        return jsonArray;
    }
    private String getOtherDrug(){
        if(!etDrugOther.getText().toString().equals("")){
            return etDrugOther.getText().toString();
        }
        return "";
    }
    private String getOtherHistory(){
        if(!etHistoryOther.getText().toString().equals("")){
            return etHistoryOther.getText().toString();
        }
        return "";
    }


    public void initialUI(){
        etAge =  vf.findViewById(R.id.etAge);
        etPassword =  vf.findViewById(R.id.etPassword);
        etPasswordCheck =  vf.findViewById(R.id.etPasswordCheck);
        etFname =  vf.findViewById(R.id.etFname);
        etLname =  vf.findViewById(R.id.etLname);
        radioMale =  vf.findViewById(R.id.radioMale);
        radioFemale =  vf.findViewById(R.id.radioFemale);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnBack2Drug = (Button) findViewById(R.id.btnBack2Drug);
        btnGo2History = (Button) findViewById(R.id.btnGo2History);
        etBMI = vf.findViewById(R.id.etBMI);
        btnGo2History.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.setDisplayedChild(2);
            }
        });
        btnBack2Drug.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf.setDisplayedChild(1);
            }
        });
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vf.setDisplayedChild(0);
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    jobj.put("sex",sex);
                    jobj.put("bmi",bmi);
                    jobj.put("history", historyJobj.toString());
                    jobj.put("drug", drugJobj.toString());
                    jobj.put("history_other", otherHisory);
                    jobj.put("drug_other", otherDrug);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpTask httpTask = new HttpTask("POST",jobj,"/user/add",null);
                httpTask.setCallback(RegisterActivity.this);
                httpTask.execute();
            }
        });
        etAccount = vf.findViewById(R.id.etAccount);
        etAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                HttpTask httpTask = new HttpTask("GET", null, "/user/checkid",etAccount.getText().toString());
                httpTask.setCallback(RegisterActivity.this);
                httpTask.execute();
            }
        });
        etAccount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    Log.d("account",v.getText().toString());
                    HttpTask httpTask = new HttpTask("GET", null, "/user/checkid",v.getText().toString());
                    httpTask.setCallback(RegisterActivity.this);
                    httpTask.execute();
                }
                return false;
            }
        });
    }
    public void finishRegister(String account, String password){
        Intent intent = new Intent();
        intent.putExtra("account",account);
        intent.putExtra("password",password);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("註冊");
        setSupportActionBar(toolbar);
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

    @Override
    public void processFinish(int state, String result, String endPoint) {
        switch (endPoint){
            case "/user/checkid":
                if(state==602){
                    etAccount.setError("帳號已被註冊");
                    validateAccount = false;
                }else
                    validateAccount = true;
                break;
            case "/user/add":
                if(state==200){
//                    finishRegister(account,password);
                    Intent intent = new Intent();
                    intent.putExtra("account",account);
                    intent.setClass(this,MeasurementActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(state==602){
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setMessage("帳號已存在")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    Log.d("state"," "+state);
                }else{
                    new AlertDialog.Builder(RegisterActivity.this)
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("確定離開?")
                .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
