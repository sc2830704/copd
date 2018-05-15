package com.ntust.mitlab.copdwalk.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class ApManager {

    public static boolean isApOn(Context context) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);

        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
//            if(isApOn(context)) {
//                wifimanager.setWifiEnabled(false);
//            }
            setWiFiEnable(context, false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void setWiFiEnable(Context context, boolean status){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            Log.d("APManager", "關閉wifi...");
            wifiManager.setWifiEnabled(status);
        }else{
            Log.d("APManager", "wifi已經關閉");
        }


    }
    public static String getApID(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(context.WIFI_SERVICE);
        Method getConfigMethod;
        String ssid="";
        try {
            getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
            ssid = wifiConfig.SSID;
            Log.d("APManager","ssid="+ssid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return ssid;
    }
    public static String getKey(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        Method getConfigMethod ;
        String key="";
        try {
            getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
            key = wifiConfig.preSharedKey;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return key;
    }
}
