package azuresky.smartdrug.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import azuresky.smartdrug.DAO.ScheduleDao;
import azuresky.smartdrug.DAO.ScheduleDaoFactory;
import azuresky.smartdrug.R;

/**
 * Created by User on 2015/11/29.
 */
public class ScheduleAdapter extends SimpleAdapter{
    private ScheduleDaoFactory sdf = new ScheduleDaoFactory();
    private ArrayList<HashMap<String ,Object>> list;
    private ScheduleDao sd;
    private Context context;
    public ScheduleAdapter(Context context, ArrayList<HashMap<String ,Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        list = data;    //list指向data物件，透過修改list就可以修該data
        this.context = context;
        sd = sdf.createScheduleDao(context);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        //LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(resource, parent, false);
        Log.d("adapter", "" + position);

        final Button btn1 = (Button)view.findViewById(R.id.remove);
        btn1.setTag(position);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int) v.getTag()) {
                    case 0:
                        list.remove(0);
                        sd.delete(1);
                        break;
                    case 1:
                        list.remove(1);
                        sd.delete(2);
                        break;
                    case 2:
                        list.remove(2);
                        sd.delete(3);
                        break;
                }
                //通知adapter資料改變以更新畫面
                notifyDataSetChanged();
            }
        });


        return view;
    }

}
