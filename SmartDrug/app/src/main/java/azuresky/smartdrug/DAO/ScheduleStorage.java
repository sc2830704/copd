package azuresky.smartdrug.DAO;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by User on 2015/11/20.
 */
//KEY:ff[index] VALUE:[date1]|[date2]|[date3]|[drug]|[description]
//KEY:count VALUE:[]


public class ScheduleStorage {
    private static SharedPreferences.Editor myEditor;
    private static SharedPreferences sharedPreferences;
    private static final Object Lock = new Object();
    public static final String DEFAULT_DATA = "0";
    public static void setData(Context context,String key,String value){
        synchronized(Lock) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            //sharedPreferences = context.getSharedPreferences("s1",Context.MODE_PRIVATE);
            myEditor = sharedPreferences.edit();
            myEditor.putString(key, value);
            myEditor.commit();
        }
    }
    public static String getData(Context context,String key)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        synchronized(Lock){
            return sharedPreferences.getString(key, DEFAULT_DATA);
        }
    }
    public static void remove(Context context,String key){
        synchronized(Lock){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            myEditor.remove(key);
            myEditor.commit();
        }
    }
}
