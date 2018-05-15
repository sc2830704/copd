package com.ntust.mitlab.copdwalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HealthEducationActivity extends AppCompatActivity {


    Button btnIntroduce, btnEvaluate, btnPrinciple, btnSymptom, btnTherapy, btnFactory, btnControl;
    Button.OnClickListener btnListener = new Button.OnClickListener(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnIntroduce:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_artilce_introduce);
                    bundle.putString("title","疾病簡介");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnEvaluate:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_evaluate);
                    bundle.putString("title","診斷與評估");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnPrinciple:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_principle);
                    bundle.putString("title","身體活動原則");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnSymptom:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_symptom);
                    bundle.putString("title","疾病症狀");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnTherapy:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_therapy);
                    bundle.putString("title","藥物治療");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnFactory:
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_factor);
                    bundle.putString("title","危險因子");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.btnControl :
                    intent.setClass(HealthEducationActivity.this, HealthArticleActivity.class);
                    bundle.putInt("layout",R.layout.content_article_factor_control);
                    bundle.putString("title","危險因子控制");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_education);
        setUpToolbar();
        btnIntroduce = findViewById(R.id.btnIntroduce);
        btnEvaluate = findViewById(R.id.btnEvaluate);
        btnPrinciple = findViewById(R.id.btnPrinciple);
        btnSymptom = findViewById(R.id.btnSymptom);
        btnTherapy = findViewById(R.id.btnTherapy);
        btnFactory = findViewById(R.id.btnFactory);
        btnControl = findViewById(R.id.btnControl);
        btnIntroduce.setOnClickListener(btnListener);
        btnEvaluate.setOnClickListener(btnListener);
        btnPrinciple.setOnClickListener(btnListener);
        btnSymptom.setOnClickListener(btnListener);
        btnTherapy.setOnClickListener(btnListener);
        btnFactory.setOnClickListener(btnListener);
        btnControl.setOnClickListener(btnListener);
    }



    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
