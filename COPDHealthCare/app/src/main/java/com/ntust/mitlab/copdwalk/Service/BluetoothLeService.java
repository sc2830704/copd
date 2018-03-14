package com.ntust.mitlab.copdwalk.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ntust.mitlab.copdwalk.MainActivity;
import com.ntust.mitlab.copdwalk.R;
import com.ntust.mitlab.copdwalk.StepService.SensorListener;
import com.ntust.mitlab.copdwalk.util.MyException;
import com.ntust.mitlab.copdwalk.util.MyShared;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.ntust.mitlab.copdwalk.DeviceActivity.lastLeftDevice;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.CLIENT_CHARACTERISTIC_CONFIG_NEW;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.CLIENT_CHARACTERISTIC_CONFIG_ZOE;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.Characterstic_SPO2_UUID;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.UUID_DATA_READ;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.UUID_SERVICE_SPO2_wrist;
import static com.ntust.mitlab.copdwalk.Model.GattAttributes.UUID_SERVICE_ZOE;

public class BluetoothLeService extends Service {
    private final static String TAG = "BluetoothLeService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private TreeMap<String,BluetoothGatt> bleGatts = new TreeMap<>();

    private final static String TAG_BLE = "BLE_Service";

    public final static String ACTION_GATT_CONNECTED =
            "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "EXTRA_DATA";
    public final static String DEVICE_ADDR =
            "DEVICE_ADDR";
    public final static String BT_NOT_ENABLE ="BT_NOT_ENABLE";
    public final static String BT_IS_CONNECTED ="BT_IS_CONNECTED";
    public final static String BT_CONNECTED_FAIL ="BT_CONNECTED_FAIL";
    public final static String NO_DEVICE = "NO_DEVICE";
    public final static String TYPE = "DEVICE_TYPE";
    Notification notification;

    public enum DEVICE_TYPE {
        WATCH("DEVICE_WATCH"),
        SPO2("DEVICE_SPO2"),
        ENV("DEVICE_ENV"),
        OTHERS("OTHER");
        private String name;
        DEVICE_TYPE(String s) {
            name = s;
        }
        @Override
        public String toString() {
            return name;
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG_BLE,"onConnectionStateChange , State:"+newState);
            switch (newState){
                case BluetoothProfile.STATE_CONNECTED:
                    broadcastUpdate(ACTION_GATT_CONNECTED,gatt.getDevice().getAddress());
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    gatt.getDevice().getAddress();
                    broadcastUpdate(ACTION_GATT_DISCONNECTED,gatt.getDevice().getAddress());
                    bleGatts.remove(gatt.getDevice().getAddress());
                    break;
                default:
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            Log.d(TAG_BLE,"onServicesDiscovered, States:"+status);
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED,gatt.getDevice().getAddress());
            } else
            {
                //Log.w("TAG3", "onServicesDiscovered received: " + status);
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status)
        {
            //Log.d(TAG_BLE,"getUuid(): "+characteristic.getUuid());
            //Log.d(TAG_BLE,"getProperties():"+characteristic.getProperties());
            //Log.d(TAG_BLE,"getStringValue(): "+characteristic.getStringValue(0));
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                   //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        // seems never be called
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
            Log.d(TAG_BLE,"onCharacteristicChanged");
            String address = gatt.getDevice().getAddress();
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,address);
//            String data = characteristic.getStringValue(0);
//            Log.i(TAG_BLE,"-----------------\nonCharacteristicChanged ");
//            Log.d("onCharacteristicChanged", String.format("Device:%s , Data: %s \n-----------------",addr,data));
        }
    };
    private DEVICE_TYPE getDeviceType(String address){
        if(address.equals(MyShared.getData(BluetoothLeService.this, DEVICE_TYPE.WATCH.toString())))
            return DEVICE_TYPE.WATCH;
        else if(address.equals(MyShared.getData(BluetoothLeService.this,DEVICE_TYPE.SPO2.toString())))
            return DEVICE_TYPE.SPO2;
        else {
            return lastLeftDevice;
        }
    }
    private void broadcastUpdate(final String action, final String address)
    {
        Log.d("broadcastUpdate","action " + action);
        final Intent intent = new Intent(action);
        intent.putExtra("address",address);
        intent.putExtra(TYPE,getDeviceType(address).toString());
        sendBroadcast(intent);
    }
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic, String address)
    {
        final Intent intent = new Intent(action);
        String text;
        if(getDeviceType(address)==DEVICE_TYPE.WATCH)
            text = bleByte2Text(characteristic.getValue());
        else
            text = characteristic.getStringValue(0);
        Log.d(TAG + "DEBUG",getDeviceType(address)+", DATA:" + text);

        intent.putExtra(EXTRA_DATA,text);
        intent.putExtra(DEVICE_ADDR,address);
        intent.putExtra(TYPE,getDeviceType(address).toString());
        sendBroadcast(intent);
    }
    public class LocalBinder extends Binder{
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG_BLE,"Service on bind");

        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.d(TAG_BLE,"Service on Unbind");
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        disconnectGatts();
        closeGatt();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

            if (mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return mBluetoothAdapter.isEnabled();


    }

    public boolean connectGatt(final String address) throws MyException
    {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled() )
        {
            throw new MyException(BT_NOT_ENABLE);
        }else if(address==null){
            throw new MyException(NO_DEVICE);
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        //檢查gatt是否已經存在
        if(bleGatts.get(address)==null){
            BluetoothGatt bleGatt = device.connectGatt(this, false, mGattCallback);
            bleGatts.put(address,bleGatt);
            return bleGatt.connect();
        }else{
                throw new MyException(BT_IS_CONNECTED); //重複連線拋出Exception
        }
//        return true;
    }

    //disconnect device on specific address
    public void disconnectGatt(String address)
    {
        if (mBluetoothAdapter == null || bleGatts.get(address)==null)
        {
            Log.w(TAG, "disconnectGatt : BluetoothAdapter not initialized");
            return;
        }
        bleGatts.get(address).disconnect();
        bleGatts.remove(address);
    }

    //disconnect all connected device
    public void disconnectGatts()
    {
        if (mBluetoothAdapter == null || bleGatts.isEmpty())
        {
            Log.w(TAG, "disconnectGatt : BluetoothAdapter not initialized");
            return;
        }
        for(Map.Entry<String,BluetoothGatt> entry:bleGatts.entrySet()){
            entry.getValue().disconnect();
        }
    }
    public void closeGatt() {
        if (bleGatts.isEmpty()) {
            return;
        }
        for(Map.Entry<String,BluetoothGatt> entry:bleGatts.entrySet()){
            entry.getValue().close();
        }
        bleGatts.clear();
        //   mBluetoothGatt = null;
    }
    public boolean isConnecting(String address){
        if(bleGatts.containsKey(address))
            return true;
        else
            return false;
    }
    private String bleByte2Text(byte[] data){
        String text = null;
        if(data.length>0) {
            //宣告檢查碼
            int checksum = 0;
            //計算檢查碼
            for (int i = 1; i < data.length - 1; i++) {
                checksum += data[i];
            }
            //判斷檢查碼是否正確
            if (Integer.toHexString(checksum & 0xFF).equals(Integer.toHexString(data[data.length - 1] & 0xFF))) {
                //宣告資料變數
                byte[] bledata = new byte[data.length - 3];
                //取出header部分
                byte head = data[0];
                //Log.i("Header", String.valueOf(head & 0xFF));
                //取出資料部分位元組
                System.arraycopy(data, 2, bledata, 0, bledata.length);
                data = bledata;
                try {
                    //將資料部分位元組轉為文字
                    text = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("Header-"+String.valueOf(head & 0xFF), ""+text);
                //去除空白字元
                text = text.trim();
            }
        }
        return text;
    }
    public void sendData(String address,String msg){
        //依照Zoetek Protocol格式傳送資料
        //宣告20Byte資料變數
        byte[] data = new byte[2+msg.length()+1];
        //宣告標頭
        byte head = (byte)0x00;
        //宣告類型
        byte type = (byte)0x80;
        //加入標頭與類型
        data[0] = head;
        data[1] = type;
        //宣告檢查碼
        int checksum = 0;
        //計算檢查碼
        for(int i=0;i<msg.getBytes().length;i++){
            checksum = checksum + msg.getBytes()[i];
            data[i+2] = msg.getBytes()[i];
        }
        checksum = checksum & 0xFF;
        checksum = (checksum + (short)(type) & 0xFF) & 0xFF;
        //加入檢查碼
        data[2+msg.length()+1-1] = (byte)checksum;
        //發送資料
        writeCharacteristic(data, address, DEVICE_TYPE.WATCH);
    }
    public void sendSPO2(String address,String data){
        writeCharacteristic(data.getBytes(), address, DEVICE_TYPE.SPO2);
    }
    public void readCharacteristic(BluetoothGattCharacteristic characteristic, String address)
    {
        if (mBluetoothAdapter == null)
        {
            Log.w(TAG, "readCharacteristic : BluetoothAdapter not initialized");
            return;
        }
        bleGatts.get(address).readCharacteristic(characteristic);
    }
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled, String address,DEVICE_TYPE type)
    {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "setCharacteristicNotification : BluetoothAdapter not initialized");
            return;
        }
        String CLIENT_CHARACTERISTIC_CONFIG;
        if(type==DEVICE_TYPE.SPO2){
            CLIENT_CHARACTERISTIC_CONFIG = CLIENT_CHARACTERISTIC_CONFIG_NEW;
            Log.d("characteristic", characteristic.getUuid()+"");
            if(characteristic.getDescriptors().isEmpty())
                Log.d("isEmpty","isEmpty");
            for(BluetoothGattDescriptor des : characteristic.getDescriptors()){
                Log.d("getDescriptors",""+des.getUuid());
            }
        }
        else{
            CLIENT_CHARACTERISTIC_CONFIG = CLIENT_CHARACTERISTIC_CONFIG_ZOE;
        }

        bleGatts.get(address).setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if(descriptor!=null){
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bleGatts.get(address).writeDescriptor(descriptor);
        }

    }
    private void writeCharacteristic(byte[] data, String address,DEVICE_TYPE type)
    {
        if (mBluetoothAdapter == null || bleGatts.isEmpty()) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        UUID Service,Characteristic;
        if(type==DEVICE_TYPE.SPO2){
            Service = UUID_SERVICE_SPO2_wrist;
            //Characteristic = SerialPortUUID;
            Characteristic = Characterstic_SPO2_UUID;
        }
        else{
            Service = UUID_SERVICE_ZOE;
            Characteristic = UUID_DATA_READ;
        }
        if(bleGatts.get(address).getService(Service)==null)
            return;
        BluetoothGattCharacteristic chara = bleGatts.get(address).getService(Service).getCharacteristic(Characteristic);
        chara.setValue(data);
        bleGatts.get(address).writeCharacteristic(chara);
    }
    public boolean isCharAvaiable(String address,DEVICE_TYPE type){
        UUID Service,Characteristic;
        if(type==DEVICE_TYPE.SPO2){
            Service = UUID_SERVICE_SPO2_wrist;
        }
        else{
            Service = UUID_SERVICE_ZOE;
        }
//        List<BluetoothGattService> bleService = bleGatts.get(address).getServices();
        for(BluetoothGattService bleService : bleGatts.get(address).getServices()){
            Log.d("uuid:",""+bleService.getUuid());
        }
        Log.d("isCharAvaiable","isCharAvaiable");
        if(bleGatts.get(address).getService(Service)==null)
            return false;
        return true;
    }
    public List<BluetoothGattService> getSupportedGattServices(String address)
    {
        if(bleGatts.isEmpty()){
            return null;
        }
        return bleGatts.get(address).getServices();
    }
    public BluetoothGattService getGattSerivce(String address, UUID uuid){
        return bleGatts.get(address).getService(uuid);
    }
    public void stratForeground(){

        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentTitle("COPD Walk");
        builder.setContentText("運動中");
        builder.setSmallIcon(R.drawable.ic_lungs);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        startForeground(1,notification);//讓service變成前景對象，顯示在狀態欄中
    }

}