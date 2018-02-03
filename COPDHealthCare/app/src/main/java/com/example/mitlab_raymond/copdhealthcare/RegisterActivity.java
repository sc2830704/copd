package com.example.mitlab_raymond.copdhealthcare;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.mitlab_raymond.copdhealthcare.Callback.AsyncResponse;
import com.example.mitlab_raymond.copdhealthcare.Service.HttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements AsyncResponse {
    Button btnNext, btnOK, btnBack, btnCancel;
    EditText  etAccount, etPassword, etPasswordCheck, etLname ,etFname, etAge, etBMI;
    CheckBox cbHeartDisease, cbBloodPressure, cbDiabetes, cbArrhythmia, cbHeartFailure, cbStroke;
    CheckBox cbFenoterol, cbFormoterol, cbIndacaterol, cbSalbutamol, cbSalmeterol, cbVilanterol, cbGlycopyrronium,
    cbUmeclidinium, cbIpratropiumBormide, cbTiotropium, cbBeclomethasone, cbBudesonide, cbFluticasonePropionate,
            cbRoflumilast, cbAminophylline, cbTheophylline, cbOralSteroids;
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
        cbBloodPressure = (CheckBox) findViewById(R.id.cbBloodPressure);
        cbDiabetes = (CheckBox) findViewById(R.id.cbDiabetes);
        cbArrhythmia = (CheckBox) findViewById(R.id.cbArrhythmia);
        cbHeartFailure = (CheckBox) findViewById(R.id.cbHeartFailure);
        cbStroke = (CheckBox) findViewById(R.id.cbStroke);
        cbFenoterol = (CheckBox) findViewById(R.id.cbFenoterol);
        cbFormoterol = (CheckBox) findViewById(R.id.cbFormoterol);
        cbIndacaterol = (CheckBox) findViewById(R.id.cbIndacaterol);
        cbSalbutamol = (CheckBox) findViewById(R.id.cbSalbutamol);
        cbSalmeterol = (CheckBox) findViewById(R.id.cbSalmeterol);
        cbVilanterol = (CheckBox) findViewById(R.id.cbVilanterol);
        cbGlycopyrronium = (CheckBox) findViewById(R.id.cbGlycopyrronium);
        cbUmeclidinium = (CheckBox) findViewById(R.id.cbUmeclidinium);
        cbIpratropiumBormide = (CheckBox) findViewById(R.id.cbIpratropiumBormide);
        cbTiotropium = (CheckBox) findViewById(R.id.cbTiotropium);
        cbBeclomethasone = (CheckBox) findViewById(R.id.cbBeclomethasone);
        cbBudesonide = (CheckBox) findViewById(R.id.cbBudesonide);
        cbFluticasonePropionate = (CheckBox) findViewById(R.id.cbFluticasonePropionate);
        cbRoflumilast = (CheckBox) findViewById(R.id.cbRoflumilast);
        cbAminophylline = (CheckBox) findViewById(R.id.cbAminophylline);
        cbTheophylline = (CheckBox) findViewById(R.id.cbTheophylline);
        cbOralSteroids = (CheckBox) findViewById(R.id.cbOralSteroids);

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
        if(cbBloodPressure.isChecked())
            jsonArray.put("HeartDisease");
        if(cbDiabetes.isChecked())
            jsonArray.put("HeartDisease");
        if(cbArrhythmia.isChecked())
            jsonArray.put("HeartDisease");
        if(cbHeartFailure.isChecked())
            jsonArray.put("HeartDisease");
        if(cbStroke.isChecked())
            jsonArray.put("HeartDisease");

        return jsonArray;
    }
    private JSONArray getDrug(){
        JSONArray jsonArray = new JSONArray();
        if(cbFenoterol.isChecked())
            jsonArray.put("Fenoterol");
        if(cbFormoterol.isChecked())
            jsonArray.put("Formoterol");
        if(cbIndacaterol.isChecked())
            jsonArray.put("Indacaterol");
        if(cbSalbutamol.isChecked())
            jsonArray.put("Salbutamol");
        if(cbSalmeterol.isChecked())
            jsonArray.put("Salmeterol");
        if(cbVilanterol.isChecked())
            jsonArray.put("Vilanterol");
        if(cbGlycopyrronium.isChecked())
            jsonArray.put("Glycopyrronium");
        if(cbUmeclidinium.isChecked())
            jsonArray.put("Umeclidinium");
        if(cbIpratropiumBormide.isChecked())
            jsonArray.put("IpratropiumBormide");
        if(cbTiotropium.isChecked())
            jsonArray.put("Tiotropium");
        if(cbBeclomethasone.isChecked())
            jsonArray.put("Beclomethasone");
        if(cbBudesonide.isChecked())
            jsonArray.put("Budesonide");
        if(cbFluticasonePropionate.isChecked())
            jsonArray.put("FluticasonePropionate");
        if(cbRoflumilast.isChecked())
            jsonArray.put("Roflumilast");
        if(cbAminophylline.isChecked())
            jsonArray.put("Aminophylline");
        if(cbTheophylline.isChecked())
            jsonArray.put("Theophylline");
        if(cbOralSteroids.isChecked())
            jsonArray.put("OralSteroids");

        return jsonArray;
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
        etBMI = vf.findViewById(R.id.etBMI);
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
//                LinearLayout ll = (LinearLayout) findViewById(R.id.test);
//                LayoutTransition layoutTransition = ll.getLayoutTransition();
//                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//                if(ll.getVisibility()==View.VISIBLE)
//                    ll.setVisibility(View.GONE);
//                else
//                    ll.setVisibility(View.VISIBLE);
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
                JSONObject jobj = new JSONObject();
                try {
                    jobj.put("id",account);
                    jobj.put("pwd",password);
                    jobj.put("fname",fname);
                    jobj.put("lname",lname);
                    jobj.put("age",age);
                    jobj.put("sex",sex);
                    jobj.put("bmi",bmi);
                    jobj.put("history",historyJobj.toString());
                    jobj.put("drug",drugJobj.toString());
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
                if(state==200)
                    finishRegister(account,password);
                else
                    Log.d("state"," "+state);
                break;
        }
        Log.d("processFinish ", "endPoint "+endPoint);
        Log.d("processFinish ", "result "+result);
        Log.d("processFinish ", "state "+state);
    }
}
