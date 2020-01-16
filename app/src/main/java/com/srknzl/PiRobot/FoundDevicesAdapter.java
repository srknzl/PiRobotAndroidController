package com.srknzl.PiRobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FoundDevicesAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    List<FoundDeviceModel> modellist;
    ArrayList<FoundDeviceModel> arrayList;

    public class DeviceViewHolder{
        TextView mAddress;
        TextView mName;
    }

    public FoundDevicesAdapter(Context context, List<FoundDeviceModel> modellist){
        mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<FoundDeviceModel>();
        this.arrayList.addAll(modellist);
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int position) {
        return modellist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        DeviceViewHolder holder;
        if (view==null){
            holder = new DeviceViewHolder();
            view = inflater.inflate(R.layout.device, null);

            holder.mAddress = view.findViewById(R.id.device_address);
            holder.mName = view.findViewById(R.id.device_name);

            view.setTag(holder);
        }
        else {
            holder = (DeviceViewHolder)view.getTag();
        }
        holder.mAddress.setText(modellist.get(position).getAddress());
        holder.mName.setText(modellist.get(position).getName());
        return view;
    }
}
