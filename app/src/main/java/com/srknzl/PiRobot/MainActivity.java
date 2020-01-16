package com.srknzl.PiRobot;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListViewAdapter adapter;
    ArrayList<Model> arrayList = new ArrayList<>();
    final Context context = this;
    Vector<BluetoothDevice> foundDevices = new Vector<>();


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundDevices.add(device);
                if(device!=null && device.getName().equals("raspberrypi")){
                    Bluetooth.bluetoothAdapter.cancelDiscovery();
                    ConnectThread connectThread = new ConnectThread(device, context);
                    connectThread.start();
                }
            }
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button bluetoothButton = this.findViewById(R.id.bluetooth_button);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        CheckBox c = this.findViewById(R.id.connection_status);

        if(Bluetooth.connected && Bluetooth.communicationThread != null){
            c.setChecked(true);
        }else{
            c.setChecked(false);
        }
        final LinearLayout bgShader = this.findViewById(R.id.background_shader);
        final ProgressBar pb = this.findViewById(R.id.progressbar);

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgShader.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

                boolean paired = Bluetooth.connectIfPaired(context);
                if(!paired){
                    foundDevices.clear();
                    boolean discovering = Bluetooth.bluetoothAdapter.startDiscovery();
                    if(!discovering){
                        Toast.makeText(context,"Cannot connect for some reason", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//---------
        mainmenu();
//-----------
    }




    public void setlist(Vector<String> titles, Vector<String> descs, Vector<Integer> icons) {// Title desc icon
        arrayList.clear();
        listView = findViewById(R.id.listView);
        for (int i =0; i<titles.size(); i++){
            Model model = new Model(titles.elementAt(i), descs.elementAt(i), icons.elementAt(i));
            arrayList.add(model);
        }
        adapter = new ListViewAdapter(this, arrayList);
        listView.setAdapter(adapter);
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void mainmenu(){
        Vector<String> titles = new Vector<>();
        Vector<String> descs = new Vector<>();
        Vector<Integer> icons = new Vector<>();

        titles.add("Manuel with Buttons");
        descs.add("Control the car with buttons");
        icons.add(R.drawable.car);

        titles.add("Auto");
        descs.add("Control the car in autonomous mode");
        icons.add(R.drawable.car);

        setlist(titles,descs,icons);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}