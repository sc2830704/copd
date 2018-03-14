package com.ntust.mitlab.copdwalk.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntust.mitlab.copdwalk.BuildConfig;
import com.ntust.mitlab.copdwalk.StepService.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import static com.ntust.mitlab.copdwalk.util.Util.DF_Date;

/**
 * Created by mitlab_raymond on 2017/11/28.
 */

public class DBHelper extends SQLiteOpenHelper {
    public final static String DB_NAME = "copd";
    public final static String TABLE_PDO = "pdo";
    public final static String TABLE_ACTIVITY = "activity";
    public final static String TABLE_DAILY = "daily";
    private final  String TAG = "DBHelper";
    private static DBHelper instance;
    private static final AtomicInteger openCounter = new AtomicInteger();
    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }
    public static synchronized DBHelper getInstance(final Context c) {
        if (instance == null) {
            instance = new DBHelper(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("TAG","OnCreate");
        sqLiteDatabase.execSQL("CREATE  TABLE "+TABLE_ACTIVITY+
                " (date INTEGER," +
                "steps INTEGER," +
                "bp TEXT," +
                "data INTEGER," +
                "start_time INTEGER," +
                "end_time INTEGER," +
                "distance INTEGER," +
                "h_i_time INTEGER," +
                "isSync INTEGER)"
        );
        sqLiteDatabase.execSQL("CREATE  TABLE "+TABLE_PDO+
                " (date INTEGER," +
                "steps INTEGER)");

        sqLiteDatabase.execSQL("CREATE  TABLE "+TABLE_DAILY+
                " (date INTEGER," +
                " h_i_time INTEGER," +
                " distance INTEGER," +
                " step INTEGER," +
                "sync_flag INTEGER)");
        Log.d("TAG","OnCreateEND");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"onUpgrade");
        if (newVersion > oldVersion) {
            db.beginTransaction();//建立交易
            boolean success = false;//判斷參數
            //由之前不用的版本，可做不同的動作
            switch (oldVersion) {
                case 1:
                    db.execSQL("CREATE  TABLE "+TABLE_ACTIVITY+
                            " (date INTEGER," +
                            "steps INTEGER," +
                            "bp TEXT," +
                            "data TEXT," +
                            "start_time INTEGER," +
                            "end_time INTEGER," +
                            "distance INTEGER," +
                            "h_i_time INTEGER," +
                            "isSync INTEGER)");
                    oldVersion++;
                    success = true;
                    break;
            }

            if (success) {
                Log.d(TAG,"CREATE NEW TABLE");
                db.setTransactionSuccessful();//正確交易才成功
            }
            db.endTransaction();
        }
        else {
            onCreate(db);
        }
    }
    //取得一段時間內的步數總和
    public int getDailySteps(Long start, Long end){
        Cursor c = getReadableDatabase().query(
                TABLE_DAILY,
                new String[]{"SUM(step)"},
                "date >= ? AND date <= ?",
                new String[]{String.valueOf(start), String.valueOf(end)},
                null,
                null,
                null);
//改為輸出json 包含高強度運動時間
//        JSONArray jsonArray = new JSONArray();
//        while(c.moveToNext()){
//            JSONObject jobj = new JSONObject();
//            try {
//                jobj.put("steps", c.getInt(0));
//                jobj.put("distance",c.getInt(1));
//                jobj.put("h_i_time",c.getInt(2));
//                jobj.put("start_time",c.getLong(3));
//                jobj.put("end_time",c.getLong(4));
//                jobj.put("bp",c.getString(5));
//                jobj.put("data",c.getString(6));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            jsonArray.put(jobj);
//
//        }
//        c.close();
//        return jsonArray.toString();


        int totalSteps;
        if(c.getCount() == 0)
            totalSteps = 0;
        else{
            c.moveToFirst();
            totalSteps = c.getInt(0);
        }
        c.close();
        return totalSteps;
    }
    //儲存每日的步數，每天一筆
    public boolean saveDaily(long date, int step, int h_i_time, int distance){
        boolean flag = false;
        //檢查同一天是否已經有資料，已經存在傳回false
        Cursor c = getReadableDatabase().query(TABLE_DAILY,
                new String[]{"date","step","h_i_time","distance","sync_flag"},
                "date="+date,
                null,
                null,
                null,
                "date DESC");
        if(c.moveToNext()){
            Log.d(TAG, "該日期已存在, date: "+DF_Date.format(date));
            return flag;
        }
        //不存在則新增
        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("step",step);
        values.put("h_i_time",h_i_time);
        values.put("distance",distance);
        values.put("sync_flag", 0 );
        try {
            getWritableDatabase().insert(TABLE_DAILY,null,values);
            flag = true;
        }finally {
            c.close();
        }
        return flag;
    }
    //修改每日同步的flag
    public void updateDaily(long date, int sync_flag) {
        ContentValues values = new ContentValues();
        getWritableDatabase().beginTransaction();
        values.put("sync_flag", sync_flag);
        getWritableDatabase().update(TABLE_DAILY, values, "date ="+ date, null);
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
    }
    //取得未同步到雲端的步數(只會取得昨天之前的:date < Util.getToday())
    public String getUnsyncDaily(){
        Cursor c = getReadableDatabase().query(
                TABLE_DAILY,
                new String[]{"date","step","h_i_time","distance","sync_flag"},
                "sync_flag=0 AND date<"+Util.getToday(),
                null,
                null,
                null,
                "date DESC");
        JSONArray jsonArray = new JSONArray();
        while(c.moveToNext()){
            JSONObject jobj = new JSONObject();
            try {
                jobj.put("date", c.getLong(0));
                jobj.put("step",c.getInt(1));
                jobj.put("h_i_time",c.getInt(2));
                jobj.put("distance",c.getLong(3));
                jobj.put("sync_flag",c.getInt(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jobj);

        }
        c.close();
        return jsonArray.toString();
    }

    public boolean saveActivity(long date, int steps, String bp, String data, int h_i_time, int distance, long start_time, long end_time){
        boolean flag = false;
        getWritableDatabase().beginTransaction();
        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("steps",steps);
        values.put("bp",bp);
        values.put("data",data);
        values.put("h_i_time",h_i_time);
        values.put("distance",distance);
        values.put("start_time",start_time);
        values.put("end_time",end_time);
        try {
            getWritableDatabase().insert(TABLE_ACTIVITY,null,values);
            getWritableDatabase().setTransactionSuccessful();
            flag = true;
        }finally {
            getWritableDatabase().endTransaction();
        }
        return flag;
    }
    public String getAllActivity(){
        Cursor c = getReadableDatabase().query(
                TABLE_ACTIVITY,
                new String[]{"steps","distance","h_i_time","start_time","end_time","bp","data"},
                null,
                null,
                null,
                null,
                "start_time DESC");
        JSONArray jsonArray = new JSONArray();
        while(c.moveToNext()){
            JSONObject jobj = new JSONObject();
            try {
                jobj.put("steps", c.getInt(0));
                jobj.put("distance",c.getInt(1));
                jobj.put("h_i_time",c.getInt(2));
                jobj.put("start_time",c.getLong(3));
                jobj.put("end_time",c.getLong(4));
                jobj.put("bp",c.getString(5));
                jobj.put("data",c.getString(6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jobj);

        }
        c.close();
        return jsonArray.toString();
    }
    public String getActivity(long start,long end){
        Cursor c = getReadableDatabase().query(
                TABLE_ACTIVITY,
                new String[]{"steps","bp","data","distance","h_i_time","start_time","end_time"},
                "date >= ? AND date <= ?",
                new String[]{String.valueOf(start),String.valueOf(end)},
                null,
                null,
                null);
        JSONArray jsonArray = new JSONArray();
        while(c.moveToNext()){
            JSONObject jobj = new JSONObject();
            try {
                jobj.put("steps", c.getInt(0));
                jobj.put("bp",c.getString(1));
                jobj.put("data",c.getString(2));
                jobj.put("distance",c.getInt(3));
                jobj.put("h_i_time",c.getInt(4));
                jobj.put("start_time",c.getLong(5));
                jobj.put("end_time",c.getLong(6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG,""+c.getInt(4));
            jsonArray.put(jobj);

        }
        c.close();
        return jsonArray.toString();
    }
    public void saveCurrentSteps(int steps) {
        ContentValues values = new ContentValues();
        values.put("steps", steps);
        if (getWritableDatabase().update(TABLE_PDO, values, "date = -1", null) == 0) {
            values.put("date", -1);
            getWritableDatabase().insert(TABLE_PDO, null, values);
        }
    }

    //記錄每分鐘的步數
    public boolean saveSteps(long date, int steps){
        getWritableDatabase().beginTransaction();
        boolean newEntryCreated = false;
        try {
            ContentValues values = new ContentValues();
            values.put("steps", steps);
            values.put("date", date);
            getWritableDatabase().insert(TABLE_PDO, null, values);
            newEntryCreated = true;
            getWritableDatabase().setTransactionSuccessful();
        }catch (Exception ex) {
            newEntryCreated = false;
        }finally
        {
            getWritableDatabase().endTransaction();
        }
        return newEntryCreated;
    }
    public int getSteps(final long date){
        Cursor c = getReadableDatabase().query(TABLE_PDO, new String[]{"steps"}, "date = ?",
                new String[]{String.valueOf(date)}, null, null, null);
        c.moveToFirst();
        int re;
        if (c.getCount() == 0) re = Integer.MIN_VALUE;
        else re = c.getInt(0);
        c.close();
        return re;
    }

    public int getSteps(long start, long end){
        Cursor c = getReadableDatabase()
                .query(TABLE_PDO, new String[]{"SUM(steps)"}, "date >= ? AND date <= ?",
                        new String[]{String.valueOf(start), String.valueOf(end)}, null, null, null);
        int re;
        if (c.getCount() == 0) {
            re = 0;
        } else {
            c.moveToFirst();
            re = c.getInt(0);
        }
        c.close();
        return re;
    }
    public int getHItime(long start, long end){
        Cursor c = getReadableDatabase()
                .query(TABLE_PDO, new String[]{"COUNT(steps)"}, "date >= ? AND date <= ? AND steps >= ?",
                        new String[]{String.valueOf(start), String.valueOf(end), "100"}, null, null, null);
        int re;
        if (c.getCount() == 0) {
            re = 0;
        } else {
            c.moveToFirst();
            re = c.getInt(0);
        }
        c.close();
        return re;
    }
    public String getAll(){
        Cursor c = getReadableDatabase().query(TABLE_PDO, null, null, null, null, null, null);
        String results="";
        while(c.moveToNext()){
            String row = c.getColumnName(0)+":"+c.getLong(0) +","+ c.getColumnName(1)+":"+c.getLong(1);
            results+=row+"\n";
        }
        c.close();
        return results;
    }
    //get steps since boot
    public int getCurrentSteps() {
        int re = getSteps(-1);
        return re == Integer.MIN_VALUE ? 0 : re;
    }
    public void addToLastEntry(int steps) {
        if (steps > 0) {
            getWritableDatabase().execSQL("UPDATE " + TABLE_PDO + " SET steps = steps + " + steps +
                    " WHERE date = (SELECT MAX(date) FROM " + TABLE_PDO + ")");
        }
    }
    public void clearTable(){
        getWritableDatabase().execSQL("delete from "+ TABLE_ACTIVITY);
        getWritableDatabase().execSQL("delete from "+ TABLE_PDO);
        getWritableDatabase().execSQL("delete from "+ TABLE_DAILY);
    }
    public void clearTable(String table){

    }
}
