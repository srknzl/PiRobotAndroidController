package com.srknzl.PiRobot;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.Set;

public class Bluetooth {
    static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static boolean connected = false;
    static CommunicationThread communicationThread = null;

    /*
    Desc: Queries paired devices and connects to name raspberrypi if it exists in paired devices
    Returns if paired
     */
    public static boolean connectIfPaired(Context c){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if(deviceName.equals("raspberrypi")){
                    ConnectThread connectThread = new ConnectThread(device, c);
                    connectThread.start();
                    return true;
                }
            }
        }
        return false;
    }
}
