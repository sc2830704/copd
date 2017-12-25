package azuresky.smartdrug;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import azuresky.smartdrug.Exception.HttpException;


/**
 * Created by User on 2015/12/17.
 */
public class FragmentENV extends Fragment {

    //String semdserverHOST="http://mitlab.no-ip.org:8080/test3.php";
    String getserverHOST="http://140.118.122.246/sk/mitlab/search.php";
    private LinearLayout llBarChart;
    private ProgressDialog pd;

    EditText editText;
    Button Data_pm2_5,drawChartButton,Data_temp,Data_Hum,Data_CO2;
    static String DataResult;
    String[] titles = new String[] { "CO2" ,"溫度" ,"濕度" ,"PM2.5" }; // 定義折線的名稱
    int[] colors = new int[] { Color.BLUE, Color.GREEN ,Color.YELLOW,Color.BLACK};// 折線的顏色
    List<double[]> xx = new ArrayList<double[]>(); // 點的x坐標
    List<double[]> yy = new ArrayList<double[]>(); // 點的y坐標


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.env_layout,container,false);
        llBarChart = (LinearLayout) view.findViewById(R.id.chart);
        editText=(EditText)view.findViewById(R.id.date);
        llBarChart.removeAllViews();


        drawChartButton = (Button) view.findViewById(R.id.post); //向資料庫 php http echo 資料
        Data_pm2_5 = (Button) view.findViewById(R.id.Data_pm2_5);
        Data_temp = (Button) view.findViewById(R.id.Data_temp); //溫度
        Data_CO2 = (Button) view.findViewById(R.id.Data_CO2);
        Data_Hum = (Button) view.findViewById(R.id.Data_Hum);  //濕度

        drawChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("table","sensordata_search");
                        params.put("date",editText.getText().toString());
                      // params.put("date","2016-01-18");
                         Log.d("POST", "post on Click");
                        try {
                            doPostAsyn(getserverHOST, getPostDataString(params));
                            Log.d("POST", "Try" + editText.getText().toString() );
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("POST", "StackTrace");
                        }
                        llBarChart.removeAllViews();
                        Data_Hum.setEnabled(true);
                        Data_CO2.setEnabled(true);
                        Data_temp.setEnabled(true);
                        Data_pm2_5.setEnabled(true);
                        // addchart();

                    }
                });
        // addchart_select (type,X軸最大值,X軸最小值,Y軸最大值,Y軸最小值)
        Data_pm2_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llBarChart.removeAllViews();
                if(DataResult == "") {
                    Toast.makeText(getActivity(), "無資料", Toast.LENGTH_LONG).show();
                }
                else
                    addchart_select(3, 23, 0, 300, 0); // 3 is PM2.5

            }
        });
        Data_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llBarChart.removeAllViews();
                if(DataResult == "") {
                    Toast.makeText(getActivity(), "無資料", Toast.LENGTH_LONG).show();
                }
                else
                addchart_select(1,23,0,80,-20); // 1 is Tempature
            }
        });
        Data_CO2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llBarChart.removeAllViews();
                if(DataResult == "") {
                    Toast.makeText(getActivity(), "無資料", Toast.LENGTH_LONG).show();
                }
                else
                addchart_select(0,23,0,3000,100); // 0 is CO2
            }
        });
        Data_Hum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBarChart.removeAllViews();
                if(DataResult == "") {
                    Toast.makeText(getActivity(), "無資料", Toast.LENGTH_LONG).show();
                }
                else
                    addchart_select(2,23,0,100,0); // 2 is Hum
            }
        });

        return view;
        }
    public void addchart_select(int type,int x_MAX,int x_MIN ,int y_MAX,int y_MIN){

        xx = new ArrayList<double[]>(); // 點的x坐標
        yy = new ArrayList<double[]>(); // 點的y坐標
        //欲切割的字串
        String splitString = DataResult;
        //使用「,」進行切割
        String[] names = splitString.split(",");
        Log.d("POST", "<names>=" + names.length);
        double[] dx = new double[24];
        double[] dy = new double[24];
        for (int i =0 ; i<24;i++)
        {
            dx[i]=-1;
            dy[i]=-1;
        }
        /*
        for(int i = type ; i<names.length;i=i+6) {
            dy[i] = Double.parseDouble(names[i]);
            dx[i] = Double.parseDouble( names[i+5-type].substring(11,13) )+ Double.parseDouble(names[i+5-type].substring(14, 16)) / 60 + Double.parseDouble(names[i+5-type].substring(17, 19)) / 3600;
        }
        */
        int hour = Integer.parseInt(names[5].substring(11, 13));
        int count = 0 ;
        double total_value = 0 ;
        //若有資料斷層，則此平均的演算法不適用
        for(int i = type ; i<names.length;i=i+6) {

            if (Double.parseDouble(names[i+5-type].substring(11,13)) == hour )
            {
                total_value += Double.parseDouble(names[i]);
                count++;
            }
            else
            {
                dy[hour]= total_value / count; //average
                dx[hour] = hour;

                hour++;
                total_value = Double.parseDouble(names[i]);
                count = 1;
            }
        }
        dy[hour] = total_value / count; //average
        dx[hour] = hour;
        // 數值X,Y坐標值輸入
        yy.add(dy);
        xx.add(dx);

        XYMultipleSeriesDataset dataset = buildDatset(titles, xx, yy,type); // 儲存座標值

       // int[] colors = new int[] { Color.BLUE };// 折線的顏色
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND }; // 折線點的形狀
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true , type);

        setChartSettings(renderer, "資料折線圖展示", "時間(小時)", "數值", x_MIN, x_MAX, y_MIN, y_MAX, Color.BLACK);// 定義折線圖
        final View chart = ChartFactory.getLineChartView(getActivity(), dataset, renderer);

        llBarChart.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    // 定義折線圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setChartTitle(title); // 折線圖名稱
        renderer.setChartTitleTextSize(80); // 折線圖名稱字形大小
        renderer.setLabelsTextSize(30);
        renderer.setAxisTitleTextSize(20);
        renderer.setXTitle(xTitle); // X軸名稱
        renderer.setYTitle(yTitle); // Y軸名稱
        renderer.setXAxisMin(xMin); // X軸顯示最小值
        renderer.setXAxisMax(xMax); // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK); // X軸線顏色
        renderer.setXLabels(23);//设置x轴显示12个点,根据setChartSettings的最大值和最小值自动计算点的间隔
        renderer.setYLabels(10);//设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间
        renderer.setYAxisMin(yMin); // Y軸顯示最小值
        renderer.setYAxisMax(yMax); // Y軸顯示最大值
        renderer.setAxesColor(axesColor); // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK); // Y軸線顏色
        renderer.setLabelsColor(Color.BLACK); // 設定標籤顏色
        renderer.setMarginsColor(Color.WHITE); // 設定背景顏色
        renderer.setMargins(new int[]{20, 30, 15, 0});//设置图表的外边框(上/左/下/右)
        renderer.setPanLimits(new double[]{-10, 20, -10, 40}); //设置拖动时X轴Y轴允许的最大值最小值.
        renderer.setZoomLimits(new double[]{-10, 20, -10, 40});//设置放大缩小时X轴Y轴允许的最大最小值.


        renderer.setShowGrid(true); // 設定格線
    }

    // 定義折線圖的格式
    private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill,int type) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        //int length = colors.length;
        int length = 1;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[type]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r); //將座標變成線加入圖中顯示
        }
        return renderer;
    }

    // 資料繪圖處理
    private XYMultipleSeriesDataset buildDatset(String[] titles, List<double[]> xValues,
                                                List<double[]> yValues,int type) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // int length = titles.length; // 折線數量
        int length = 1;// for this app  only one
        for (int i = 0; i < length; i++) {
            // XYseries對象,用於提供繪製的點集合的資料
            // XYSeries series = new XYSeries(titles[i]); // 依據每條線的名稱新增
            XYSeries series = new XYSeries(titles[type]); // 依據每條線的名稱新增

            double[] xV = xValues.get(i); // 獲取第i條線的資料
            double[] yV = yValues.get(i);
            int seriesLength = xV.length; // 有幾個點

            for (int k = 0; k < seriesLength; k++) // 每條線裡有幾個點
            {

                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
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

    public void doPostAsyn(final String urlStr, final String params) throws Exception
    {
        //Progress Dialog
        pd = ProgressDialog.show(getActivity(), "Loading...", "Please hold on", true, false);
        //
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = doPost(urlStr, params);
                    DataResult = result;
                    Log.d("POST", result);
                    pd.dismiss();
                } catch (HttpException e)
                {
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            };
        }.start();
    }
    public static String doPost(String url, String param) throws HttpException {
        PrintWriter printWriter = null;
        BufferedReader bufReader = null;
        String result = "";
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
            throw new HttpException("Connecting Server fail");
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
}


