package azuresky.smartdrug;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import azuresky.smartdrug.DAO.ScheduleDao;
import azuresky.smartdrug.DAO.ScheduleDaoFactory;
import azuresky.smartdrug.DAO.ScheduleStorage;

/**
 *
 * 提供給ViewPager的其中一個content
 */
public class FragmentPage extends Fragment{
    private static final String TITLE = "TITLE";
    private static final String RESID= "RESID";
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.TAIWAN);
    private SimpleDateFormat tsdf = new SimpleDateFormat("E HH:mm:ss",Locale.TAIWAN);
    private ScheduleDao sdao;
    private TextView time,nextSchedule,previous,count,left;
    private Date date,lastTime;
    private String dateText,missedTimeText,leftTimeText;
    //handler用來處理UI操作
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Context context = getParentFragment().getActivity();
            date = getNextTime(); //取得下一次服藥時間
            lastTime = new Date(Long.parseLong(ScheduleStorage.getData(context,"last")));
            if(date!=null){
                dateText = getDateInfo(date)+" "+sdf.format(date);
                missedTimeText = ""+getMissed();
                leftTimeText = getLeftTime(date,lastTime);
                left.setTextColor(Color.RED);
                left.setText(leftTimeText);
                nextSchedule.setText(dateText);
            }
            else
                nextSchedule.setText("尚未有行程");
            //上次服藥時間
            if(!ScheduleStorage.getData(context, "p").equals(ScheduleStorage.DEFAULT_DATA))
                previous.setText(ScheduleStorage.getData(getParentFragment().getActivity(),"p"));
            else
                previous.setText("無");
            count.setText(missedTimeText);
            time.setText(tsdf.format(Calendar.getInstance().getTime()));
            return false;
        }

    });

    private String getLeftTime(Date date,Date lastTime) {
        Date now = new Date();
        long currentMills = now.getTime();
        if(lastTime.getTime()<currentMills-60*1000)
            return leftTimeFormat(date.getTime()-now.getTime());
        else
            return "時間到,請準時服用藥物";

    }

    private String leftTimeFormat(long millSeconds) {

        long seconds = millSeconds/1000;
        int minutes = (int) (seconds/60%60);
        int hours = (int) (seconds/60/60%24);
        int days = (int) (seconds/60/60/24);
        String leftTime = "";
        if(days>0)
            leftTime+=""+days+"天";
        leftTime+=hours+"小時";
        leftTime+=minutes+"分";
        leftTime+=seconds%60+"秒後";
        return leftTime;


    }

    //執行時間的runnable
    private Runnable timeRunnable = new Runnable(){
        @Override
        public void run() {
            Message msg = new Message();
            msg.what=1;
            myHandler.sendMessage(msg);
            myHandler.postDelayed(this,1000);
        }
    };
    public static final FragmentPage newInstance(String title, int resId){
        FragmentPage fg = new FragmentPage();
        Bundle bd = new Bundle();
        bd.putString(TITLE,title);
        bd.putInt(RESID, resId);
        fg.setArguments(bd);
        return  fg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdao = new ScheduleDaoFactory().createScheduleDao(getParentFragment().getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentpage,container,false);
        myHandler.post(timeRunnable);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        time = (TextView)view.findViewById(R.id.time);
        previous = (TextView)view.findViewById(R.id.previous);
        nextSchedule = (TextView)view.findViewById(R.id.nextSchedule);
        count = (TextView)view.findViewById(R.id.count);
        left = (TextView)view.findViewById(R.id.left);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(timeRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private Date getNextTime() {
        Date date=null;
        Date now = new Date();
        Set<Schedule> schedules;
        schedules = sdao.getAllSchedule();
        for(Schedule s:schedules){
            if(s.getBreakfast().after(now)){
                date = s.getBreakfast();
                break;
            }
            else if(s.getLunch().after(now)){
                date = s.getLunch();
                break;
            }
            else if(s.getDinner().after(now)) {
                date = s.getDinner();
                break;
            }


        }
        return date;
    }

    private String getDateInfo(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if(today.get(Calendar.DATE)==c.get(Calendar.DATE))
            return "今天";
        else if(today.get(Calendar.DATE)==c.get(Calendar.DATE)-1)
            return "明天";
        else
            return c.get(Calendar.MONTH)+"/"+c.get(Calendar.DATE);
    }

    private int getMissed(){
        int count=0;
        Set<Schedule> schedules = sdao.getAllSchedule();
        for(Schedule schedule:schedules){
            if(schedule.getBreakfast().getTime() < (new Date().getTime()-1000*60*60))
                count++;
            if(schedule.getLunch().getTime() < (new Date().getTime()-1000*60*60))
                count++;
            if(schedule.getDinner().getTime() < (new Date().getTime()-1000*60*60))
                count++;
        }

        return count;
    }


}
