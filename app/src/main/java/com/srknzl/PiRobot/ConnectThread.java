package com.srknzl.PiRobot;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.UUID;



public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Activity context;

    public ConnectThread(BluetoothDevice device, Context context) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.context = (Activity) context;
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        Bluetooth.bluetoothAdapter.cancelDiscovery();
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (final IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("BLUETOOTH", "Could not close the client socket", closeException);
            }
            Bluetooth.connected = false;
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar pb = context.findViewById(R.id.progressbar);
                    LinearLayout bgShader = context.findViewById(R.id.background_shader);
                    CheckBox c = context.findViewById(R.id.connection_status);

                    c.setChecked(false);
                    bgShader.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    if(Bluetooth.connected && Bluetooth.communicationThread != null){
                        Toast.makeText(context,"Connection Lost!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"Could not connect!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }
        Bluetooth.connected = true;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = context.findViewById(R.id.progressbar);
                LinearLayout bgShader = context.findViewById(R.id.background_shader);
                CheckBox checkbox = context.findViewById(R.id.connection_status);

                Toast.makeText(context,"Connected!", Toast.LENGTH_SHORT).show();
                checkbox.setChecked(true);
                bgShader.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
            }
        });

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.

        CommunicationThread commThread = new CommunicationThread(mmSocket, context);
        commThread.start();
        Bluetooth.communicationThread = commThread;
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Could not close the client socket", e);
        }
    }
}