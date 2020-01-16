package com.srknzl.PiRobot;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListView deviceView;
    ListViewAdapter adapter;

    FoundDevicesAdapter devicesAdapter;
    ArrayList<Model> arrayList = new ArrayList<>();
    ArrayList<FoundDeviceModel> deviceList = new ArrayList<>();
    final int REQUEST_PERMISSION_BLUETOOTH = 22;

    final Context context = this;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                FoundDeviceModel model = new FoundDeviceModel(device.getName(), device.getAddress());
                Log.e("BLUETOOTH DEVICE", device.getAddress() + "-" + device.getName());
                deviceList.add(model);
                devicesAdapter = new FoundDevicesAdapter(context, deviceList);
                deviceView.setAdapter(devicesAdapter);

                if (device.getName().equals("raspberrypi")) {
                    Bluetooth.bluetoothAdapter.cancelDiscovery();
                    ConnectThread connectThread = new ConnectThread(device, context);
                    connectThread.start();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                final LinearLayout bgShader = ((MainActivity) context).findViewById(R.id.background_shader);
                final ProgressBar pb = ((MainActivity) context).findViewById(R.id.progressbar);
                bgShader.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
                deviceView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Scan finished!", Toast.LENGTH_SHORT).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button bluetoothButton = this.findViewById(R.id.bluetooth_button);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        CheckBox c = this.findViewById(R.id.connection_status);

        deviceView = this.findViewById(R.id.foundDevices);
        devicesAdapter = new FoundDevicesAdapter(this, deviceList);
        deviceView.setAdapter(devicesAdapter);

        if (Bluetooth.connected && Bluetooth.communicationThread != null) {
            c.setChecked(true);
        } else {
            c.setChecked(false);
        }
        final LinearLayout bgShader = this.findViewById(R.id.background_shader);
        final ProgressBar pb = this.findViewById(R.id.progressbar);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Not to annoy user.
                Toast.makeText(this, "Bluetooth discovery permission must be granted to use the app.", Toast.LENGTH_SHORT).show();
            } else {

                // Request permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSION_BLUETOOTH);
            }
        } else {
            // Permission has already been granted.
            Toast.makeText(this, "Bluetooth discovery permission already granted.", Toast.LENGTH_SHORT).show();
        }

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bluetooth.communicationThread != null && Bluetooth.connected){
                    Toast.makeText(context, "Already connected!", Toast.LENGTH_SHORT).show();
                }
                if (!Bluetooth.bluetoothAdapter.isEnabled()) {
                    final int REQUEST_ENABLE_BT = 23;
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    return;
                }
                bgShader.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);


                boolean paired = Bluetooth.connectIfPaired(context);
                if (!paired) {
                    deviceList.clear();
                    listView.setVisibility(View.GONE);
                    deviceView.setVisibility(View.VISIBLE);

                    boolean discovering = Bluetooth.bluetoothAdapter.startDiscovery();
                    if (!discovering) {
                        Toast.makeText(context, "Cannot connect, bluetooth may not be enabled", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mainmenu();

    }

    public void setlist(Vector<String> titles, Vector<String> descs, Vector<Integer> icons) {// Title desc icon
        arrayList.clear();
        listView = findViewById(R.id.listView);
        for (int i = 0; i < titles.size(); i++) {
            Model model = new Model(titles.elementAt(i), descs.elementAt(i), icons.elementAt(i));
            arrayList.add(model);
        }
        adapter = new ListViewAdapter(this, arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            Toast.makeText(context, "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(context, "Bluetooth couldn't enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void mainmenu() {
        Vector<String> titles = new Vector<>();
        Vector<String> descs = new Vector<>();
        Vector<Integer> icons = new Vector<>();

        titles.add("Manuel with Buttons");
        descs.add("Control the car with buttons");
        icons.add(R.drawable.car);

        titles.add("Auto");
        descs.add("Control the car in autonomous mode");
        icons.add(R.drawable.car);

        setlist(titles, descs, icons);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BLUETOOTH: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();

                    // Permission granted.
                } else {
                    Toast.makeText(this, "Permission must be granted to use the application.", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Bluetooth.bluetoothAdapter != null) {
            Bluetooth.bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }
}