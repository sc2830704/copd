package azuresky.smartdrug;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import azuresky.smartdrug.DAO.ScheduleStorage;
/**
 * Created by User on 2015/11/13.
 */
public class CardService extends HostApduService {

    private static final String TAG = "CardServices";
    private String[] reg;
    private String detaials;
    protected static boolean isComplete = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {

        String data,data1,data2,data3,sps,mode="WRITEMODE";
        Log.d("CardService",byteArrayToString(commandApdu));
        if(ByteArrayToHexString(commandApdu).equals("00A4040005F01234567800")){
            Log.i(TAG, "Received APDU: " + ByteArrayToHexString(commandApdu));
            String msg= ScheduleStorage.getData(this,"identification"); ;
            if(ScheduleStorage.getData(this,"mode").equals("1"))
                mode="WRITEMODE";
            else
                mode="READMODE";
            Log.d("CardService",msg);
            return mode.getBytes();
        }
        else if(byteArrayToString(commandApdu).equals("mode")){
            Log.d("CardService",mode);
            return mode.getBytes();
        }
        else if (byteArrayToString(commandApdu).contains("command"))
        {
            String c = byteArrayToString(commandApdu).replaceAll("command", "");
            ScheduleStorage.setData(this, "p", sdf.format(new Date().getTime()));
            int index = Integer.parseInt(c);
            updateState(index);
            Log.d("CardService", "" + index);
            Intent intent = new Intent();
            intent.putExtra("index", index);
            intent.setClass(this, HintActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            return ConcatArrays(new String("1").getBytes());
        } else if (byteArrayToString(commandApdu).equals("box1")){
            sps = ScheduleStorage.getData(this,"ff1");
            if (sps.equals("0"))
                data1 = "no data";
            else
                data1 = "ff1"+getTime(sps);
            isComplete = true;
            byte[] accountBytes = data1.getBytes();
            return ConcatArrays(accountBytes);
        }
        else if(byteArrayToString(commandApdu).equals("box2")){
            sps = ScheduleStorage.getData(this,"ff2");
            if(sps.equals("0"))
                data2 = "no data";
            else
                data2 = "ff2"+getTime(sps);
            byte[] accountBytes = data2.getBytes();
            return ConcatArrays(accountBytes);
        }
        else if(byteArrayToString(commandApdu).equals("box3"))
        {
            sps = ScheduleStorage.getData(this,"ff3");
            if(sps.equals("0"))
                data3 = "no data";
            else
                data3 = "ff3"+getTime(sps);
            byte[] accountBytes = data3.getBytes();
            return ConcatArrays(accountBytes);
        }
        else
            data = "no such command,";
        byte[] accountBytes = data.getBytes();
        return ConcatArrays(accountBytes);
    }

    private void updateState(int index) {
        String box = "ff"+(index/3 + 1) ;
        String data = ScheduleStorage.getData(this,box);
        if(!data.equals("0")) {
            String[] scheduleString = data.split("\\|");
            scheduleString[index/3+8] = "true";
            scheduleString[index/3+5] = sdf.format(new Date());
            data = scheduleString[0]+"|"+scheduleString[1]+"|"+scheduleString[2]+"|"+scheduleString[3]+"|"+scheduleString[4]
                    +"|"+scheduleString[5]+"|"+scheduleString[6]+"|"+scheduleString[7]+"|"+scheduleString[8]+"|"+scheduleString[9]+"|"+scheduleString[10]+"|"+scheduleString[11];

            ScheduleStorage.setData(this,box,data);
        }

    }

    private String getTime(String sps) {
        Date now = Calendar.getInstance().getTime();
        String[] s = sps.split("\\|");
        String time="";
        Long dur;
        try {
            for (int i=0;i<3;i++){
                dur = sdf.parse(s[i]).getTime();
                time += String.format("|%s",dur-now.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    //arduino的string字串在傳送時被轉換成byte類陣列
    //將byte陣列資料還原成一String字串
    private String byteArrayToString(byte[] commandApdu) {
        String a= new String(commandApdu);
        String[] b=a.split(",");
        if(b[0].equals("F")){
            return b[1].substring(0, b[1].length() - 1);
        }
        else
            return "";
    }

    @Override
    public void onDeactivated(int reason) {}

    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
    //把一個8位元的byte切成前四位元和後四位元，每個四個位元代表一個0-F的char字元
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

}
