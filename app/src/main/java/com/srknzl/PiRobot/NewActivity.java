package com.srknzl.PiRobot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
public class NewActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Model> arrayList = new ArrayList<>();
    ListViewAdapter adapter;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String mContent = intent.getStringExtra("activityName");
        //Acil
        String des[] = new String[100];
        Arrays.fill(des, null);
        int i[] = new int[100];
        Arrays.fill(i,R.drawable.car);
        //-----MainMenu
        if (mContent.equals("ManuelWithButtons")){

            extra(mContent);
            setContentView(R.layout.manuel_with_buttons);

            Button topButton = this.findViewById(R.id.forward);
            Button backButton = this.findViewById(R.id.back);
            Button leftButton = this.findViewById(R.id.left);
            Button rightButton = this.findViewById(R.id.right);
            Button stopButton = this.findViewById(R.id.stop);

            topButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Bluetooth.communicationThread!=null && Bluetooth.connected){
                        Bluetooth.communicationThread.write("forward".getBytes(StandardCharsets.UTF_8));
                    }else{
                        Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Bluetooth.communicationThread!=null && Bluetooth.connected){
                        Bluetooth.communicationThread.write("backward".getBytes(StandardCharsets.UTF_8));
                    }else{
                        Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Bluetooth.communicationThread!=null && Bluetooth.connected){
                        Bluetooth.communicationThread.write("left".getBytes(StandardCharsets.UTF_8));
                    }else{
                        Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Bluetooth.communicationThread!=null && Bluetooth.connected){
                        Bluetooth.communicationThread.write("right".getBytes(StandardCharsets.UTF_8));
                    }else{
                        Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Bluetooth.communicationThread!=null && Bluetooth.connected){
                        Bluetooth.communicationThread.write("stop".getBytes(StandardCharsets.UTF_8));
                    }else{
                        Toast.makeText(context,"Connect first!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (mContent.equals("Auto")){
            extra(mContent);
            setContentView(R.layout.autonomous);
        }

    }
    String mcon="none";
    boolean booleansearch =true;
    public void extra(String m){
        mcon=m;
        booleansearch =false;
    }
    public void setlist(String t[],String d[],int ic[]){
        arrayList.clear();
        Arrays.sort(t);
        listView = findViewById(R.id.listView);
        for (int i =0; i<t.length; i++){
            Model model = new Model(t[i], d[i], ic[i]);
            //bind all strings in an array
            arrayList.add(model);
        }
        adapter = new ListViewAdapter(this, arrayList);
        listView.setAdapter(adapter);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}