package com.ntust.mitlab.copdwalk.Model;

import android.util.Log;

import com.ntust.mitlab.copdwalk.MyApp;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by MITLAB on 2018/2/13.
 */

public class ExerciseHelper {
    public int workState;
    public String  workMessage, warnningMessage;
    public float sbpBefore, dbpBefore, sbpAfter, dbpAfter, hrBefore, spo2Before, hrAfeter, spo2After;
    public ArrayList<Float> sbps_60, dbps_60, spo2s_60, hrs_60, sbps_5s, dbps_5s, spo2s_5s, hrs_5s;
    public float avgSpo2, avgSbp, avgDbp, avgHR;
    public ExerciseHelper(){
        sbps_60 = new ArrayList<>();
        dbps_60 = new ArrayList<>();
        spo2s_60 = new ArrayList<>();
        hrs_60 = new ArrayList<>();
        sbps_5s = new ArrayList<>();
        dbps_5s = new ArrayList<>();
        spo2s_5s = new ArrayList<>();
        hrs_5s = new ArrayList<>();
        workState = 0;
    }
    public boolean isPreTestOk(){
        if(MyApp.isSPO2Disable)
            return true;
        if(sbps_60.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(dbps_60.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(spo2s_60.isEmpty()){
            workMessage = "血氧測量不完整";
            return false;
        }
        if(hrs_60.isEmpty()){
            workMessage = "心率測量不完整";
            return false;
        }
        workMessage = "前測完成";
        return true;
    }
    public boolean isAfterTestOk(){
        if(MyApp.isSPO2Disable)
            return true;
        if(sbps_60.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(dbps_60.isEmpty()){
            workMessage = "血壓測量不完整";
            return false;
        }
        if(spo2s_60.isEmpty()){
            workMessage = "血氧測量不完整";
            return false;
        }
        if(hrs_60.isEmpty()){
            workMessage = "心率測量不完整";
            return false;
        }
        workMessage = "恭喜您運動完成";
        return true;
    }
    public void calculateAverageIn60s(){
        avgSpo2=0; avgSbp=0; avgDbp=0; avgHR=0;
        for(float data:spo2s_60){
            avgSpo2 += data;
        }
        avgSpo2 = avgSpo2/spo2s_60.size();
        for(float data:sbps_60){
            avgSbp += data;
        }
        avgSbp = avgSbp/sbps_60.size();
        for(float data:dbps_60){
            avgDbp += data;
        }
        avgDbp = avgDbp/dbps_60.size();
        for(float data:hrs_60){
            avgHR += data;
        }
        avgHR = avgHR/hrs_60.size();
    }
    public boolean isOkToWork(){
        if(MyApp.isSPO2Disable){
            return true;
        }
        if(avgSpo2<92){
            workMessage = "指尖血氧飽和度(SpO2)已低於安全標準(92%)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }
        if(avgHR>100){
            workMessage = "心跳速率高於安全標準(>100次/分鐘)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }else if(avgHR<60){
            workMessage = "心跳速率低於安全標準(<60次/分鐘)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }
        if(avgSbp>140){
            workMessage = "血壓高於安全標準(收縮壓>140mmHg)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }else if(avgSbp<90){
            workMessage = "血壓低於安全標準(收縮壓<90mmHg)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }
        if(avgDbp>90){
            workMessage = "血壓高於安全標準(舒張壓>90mmHg)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }else if(avgDbp<60){
            workMessage = "血壓低於安全標準(舒張壓<60mmHg)";
            warnningMessage = "您休息狀態的"+workMessage+"，建議您目前不宜單獨外出活動；10分鐘後再次測量，待數值於安全標準並諮詢專業醫護人員再進行活動。";
            return false;
        }
        return true;
    }
    public void savePreTestData(){
        if(MyApp.isSPO2Disable){
                dbpBefore = 80;
                sbpBefore = 110;
                return;
        }
        sbpBefore = floatToSecnondDec(avgSbp);
        dbpBefore = floatToSecnondDec(avgDbp);
        hrBefore = floatToSecnondDec(avgHR);
        spo2Before = floatToSecnondDec(avgSpo2);
    }
    public void saveAfterTestData(){
        if(MyApp.isSPO2Disable){
                dbpAfter = 80;
                sbpAfter = 110;
                return;
        }
        sbpAfter = floatToSecnondDec(avgSbp);
        dbpAfter = floatToSecnondDec(avgDbp);
        hrAfeter = floatToSecnondDec(avgHR);
        spo2After = floatToSecnondDec(avgSpo2);
    }
    public void sample(){
        //統計spo2、hr、sbp、dbp數值
        if(!hrs_5s.isEmpty())
            hrs_60.add(getListAverage(hrs_5s));
        if(!spo2s_5s.isEmpty())
            spo2s_60.add(getListAverage(spo2s_5s));
        if(!dbps_5s.isEmpty())
            dbps_60.add(getListAverage(dbps_5s));
        if(!sbps_5s.isEmpty())
            sbps_60.add(getListAverage(sbps_5s));
        hrs_5s.clear();
        spo2s_5s.clear();
        dbps_5s.clear();
        sbps_5s.clear();
    }

    public float getListAverage(ArrayList<Float> list) {
        if(list.size()==0)
            return -1;
        float average=0;
        for(Float data:list){
            average += data;
        }
        average = average/list.size();
        String ave = String.format("%.2f",average);
        return Float.parseFloat(ave);
        //return average;

    }

    private float floatToSecnondDec(float value){
        return Float.parseFloat(String.format("%.2f",value));
    }
}
