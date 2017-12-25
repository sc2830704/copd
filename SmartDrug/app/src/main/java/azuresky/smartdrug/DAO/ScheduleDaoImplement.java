package azuresky.smartdrug.DAO;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import azuresky.smartdrug.Exception.DaoException;
import azuresky.smartdrug.PlayReceiver;
import azuresky.smartdrug.Schedule;


/**
 * Created by User on 2015/11/19.
 */
public class ScheduleDaoImplement implements ScheduleDao {

    public static Set<Schedule> schedules = new TreeSet<>();
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String scheduleName="";
    private String scheduleData="";
    public static String messageToArduino;
    public ScheduleDaoImplement(Context context){
        this.context=context;
    }
    @Override
    public void add(Schedule schedule) throws DaoException, ParseException {
        sync();
        if(schedules.size()<3){
            if(schedules.contains(schedule))
                throw new DaoException("設定失敗，請勿設定相同日期");
            else
                schedules.add(schedule);
            //Log.d("ScheduleDao", "add successfully, ScheduleSize: " + schedules.size());
            commit();
            Toast.makeText(context,"新增成功!",Toast.LENGTH_SHORT).show();
        }
        else
            throw new DaoException("設定失敗，時間排程預設上限為三天!");
        if(schedules.size()==3)
            Toast.makeText(context,"設定完成後，請將手機靠近藥盒完成藥盒設定", Toast.LENGTH_LONG).show();

    }

    @Override
    public Schedule get(int position) {

        Iterator<Schedule> it = schedules.iterator();
        Schedule s = null;
        while (position-->=0)
            s = it.next();
        if(s!=null && schedules.contains(s))
            return s;
        return null;
    }

    @Override
    public void update(int index,Schedule schedule) throws ParseException {
        sync();
        Iterator<Schedule> it = schedules.iterator();
        Schedule s = null;
        while (index-->=0)  //取得修改的schedule物件實體
            s = it.next();

        if(s!=null && s.getDate().equals(schedule.getDate())) {     //修改行程,如果要修改的schedule與傳入的schedule日期相同則直接寫入新的
            schedules.remove(s);
            schedules.add(schedule);
        }
        else if(schedules.add(schedule) &&  schedules.contains(s))     //修改行程,如果要修改的schedule與傳入的schedule日期不同，則新增成功，並移除舊的schedule
            schedules.remove(s);
        else
            Toast.makeText(context,"修改失敗，該日期已有行程", Toast.LENGTH_LONG).show();


        commit();
    }

    @Override
    public void delete(int index) {
        try {
            sync();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //移除alarm
        removeAlarm(index);
        Iterator<Schedule> i = schedules.iterator();
        Schedule drop = null;
        for(int c=0; c<index; c++){
            drop = i.next();
        }
        if(schedules.contains(drop)){
            schedules.remove(drop);
            Log.d("ScheduleDao", "remove successfully, ScheduleSize: " + schedules.size());
            commit();
        }
        else
            Log.d("ScheduleDao", "remove failed! " + index + " not exists");
    }


    @Override
    public Set<Schedule> getAllSchedule() {
        try {
            sync();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    @Override
    public String[] getScheduleArray(){
        String[] s = new String[schedules.size()];
        Iterator<Schedule> it = schedules.iterator();
        for(int i=0;i<schedules.size();i++){
            if(it.hasNext()) {
                Schedule myi = it.next();
                s[i] = String.format("%s|%s|%s|%s|%s", sdf.format(myi.getBreakfast()), sdf.format(myi.getLunch()),
                        sdf.format(myi.getDinner()), myi.getDrugName(), myi.getDescription());
            }
        }
        return s;
    }
    public void commit(){
        int index=0;    //計算有幾筆資料
        //上傳前先清空所有資料
        ScheduleStorage.setData(context, "ff1","0");
        ScheduleStorage.setData(context, "ff2","0");
        ScheduleStorage.setData(context, "ff3","0");
        for(Schedule s:schedules){
            index++;
            scheduleData = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%b|%b|%b|%s",
                    sdf.format(s.getBreakfast()),
                    sdf.format(s.getLunch()),
                    sdf.format(s.getDinner()),
                    s.getDrugName(),
                    s.getState(),
                    sdf.format(s.getTimeB()),
                    sdf.format(s.getTimeL()),
                    sdf.format(s.getTimeD()),
                    s.isStateB(),s.isStateL(),s.isStateD(),s.getUsage());
            scheduleName = "ff"+index;  //根據資料順序賦予檔名
            ScheduleStorage.setData(context, scheduleName, scheduleData);
            //Log.d("ScheduleDao", "Name: " + scheduleName);
            //Log.d("ScheduleDao data", scheduleData);
            setAlarm(index,s.getBreakfast().getTime(), s.getLunch().getTime(), s.getDinner().getTime());

        }
        //紀錄資料數目
        ScheduleStorage.setData(context, "Count", String.valueOf(index));


    }
    public void sync() throws ParseException {
        int count = Integer.parseInt(ScheduleStorage.getData(context, "Count"));
        //Log.d("ScheduleDao", "schedule count:" + count);
        //同步前先清空local資料
        schedules.clear();
        messageToArduino = "";
        for(int i=1;i<=count;i++)
        {
            scheduleName = "ff"+i;
            String[] data = ScheduleStorage.getData(context,scheduleName).split("\\|");
            //Log.d("ScheduleDao",ScheduleStorage.getData(context, scheduleName));
            Date breakfast = sdf.parse(data[0]);
            Date lunch = sdf.parse(data[1]);
            Date dinner = sdf.parse(data[2]);
            String drug = data[3];
            String state = data[4];
            Date dateB = sdf.parse(data[5]);
            Date dateL = sdf.parse(data[6]);
            Date dateD = sdf.parse(data[7]);
            boolean stateB = Boolean.parseBoolean(data[8]);
            boolean stateL = Boolean.parseBoolean(data[9]);
            boolean stateD = Boolean.parseBoolean(data[10]);
            String usage = data[11];
            Schedule s = new Schedule(breakfast, lunch, dinner, drug, state, dateB, dateL, dateD, stateB, stateL, stateD, usage);
            schedules.add(s);

            messageToArduino += String.format("%s|%s|%s|%s|%s|%s|,",scheduleName, breakfast, lunch, dinner, drug ,state);
        }
    }


    private void setAlarm(int index, long time1, long time2, long time3) {

        long[] time={time1,time2,time3};
        Intent intent = new Intent(context,PlayReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt("box",index);
        Log.d("index",index+"");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        for(int i=0; i<3;i++){
            bundle.putInt("time",i);
            intent.putExtras(bundle);
            int requestCode = index * index -1 + i;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            if(time[i]>new Date().getTime())    //如果時間還沒過才設定鬧鐘
                alarmManager.set(AlarmManager.RTC_WAKEUP,time[i],pendingIntent);
        }
    }
    private void removeAlarm(int index) {
        Intent intent = new Intent(context,PlayReceiver.class);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for(int i=0; i<3;i++){
            int requestCode = index * index -1 + i;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

    }
}