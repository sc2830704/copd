package azuresky.smartdrug.util;

/**
 * Created by User on 2016/1/19.
 */
/*
public class HttpSearch {

    public static String search(URI uri,String params,){
        String result = "";
        try {
            doPostAsyn(uri,getPostDataString(params), message.PostRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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
    public void doPostAsyn(final String urlStr, final String params, final message message) throws Exception
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
                    msg.what = message.getInt();
                    myHandler.sendMessage(msg);
                    dialog.dismiss();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }.start();

    }//處以同步工作的方法
    public static void doPostAsyn(final String urlStr, final String params, final message message) throws Exception
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
                    msg.what = message.getInt();
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
            connection.setRequestProperty("accept", "* /*");
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


}*/
