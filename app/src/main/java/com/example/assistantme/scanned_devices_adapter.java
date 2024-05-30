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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class scanned_devices_adapter extends BaseAdapter {
    Context context;
    public static Button pair;
    public scanned_devices_adapter(Context context) {
        this.context=context;
    }

    @Override
    public int getCount() {
        return MainActivity.scan_device.size();
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
        View v= LayoutInflater.from(context).inflate(R.layout.scanned_device_layout,parent,false);
        TextView device_name=v.findViewById(R.id.device_name_scan);
        TextView mac_add=v.findViewById(R.id.mac_address_scan);
        pair=v.findViewById(R.id.btn_pair);
        device_name.setText(MainActivity.scan_device.get(position).getName());
        mac_add.setText(MainActivity.scan_device.get(position).getAddress());

        pair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method m    = MainActivity.scan_device.get(position).getClass().getMethod("createBond", (Class[]) null);
                    m.invoke(MainActivity.scan_device.get(position), (Object[]) null);
                  //  MainActivity.popup.dismiss();

                }
                catch (Exception e) {
                    Toast.makeText(context, "pairing failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
      BluetoothAdapter bluetooth=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
       ArrayList<BluetoothDevice> paired=new ArrayList<>(pairedDevices);


            if(paired.contains(MainActivity.scan_device.get(position))){
                pair.setClickable(false);
                pair.setText("Paired");

            }


        return v;
    }
}
