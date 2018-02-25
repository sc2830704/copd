package com.ntust.mitlab.copdwalk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by User on 2015/11/20.
 */
//KEY:ff[index] VALUE:[date1]|[date2]|[date3]|[drug]|[description]
//KEY:count VALUE:[]


public class MyShared {
    private static SharedPreferences.Editor myEditor;
    private static SharedPreferences sharedPreferences;
    private static final Object Lock = new Object();
    public static final String DEFAULT_DATA = null;


    public static void setData(Context context, String key, String value){
        synchronized(Lock) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //sharedPreferences = context.getSharedPreferences("s1",Context.MODE_PRIVATE);
            myEditor = sharedPreferences.edit();
            myEditor.putString(key, value);
            myEditor.commit();
        }
    }
    public static String getData(Context context, String key)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        synchronized(Lock){
            String data = sharedPreferences.getString(key, DEFAULT_DATA);
            return data;
        }
    }
    public static String getData(Context context, String key, String defaultvalue)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        synchronized(Lock){
            String data = sharedPreferences.getString(key, defaultvalue);
            return data;
        }
    }
    public static <T> void setObject(Context context, String key, T obj){
        synchronized(Lock) {
            Gson gson = new Gson();
            String value = gson.toJson(obj);
            setData(context,key,value);
        }
    }
    public static void remove(Context context, String key){
        synchronized(Lock){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            myEditor = sharedPreferences.edit();
            myEditor.remove(key);
            myEditor.commit();
        }
    }
    public static Map<String, ?> getAll(Context context){
        synchronized(Lock){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Map<String, ?> allEntries = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            }
            return allEntries;

        }



    }


}
