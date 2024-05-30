package com.example.assistantme;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.lang.reflect.Method;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Set;

public class paired_devices_adapter extends BaseAdapter {
    Context context;
   public static Button unpair;
    public paired_devices_adapter(Context context) {
        this.context=context;
    }

    @Override
    public int getCount() {
        return MainActivity.pair_device.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v= LayoutInflater.from(context).inflate(R.layout.paired_device_layout,parent,false);
        TextView device_name=v.findViewById(R.id.device_name);
        TextView mac_add=v.findViewById(R.id.mac_address);
        unpair=v.findViewById(R.id.btn_unpair);
         device_name.setText(MainActivity.pair_device.get(position).getName());
         mac_add.setText(MainActivity.pair_device.get(position).getAddress());
        unpair.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {

                             Method m    = MainActivity.pair_device.get(position).getClass().
                                     getMethod("removeBond", (Class[]) null);
                             m.invoke(MainActivity.pair_device.get(position), (Object[]) null);
                             MainActivity.pair_device.remove(position);
                             MainActivity.adapter.notifyDataSetChanged();
                 }
                 catch (Exception e) {
                     Toast.makeText(context, "unpairing failed", Toast.LENGTH_SHORT).show();
                 }
             }
         });
        return v;
    }
}
