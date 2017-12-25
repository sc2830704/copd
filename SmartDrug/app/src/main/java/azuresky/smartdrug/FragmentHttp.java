package azuresky.smartdrug;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import azuresky.smartdrug.DAO.ScheduleDao;
import azuresky.smartdrug.DAO.ScheduleDaoFactory;

/**
 * Created by User on 2015/12/17.
 */
public class FragmentHttp extends Fragment {


    private Button load;
    private TextView breakfast,lunch,dinner,statue1,statue2,statue3;
    private final String URI_search = "http://140.118.122.159/mitlab/search.php";
    private  String result = "";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
    //private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private ScheduleDao sdao;
    private Message msg = new Message();
    private Spinner mySpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private boolean isRefreshClick = false;
    private JSONObject jsonObject;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            if(isNetConnect()){
                downLoadDate();
                myHandler.removeCallbacks(this);
            }
            else
                myHandler.postDelayed(this,1000);
        }
    };
    enum message{
        LoadFinished(1),RequestDate(2);
        int i;
        message(int i) {
            this.i = i;
        }
        public int getInt(){
            return i;
        }
    }
    Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if(getParentFragment().getActivity()!=null)
                        Toast.makeText(getParentFragment().getActivity(), "取得當天資訊", Toast.LENGTH_SHORT).show();
                    Log.d("MYDATA","result:"+result.toString());
                    String[] data = result.split(",");
                    breakfast.setText(data[5]);
                    lunch.setText(data[6]);
                    dinner.setText(data[7]);
                    Log.d("MYDATA","data[4] "+data[4]);
                    setStatuesText(data[4]);

                    break;
                case 2:
                    if(getParentFragment().getActivity()!=null)
                        Toast.makeText(getParentFragment().getActivity(), "取得date列表", Toast.LENGTH_SHORT).show();
                    else
                        break;
                    final String[] date = result.split(",");
                    spinnerAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,date);

                    mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("table", "box_search");
                            params.put("date", date[position]);
                            try {
                                doPostAsyn(URI_search, getPostDataString(params), message.LoadFinished);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    mySpinner.setAdapter(spinnerAdapter);
                    /*Set<Date> dateSet =  new TreeSet<>();
                    for(String d:date){
                        try {
                            dateSet.add(dateFormat.parse(d));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }*/
                    break;
                case 3:
                    if(getParentFragment().getActivity()!=null)
                        Toast.makeText(getActivity(), "連線失敗，請確定你是否開啟網路", Toast.LENGTH_LONG).show();
                    String[] array = new String[]{"無"};

                    spinnerAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,new String[]{"無"});
                    break;
                case 4:
                    spinnerAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,new String[]{"無"});
                    Toast.makeText(getActivity(), "伺服器連線逾時，請稍後重試", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private void setStatuesText(String s) {
        StringBuffer sb = new StringBuffer();
        String[] statue = new String[3];
        Log.d("MYDATA","status: "+s);
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                sb.append(s.charAt(i * 3 + j));
            }
            Log.d("FragmentHTTP",sb.toString());
            switch (sb.toString()){
                case "000":
                    statue[i] = "良好";
                    break;
                case "001":
                    statue[i] = "身體虛弱";
                    break;
                case "010":
                    statue[i] = "頭痛";
                    break;
                case "011":
                    statue[i] = "身體虛弱、頭痛";
                    break;
                case "100":
                    statue[i] = "呼吸不順";
                    break;
                case "101":
                    statue[i] = "呼吸不順、身體虛弱";
                    break;
                case "110":
                    statue[i] = "呼吸不順、頭痛";
                    break;
                case "111":
                    statue[i] = "呼吸不順、頭痛、身體虛弱";
                    break;
            }
            sb.delete(0,sb.length());
        }
        statue1.setText(statue[0]);
        statue2.setText(statue[1]);
        statue3.setText(statue[2]);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdao = new ScheduleDaoFactory().createScheduleDao(getParentFragment().getActivity());

        if(isNetConnect()){
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    downLoadDate();
                }
            }, 3000);
        }
        else{
            Toast.makeText(getActivity(), "清單下載失敗，請確定你是否開啟網路", Toast.LENGTH_LONG).show();
            myHandler.post(myRunnable);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(myRunnable);
    }

    private void downLoadDate() {
        HashMap<String, String> params = new HashMap<>();
        params.put("searchcol", "date");
        try{
            doPostAsyn(URI_search,getPostDataString(params),message.RequestDate);
        }catch (Exception e){
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_http,container,false);
        breakfast = (TextView)view.findViewById(R.id.breakfast);
        lunch = (TextView)view.findViewById(R.id.lunch);
        dinner = (TextView)view.findViewById(R.id.dinner);
        statue1 = (TextView)view.findViewById(R.id.statue1);
        statue2 = (TextView)view.findViewById(R.id.statue2);
        statue3 = (TextView)view.findViewById(R.id.statue3);
        mySpinner = (Spinner)view.findViewById(R.id.mySpinner);

        load = (Button)view.findViewById(R.id.load);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadDate();
                isRefreshClick = true;
            }
        });

        return view;
    }
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //處以同步工作的方法
    public void doPostAsyn(final String urlStr, final String params, final message ms) throws Exception
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Please wait");
        dialog.setMessage("Uploading date...");
        if(isRefreshClick){
            dialog.show();
        }

        new Thread()
        {
            public void run()
            {
                try
                {
                    result = doPost(urlStr, params);
                    Log.d("POST", result);
                    sleep(500);
                    //執行完同步工作,handler送出一個msg來操作UI動作
                    msg=new Message();
                    msg.what = ms.getInt();
                    myHandler.sendMessage(msg);
                    dialog.dismiss();
                } catch (Exception e)
                {
                    dialog.dismiss();
                    msg = new Message();
                    if(e.getClass().equals(UnknownHostException.class))
                        msg.what = 3;
                    else //if(e.getClass().equals(SocketTimeoutException.class))
                        msg.what = 4;
                    myHandler.sendMessage(msg);
                    e.printStackTrace();
                }

            }
        }.start();

    }
    public static String doPost(String url, String param) throws IOException {
        PrintWriter printWriter = null;
        BufferedReader bufReader = null;
        String result="";
        try
        {
            //Log.d("PARAM",param);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            //--- 設定連線的屬性---
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);
            connection.setDoOutput(true);   //設定是否向http提出請求,預設為false
            connection.setDoInput(true);
            connection.setReadTimeout(6000);   //設定time-out時間
            connection.setConnectTimeout(6000); //設定time-out時間
            //--------------------
            if (param != null && !param.trim().equals(""))
            {
                // 透過PrintWriter處理資料流串接URLConnection獲取的輸出資料流，得到物件實體
                printWriter = new PrintWriter(connection.getOutputStream());
                // 將資料寫至輸出串流
                printWriter.print(param);
                // 強制寫出所有資料至串流中
                printWriter.flush();
            }
            // 透過BufferReader處理資料流串接InputStreamReader節點資料流讀取資料
            bufReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufReader.readLine()) != null)   //如果還有下一行就繼續讀取
            {
                result += line;
            }
        } catch (Exception e) {
            throw e;
        }
        // 關閉與釋放資源
        finally
        {
            try
            {
                if (printWriter != null)
                    printWriter.close();
                if (bufReader != null)
                    bufReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    public boolean isNetConnect(){
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}


