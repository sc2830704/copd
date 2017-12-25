package azuresky.smartdrug;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import azuresky.smartdrug.DAO.ScheduleDao;
import azuresky.smartdrug.DAO.ScheduleDaoFactory;
import azuresky.smartdrug.DAO.ScheduleStorage;

/**
 * Created by sky on 2016/2/22.
 */
public class HintActivity extends AppCompatActivity {

    private TextView usage_text;
    private ImageView usage_img;
    private Button complete;
    private String usage;
    private TextView textView;
    private ImageView imgView;
    private List<Integer> checkItem = new ArrayList();
    private int count = 0;
    private String[] state ={"感到呼吸不順","感到頭痛或頭暈","覺得全身無力或虛弱"};
    private int[] img={R.drawable.breath,R.drawable.headache,R.drawable.weak};;
    private final String URI = "http://140.118.122.159/mitlab/update.php";
    private ScheduleDao sdao;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd", Locale.TAIWAN);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.TAIWAN);
    private Message msg;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(HintActivity.this,"上傳資料成功",Toast.LENGTH_LONG).show();
                    finish();
                case 2:
                    Toast.makeText(HintActivity.this, "伺服器連線逾時，請稍後重試", Toast.LENGTH_LONG).show();
                    break;

            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        sdao = new ScheduleDaoFactory().createScheduleDao(HintActivity.this);
        //初始化原件與設定listener
        Toolbar myToolBar = (Toolbar)findViewById(R.id.myToolbar);
        setSupportActionBar(myToolBar);
        myToolBar.setTitleTextColor(getResources().getColor(R.color.color_primaryText));
        //myToolBar.setNavigationIcon(R.mipmap.backspqce);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        myToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        usage_text = (TextView)findViewById(R.id.usage_text);
        usage_img = (ImageView)findViewById(R.id.usage_img);
        complete = (Button)findViewById(R.id.complete);
        //處理藥盒資料
        final int index = getIntent().getIntExtra("index", 0);

        setViewResource(index);
        final String data = "check"+index;
        complete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(HintActivity.this)
                        .setTitle("身體狀況評估")
                        .setMessage("確定送出後即不可更改")
                        .setPositiveButton("上傳資料", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(checkItem.get(0));
                                sb.append(checkItem.get(1));
                                sb.append(checkItem.get(2));
                                ScheduleStorage.setData(HintActivity.this, data, sb.toString());
                                if(index==2 || index==5 || index ==8)
                                    doNetwork(index);
                                else
                                    finish();
                            }
                        })
                        .show();
                check(state, img);
            }
        });



    }

    private void setViewResource(int index) {

        String box = String.valueOf(index / 3 + 1);
        String[] boxData = ScheduleStorage.getData(this, "ff" + box).split("\\|");
        if(boxData[11].equals("口服藥")){
            usage = boxData[11]+"，請配溫開水服用";
            usage_img.setImageResource(R.mipmap.drug1);
        }
        else{
            usage = boxData[11]+"，請將藥物塗抹於患處";
            usage_img.setImageResource(R.mipmap.drug_cream);
        }
        usage_text.setText(usage);
    }


    private void check(final String[] text, final int[] resource){
        //服藥後身體狀況調查
        AlertDialog ad = new AlertDialog.Builder(HintActivity.this)
                .setTitle("身體狀況")
                .setView(R.layout.check_dialog)
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkItem.add(1);
                        if (count < 3)
                            check(text, resource);
                        else
                            count = 0;

                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkItem.add(0);
                        if (count < 3)
                            check(text, resource);
                        else
                            count = 0;
                    }
                })
                .show();
        Log.d("test", count + "");
        if(count<3){
            textView = (TextView)(ad.findViewById(R.id.check_text));
            textView.setText(text[count]);
            imgView = (ImageView)(ad.findViewById(R.id.check_image));
            imgView.setImageResource(resource[count]);
            count++;
        }



    }
    public void doNetwork(int index){

        HashMap<String, String> params = new HashMap<>();
        Schedule s = sdao.get(index / 3);
        params.put("table","box");
        params.put("col1",dateFormat.format(s.getBreakfast()));
        params.put("col2",timeFormat.format(s.getBreakfast()));
        params.put("col3", timeFormat.format(s.getLunch()));
        params.put("col4", timeFormat.format(s.getDinner()));
        params.put("col5", s.getDrugName());
        params.put("col6", getState(index/3));
        params.put("col7",timeFormat.format(s.getTimeB()));
        params.put("col8",timeFormat.format(s.getTimeL()));
        params.put("col9",timeFormat.format(s.getTimeD()));
        params.put("col10",String.valueOf(s.isStateB()));
        params.put("col11",String.valueOf(s.isStateL()));
        params.put("col12",String.valueOf(s.isStateD()));
        if(isConnected()){
            Toast.makeText(HintActivity.this,"something...",Toast.LENGTH_LONG);
            try {
                doPostAsyn(URI,FragmentHttp.getPostDataString(params));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(HintActivity.this,"無法取得網路,開啟網路後再試一次",Toast.LENGTH_LONG).show();
            checkItem.clear();
        }



    }

    private String getState(int table) {
        StringBuffer sb = new StringBuffer();
        sb.append(ScheduleStorage.getData(HintActivity.this,"check"+table*3));
        sb.append(ScheduleStorage.getData(HintActivity.this,"check"+(table*3+1)));
        sb.append(ScheduleStorage.getData(HintActivity.this,"check"+(table*3+2)));
        Log.d("HintActivity","getState:"+sb.toString());
        return sb.toString();
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    public void doPostAsyn(final String urlStr, final String params) throws Exception
    {
        final ProgressDialog dialog = new ProgressDialog(HintActivity.this, ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Please wait");
        dialog.setMessage("Uploading date...");
        dialog.show();
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = FragmentHttp.doPost(urlStr, params);
                    Log.d("POST", result);
                    sleep(500);
                    //執行完同步工作,handler送出一個msg來操作UI動作
                    msg=new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
                    dialog.dismiss();
                } catch (Exception e)
                {
                    dialog.dismiss();
                    msg = new Message();
                    msg.what = 2;
                    myHandler.sendMessage(msg);
                    e.printStackTrace();
                }

            }
        }.start();

    }
}
