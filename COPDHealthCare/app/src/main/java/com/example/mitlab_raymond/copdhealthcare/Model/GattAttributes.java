package com.example.mitlab_raymond.copdhealthcare.Model;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mitlab on 2016/12/22.
 */

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";

    //write data from BLE device, we use this characteristic
    public static String CLIENT_CHARACTERISTIC_CONFIG_ZOE = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG_CHIP = "00002901-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG_NEW = "00002902-0000-1000-8000-00805f9b34fb";

    public static final UUID UUID_DATA_WRITE	= UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";
    //public static final UUID SerialPortUUID= UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");

    //read data from phone to BLE device, we use this characteristic
    public static final UUID UUID_DATA_READ	= UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID SerialPortUUID= UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");
    public static final UUID Characterstic_SPO2_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    //read data from phone to BLE device, we use this service
    public static final UUID UUID_SERVICE_ZOE	= UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_SERVICE_SPO2_glove = UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_SPO2_wrist = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

    static
    {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        attributes.put("00001811-0000-1000-8000-00805f9b34fb", "Alert Notification Service");
        attributes.put("00001810-0000-1000-8000-00805f9b34fb", "Blood Pressure");
        attributes.put("00001818-0000-1000-8000-00805f9b34fb", "Cyling Power");
        attributes.put("00001816-0000-1000-8000-00805f9b34fb", "Cycling Speed and Cadence");
        attributes.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        attributes.put("00001808-0000-1000-8000-00805f9b34fb", "Glucose");
        attributes.put("00001809-0000-1000-8000-00805f9b34fb", "Health Thermometer");
        attributes.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
        attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "FFF0");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        attributes.put("00002a38-0000-1000-8000-00805f9b34fb", "Body Sensor Location");
        attributes.put("0000fff1-0000-1000-8000-00805f9b34fb", "Read Channel");
        attributes.put("0000fff2-0000-1000-8000-00805f9b34fb", "Write Channel");
        attributes.put("0000fff3-0000-1000-8000-00805f9b34fb", "Update Channel");
        attributes.put("0000fff4-0000-1000-8000-00805f9b34fb", "Read Channel");
        attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "Write Channel");
    }

    public static String lookup(String uuid, String defaultName)
    {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
