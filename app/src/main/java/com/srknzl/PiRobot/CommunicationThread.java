package com.srknzl.PiRobot;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommunicationThread extends Thread {

    BluetoothSocket socket;
    InputStream in = null;
    OutputStream out = null;
    MainActivity mainActivity;
    Boolean abort = false;
    byte[] Buffer;


    public CommunicationThread(BluetoothSocket mmSocket, Context context){
        this.socket = mmSocket;
        mainActivity = ((MainActivity)context);

        try {
            this.in = this.socket.getInputStream();
            this.out = this.socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
                    CheckBox c = toolbar.findViewById(R.id.connection_status);
                    c.setChecked(false);
                    Toast.makeText(mainActivity, "Connection lost", Toast.LENGTH_LONG).show();
                    abort = true;
                }
            });
        }
    }

    @Override
    public void run() {
        Buffer = new byte[1024];
        int numBytes;
        while (!abort){
            try{
                numBytes = in.read(Buffer);
                // todo showMessage in ui
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BLUETOOTH", "Input stream was disconnected");
                break;
            }
        }
    }
    public void write(byte[] bytes) {
        try {
            out.write(bytes);
            //todo update ui as success
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Error occurred when sending data", e);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
                    CheckBox c = toolbar.findViewById(R.id.connection_status);
                    c.setChecked(false);
                    Toast.makeText(mainActivity, "Connection lost", Toast.LENGTH_LONG).show();
                    Bluetooth.connected = false;
                }
            });
            // Send a failure message back to the activity.
            //todo update ui as failure
        }
    }
    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Could not close the connect socket", e);
        }
    }
}
