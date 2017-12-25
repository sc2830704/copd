package azuresky.smartdrug.DAO;

import android.content.Context;

/**
 * Created by User on 2015/11/20.
 */
public class ScheduleDaoFactory {
    public ScheduleDao createScheduleDao(Context context)
    {
        return new ScheduleDaoImplement(context);
    }

}
