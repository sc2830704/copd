package azuresky.smartdrug.DAO;

import java.text.ParseException;
import java.util.Set;

import azuresky.smartdrug.Exception.DaoException;
import azuresky.smartdrug.Schedule;

/**
 * Created by User on 2015/11/19.
 */
public interface ScheduleDao {
    void add(Schedule schedule) throws DaoException, ParseException;
    void update(int index,Schedule schedule) throws ParseException;
    void delete(int index);
    Schedule get(int position);
    Set<Schedule> getAllSchedule();
    String[] getScheduleArray();
}
