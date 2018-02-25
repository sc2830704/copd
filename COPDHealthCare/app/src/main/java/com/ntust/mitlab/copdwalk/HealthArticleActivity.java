package com.ntust.mitlab.copdwalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HealthArticleActivity extends AppCompatActivity {


    public TextView tvAge, tvSmoke, tvCarer, tvAirPullet, tvSecondSmoke, tvHumdity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_article);

        String title = getIntent().getExtras().getString("title");
        int layoutID = getIntent().getExtras().getInt("layout");

        setUpToolbar(title);
        View C = findViewById(R.id.container);
        ViewGroup parent = (ViewGroup) C.getParent();
        int index = parent.indexOfChild(C);
        parent.removeView(C);
        C = getLayoutInflater().inflate(layoutID, parent, false);
        parent.addView(C, index);
        //initialUI(layoutID);



    }

    private void initialUI(int layoutID) {
        switch (layoutID){
            case R.layout.content_article_factor:
                tvAge = findViewById(R.id.tvAge);
                SpannableStringBuilder builder = new SpannableStringBuilder(tvAge.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvAge.setText(builder);

                tvAirPullet = findViewById(R.id.tvAirPullet);
                builder = new SpannableStringBuilder(tvAirPullet.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvAirPullet.setText(builder);

                tvCarer = findViewById(R.id.tvCarer);
                builder = new SpannableStringBuilder(tvCarer.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvCarer.setText(builder);

                tvSmoke = findViewById(R.id.tvSmoke);
                builder = new SpannableStringBuilder(tvSmoke.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSmoke.setText(builder);

                tvCarer = findViewById(R.id.tvCarer);
                builder = new SpannableStringBuilder(tvCarer.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvCarer.setText(builder);


                tvSecondSmoke = findViewById(R.id.tvSecondSmoke);
                builder = new SpannableStringBuilder(tvSecondSmoke.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSecondSmoke.setText(builder);

                tvHumdity = findViewById(R.id.tvHumdity);
                builder = new SpannableStringBuilder(tvHumdity.getText().toString());
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvHumdity.setText(builder);

                break;
        }
    }

    private void setUpToolbar(String title) {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(title);
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
