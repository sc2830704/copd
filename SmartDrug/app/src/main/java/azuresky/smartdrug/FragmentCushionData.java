package azuresky.smartdrug;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/1/19.
 */
public class FragmentCushionData extends Fragment {

    private final String URI_search = "http://140.118.122.159/sk/mitlab/search.php";
    private Button send;
    private TextView data;
    private String result;
    private View view;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            data.setText(result);
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        String data=null;
        //data= savedInstanceState.getString("data");
        if(!(data==null)){
            this.data.setText(data);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cushion_data_layout,container,false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = (TextView)view.findViewById(R.id.data);
        send = (Button)view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("table", "cushion_search");
                params.put("date", "2016/5/4");
                try {
                    doPostAsyn(URI_search, getPostDataString(params), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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

    //處裡同步工作的方法
    public void doPostAsyn(final String urlStr, final String params, final int in) throws Exception
    {
        final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
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
                    result = doPost(urlStr, params);
                    Log.d("POST", result);
                    sleep(500);
                    //執行完同步工作,handler送出一個msg來操作UI動作
                    Message msg = new Message();
                    msg.what = in;
                    myHandler.sendMessage(msg);
                    dialog.dismiss();
                } catch (Exception e)
                {
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
            connection.setReadTimeout(10000);   //設定time-out時間
            connection.setConnectTimeout(10000); //設定time-out時間
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!data.getText().equals("")){
            outState.putString("data",data.getText().toString());

        }

    }

}
