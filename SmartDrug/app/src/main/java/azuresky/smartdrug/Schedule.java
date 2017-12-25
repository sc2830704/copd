package azuresky.smartdrug;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import azuresky.smartdrug.DAO.ScheduleDao;

/**
 * Created by User on 2015/11/18.
 */
public class Schedule implements Comparable<Schedule>{
    private Date breakfast,lunch,dinner,timeB,timeL,timeD;
    private String drug;
    private String description;
    private Date date;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private SimpleDateFormat dateDf = new SimpleDateFormat("MM/dd");
    private String state;
    private boolean stateB,stateL,stateD;
    private String usage;
    public Schedule(Date breakfast, Date lunch, Date dinner, String drug, String state,String usage) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.drug = drug;
        this.state = state;
        this.usage = usage;
        try {
            date = dateDf.parse(dateDf.format(breakfast));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeB = new Date(0);
        timeL = new Date(0);
        timeD = new Date(0);
        stateB=false;
        stateL=false;
        stateD=false;

    }
    public Schedule(Date breakfast, Date lunch, Date dinner, String drug, String state,Date timeB, Date timeL, Date timeD, boolean stateB,boolean stateL,boolean stateD,String usage){
        this(breakfast,lunch,dinner,drug,state,usage);
        this.timeB = timeB;
        this.timeL = timeL;
        this.timeD = timeD;
        this.stateB = stateB;
        this.stateL = stateL;
        this.stateD = stateD;
    }

    public String getUsage() {
        return usage;
    }
    public boolean isStateB() {
        return stateB;
    }

    public boolean isStateL() {
        return stateL;
    }

    public boolean isStateD() {
        return stateD;
    }

    public Date getTimeB() {
        return timeB;
    }

    public Date getTimeL() {
        return timeL;
    }

    public Date getTimeD() {
        return timeD;
    }

    public Date getLunch() {
        return lunch;
    }
    public Date getDinner() {
        return dinner;
    }

    public Date getBreakfast() {
        return breakfast;
    }


    public String getDrugName() {
        return drug;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public String getState(){
        return state;
    }

    public Date getDate(){
        return date;
    }
    @Override
    public String toString() {
        return "Schedule{" +
                "breakfast=" + sdf.format(breakfast) +
                ", lunch=" + sdf.format(lunch) +
                ", dinner=" + sdf.format(dinner) +
                ", drug='" + drug + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (breakfast != null ? !breakfast.equals(schedule.breakfast) : schedule.breakfast != null)
            return false;
        if (lunch != null ? !lunch.equals(schedule.lunch) : schedule.lunch != null) return false;
        if (dinner != null ? !dinner.equals(schedule.dinner) : schedule.dinner != null)
            return false;
        if (drug != null ? !drug.equals(schedule.drug) : schedule.drug != null) return false;
        return !(description != null ? !description.equals(schedule.description) : schedule.description != null);

    }

    @Override
    public int hashCode() {
        int result = breakfast != null ? breakfast.hashCode() : 0;
        result = 31 * result + (lunch != null ? lunch.hashCode() : 0);
        result = 31 * result + (dinner != null ? dinner.hashCode() : 0);
        result = 31 * result + (drug != null ? drug.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Schedule another) {
        if(date.after(another.date))
            return 1;
        else if(date.before(another.date))
            return -1;
        return 0;
    }
}
