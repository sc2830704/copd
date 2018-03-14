package com.ntust.mitlab.copdwalk.util;

import android.os.AsyncTask;
import android.util.Log;

import com.ntust.mitlab.copdwalk.Callback.AsyncResponse;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpTask extends AsyncTask<Void,Void,String> {
    private String method,endpoint;
    private JSONObject jobj;
    private int status;
    private String url = "http://140.118.122.241/copd/apiv1";
    private final String TAG = "HttpTask";
    public AsyncResponse asyncResponse = null;
    public HttpTask(String method, JSONObject jObj, String endpoint,String id){
        this.method = method;
        this.jobj = jObj;
        if(id!=null)
            url += endpoint+"/"+id;
        else
            url += endpoint;
        this.endpoint = endpoint;
    }
    @Override
    protected String doInBackground(Void... params) {
        URL url = null;
        try {
            url = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try{
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //--- 設定連線的屬性---
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            if(method.equals("POST")||method.equals("PUT")){
                connection.setDoOutput(true);   //設定是否向http提出請求,預設為false
                connection.setDoInput(true);
            }
            connection.setUseCaches(false);
            //--------------------
            connection.setReadTimeout(10000);   //設定time-out時間
            connection.setConnectTimeout(10000); //設定time-out時間
            if(jobj!=null){
                try(OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream())){
                    wr.write(jobj.toString());
                    Log.d(TAG,"JOBJ Length:"+jobj.toString());
                }
            }
            status = connection.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
                case 404:
                    return "api not found";
                case 500:
                    return "error";
                case 601:
                    return "No data avaliable";
                case 602:
                    return "account exist";
                case 603:
                    return "missing require column";
                case 604:
                    return "auth fail";
            }
            return null;
        }catch (Exception ex){
            if(ex.getMessage().equals("connect timed out")){
                status = 500;
                return "time out";
            }

            Log.d("HttpRequest","msg:"+ex.getMessage());
        }
        return null;
    }
    @Override
    protected void onPostExecute(String res) {
        asyncResponse.processFinish(status, res, endpoint);
        //super.onPostExecute(s);
    }
    public void setCallback(AsyncResponse callback) {
        asyncResponse = callback;
    }
}
