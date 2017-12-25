package azuresky.smartdrug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import azuresky.smartdrug.Adapters.ScheduleAdapter;
import azuresky.smartdrug.DAO.ScheduleDao;
import azuresky.smartdrug.DAO.ScheduleDaoFactory;
import azuresky.smartdrug.DAO.ScheduleStorage;
import azuresky.smartdrug.Exception.DaoException;

/**
 * Created by sky on 2016/3/18.
 */
public class FragmentSchedule extends Fragment {
    private Set<Schedule> schedules = new TreeSet<>();
    private Button addSchedule,setBox;
    private ScheduleDao sd;
    private Date dateB,dateL,dateD;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat dayFormat2 = new SimpleDateFormat("MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Schedule schedule;
    private Context context;
    private EditText dateText,breakfastText,lunchText,dinnerText,drugNameText;
    private int PickRegister = 0;
    private ListView listView;
    private View view;
    private Spinner spinner;
    private ArrayAdapter<String> lunchList;
    private String[] lunch = {"口服藥", "外用藥"};
    private String usage;
    private AlertDialog setDialog;
    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            myHandler.sendMessage(new Message());
            myHandler.postDelayed(this,500);

        }
    };
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //設定藥盒完成 收到Message
            if(CardService.isComplete){
                ScheduleStorage.setData(context, "mode", "0");
                setDialog.dismiss();
                CardService.isComplete = false;
                myHandler.removeCallbacks(runnable);
                Toast.makeText(context, "設定藥盒成功", Toast.LENGTH_LONG).show();
            }

            return false;
        }
    });
    //private static final String STATE = "W,W,W";  //W:Waitting,E:Expire,T:Is taken

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Handler", "" + CardService.isComplete);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        sd = new ScheduleDaoFactory().createScheduleDao(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addSchedule = (Button)view.findViewById(R.id.addSchedule);
        setBox =(Button)view.findViewById(R.id.setBox);
        listView = (ListView)view.findViewById(R.id.ListView);
        listView.setAdapter(
                new ScheduleAdapter(
                        context,
                        getList(),
                        R.layout.schedule_list,
                        new String[]{"date","title","time","btn1"},
                        new int[]{R.id.date,R.id.title,R.id.text1,R.id.remove}
                )
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMyDialog(position);
            }
        });


        ScheduleStorage.setData(context, "mode", "0");  //強制設定為讀取模式 避免翻轉而沒有切換回讀取模式
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog(-1);
            }
        });
        setBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NfcAdapter myNfcAdapter = NfcAdapter.getDefaultAdapter(context);
                if (!myNfcAdapter.isEnabled()) {
                    new AlertDialog.Builder(context)
                            .setTitle("NFC尚未開啟")
                            .setMessage("請開啟NFC後再重試一次")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    ScheduleStorage.setData(context, "mode", "1");
                    setDialog = new AlertDialog.Builder(context)
                            .setView(R.layout.schedule_nfc_dialog)
                            .setTitle("設定藥盒")
                            .setMessage("手機靠近藥盒，聽見嗶嗶聲即完成設定")
                            .setCancelable(false)
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ScheduleStorage.setData(context, "mode", "0");
                                    myHandler.removeCallbacks(runnable);
                                }
                            })
                            .show();
                    myHandler.post(runnable);

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup container,Bundle SavedInstantState)
    {
        Log.d("FragmentSchedule", "onCreateView");
        view = layoutInflater.inflate(R.layout.schedule_layout,container,false);
        return view;

    }
    //自訂dialog，當新增schedule時顯示自訂dialog畫面
    //設定dialog來自R.layout.schedule_dialog
    //透過ad取得layout中的物件實體，並設定listener
    private void showMyDialog(final int position) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setView(R.layout.schedule_dialog)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dateB = new Date(df.parse(dateText.getText().toString() +" "+ breakfastText.getText().toString()).getTime());
                            dateL = new Date(df.parse(dateText.getText().toString() +" "+ lunchText.getText().toString()).getTime());
                            dateD = new Date(df.parse(dateText.getText().toString() +" "+ dinnerText.getText().toString()).getTime());
                            String drug = drugNameText.getText().toString();
                            schedule = new Schedule(dateB,dateL,dateD,drug.equals("") ?"none":drug,"E,E,E",usage);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //Data process
                        //if position=-1 add a schedule to dao. Else, update data
                        if (position==-1){
                            try {
                                sd.add(schedule);
                            } catch (DaoException e) {
                                Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            try {
                                sd.update(position,schedule);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        //update UI
                        listView.setAdapter(new ScheduleAdapter(
                                context,
                                getList(),
                                R.layout.schedule_list,
                                new String[]{"date","title","time","btn1"},
                                new int[]{R.id.date,R.id.title,R.id.text1,R.id.remove}
                        ));
                    }
                })
                .show();
        Toolbar dialogToolBar = (Toolbar)ad.findViewById(R.id.dialog_toolbar);//透過dialog出使化ToolBar
        dialogToolBar.setTitle("設定時間");//設定標題
        //設定每個TextView選項點擊的事件
        dateText = (EditText)ad.findViewById(R.id.dateText);
        dateText.setOnFocusChangeListener(dateTextListener);
        breakfastText = (EditText)ad.findViewById(R.id.breakfast);
        breakfastText.setOnFocusChangeListener(dateTextListener);
        lunchText = (EditText)ad.findViewById(R.id.lunch);
        lunchText.setOnFocusChangeListener(dateTextListener);
        dinnerText = (EditText)ad.findViewById(R.id.dinner);
        dinnerText.setOnFocusChangeListener(dateTextListener);
        drugNameText = (EditText)ad.findViewById(R.id.drug_name);
        spinner = (Spinner)ad.findViewById(R.id.mySpinner);
        //設定spinner
        lunchList = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, lunch);
        spinner.setAdapter(lunchList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usage = lunch[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //初始化當天時間設定
        String today = dayFormat.format(Calendar.getInstance().getTime());
        Date breakfast = new Date(Calendar.getInstance().get(Calendar.DATE));
        Date lunch = new Date(Calendar.getInstance().get(Calendar.DATE));
        Date dinner = new Date(Calendar.getInstance().get(Calendar.DATE));
        if(position==-1){
            dateText.setText(today);
            breakfastText.setText(timeFormat.format(breakfast.getTime()));
            lunchText.setText(timeFormat.format(lunch.getTime()+4*60*60*1000L));
            dinnerText.setText(timeFormat.format(dinner.getTime()+10*60*60*1000L));
            drugNameText.setText("Penicillin");
        }else{
            schedule=sd.get(position);
            dateText.setText(dayFormat.format(schedule.getDinner()));
            breakfastText.setText(timeFormat.format(schedule.getBreakfast()));
            lunchText.setText(timeFormat.format(schedule.getLunch()));
            dinnerText.setText(timeFormat.format(schedule.getDinner()));
            drugNameText.setText(schedule.getDrugName());
        }


    }
    //選擇日期與時間的監聽器
    //改變EditText的focus狀態時,顯示選擇日期或時間的dialog
    private EditText.OnFocusChangeListener dateTextListener = new EditText.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.dateText:
                    if(hasFocus){
                        showDateDialog();
                    }
                    break;
                case R.id.breakfast:
                    if(hasFocus)
                    {
                        PickRegister = 0;
                        showTimeDialog();
                        breakfastText.clearFocus();
                    }
                    break;
                case R.id.lunch:
                    if(hasFocus)
                    {
                        PickRegister = 1;
                        showTimeDialog();
                        lunchText.clearFocus();
                    }
                    break;
                case R.id.dinner:
                    if(hasFocus)
                    {
                        PickRegister = 2;
                        showTimeDialog();
                        dinnerText.clearFocus();
                    }
                    break;
            }
        }
    };
    private void showDateDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,3,datePickListener,
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("選擇日期");
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
        dateText.clearFocus();
    }

    private void showTimeDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,3,timePickListener,
                now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),true);
        timePickerDialog.setTitle("選擇時間");
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateText.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);
            dateText.clearFocus();
        }
    };
    private TimePickerDialog.OnTimeSetListener timePickListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            switch (PickRegister)
            {
                case 0:
                    if(minute<10)
                        breakfastText.setText(hourOfDay+":"+"0"+minute);
                    else
                        breakfastText.setText(hourOfDay+":"+minute);
                    break;
                case 1:
                    if(minute<10)
                        lunchText.setText(hourOfDay+":"+"0"+minute);
                    else
                        lunchText.setText(hourOfDay+":"+minute);
                    break;
                case 2:
                    if(minute<10)
                        dinnerText.setText(hourOfDay+":"+"0"+minute);
                    else
                        dinnerText.setText(hourOfDay + ":" + minute);
                    break;
            }
        }
    };
    public ArrayList<HashMap<String,Object>> getList()
    {
        Log.d("fragmentschedule","getlist called");
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        schedules = sd.getAllSchedule();
        for(Schedule s:schedules){
            HashMap<String,Object> item  = new HashMap<>();
            item.put("date","日期"+dayFormat2.format(s.getBreakfast()));
            item.put("title","早         午         晚");
            item.put("time", timeFormat.format(s.getBreakfast()) + "  " + timeFormat.format(s.getLunch()) + "  " + timeFormat.format(s.getDinner()));
            item.put("btn1", "刪除");
            list.add(item);
            //Log.d("123", timeFormat.format(s.getBreakfast()) + "  " + timeFormat.format(s.getLunch()) + "  " + timeFormat.format(s.getDinner()));
        }
        return list;
    }
}
