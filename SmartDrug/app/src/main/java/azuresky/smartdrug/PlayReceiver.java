package azuresky.smartdrug;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import azuresky.smartdrug.DAO.ScheduleStorage;

/**
 * Created by User on 2015/12/5.
 */
public class PlayReceiver extends BroadcastReceiver{


    Context context;
    final int requestedCode = 0;


    //接收來自ScheduleDaoImplement所設定的Alarm
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context=context;
        //設定藥盒描述
        int index = intent.getExtras().getInt("box");
        int time = intent.getExtras().getInt("time");
        String[] data = ScheduleStorage.getData(context,"ff"+index).split("\\|");
        String[] dsc = data[4].split(",");   //拆解description
        //dsc[time] = "E";    //設定該時段的代號
        //data[4]=dsc[0]+"," + dsc[1]+"," + dsc[2];     //重新設定description
        //ScheduleStorage.setData(context, "ff" + index, data[0]+"|" + data[1]+"|"+ data[2]+"|" + data[3]+"|" + data[4]);

        //送出廣播給fragment
        Intent intentFragment = new Intent();
        intentFragment.setAction("com.smart");
        intentFragment.putExtra("NextSchedule",data[4]);
        //context.sendBroadcast(intent);
        Log.d("onReceive","");

        Intent nIntent = new Intent(context,MainActivity.class);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =PendingIntent.getActivity(context, requestedCode, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        myNotify(pendingIntent);
    }
    public void myNotify(PendingIntent pendingIntent)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(MainActivity.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setContentTitle("提醒")
                .setContentText("該服藥了")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Notification.COLOR_DEFAULT,300,1000)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();
        notification.flags |= Notification.FLAG_INSISTENT;
        notification.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6");
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        //notification.ledARGB = Notification.COLOR_DEFAULT;
        // notification.ledOnMS = 300;
        //notification.ledOffMS = 1000;
        //notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notificationManager.notify(1, notification);
    }
}
