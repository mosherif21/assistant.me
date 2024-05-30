package com.example.assistantme;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.lang.reflect.Method;

public class send_to_device extends BaseAdapter {
    Context context;
    public static Button send;
    public send_to_device(Context context) {
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
        View v= LayoutInflater.from(context).inflate(R.layout.sendto_device_layout,parent,false);
        TextView device_name=v.findViewById(R.id.device_name_send);
        TextView mac_add=v.findViewById(R.id.mac_address_Send);
        send=v.findViewById(R.id.btn_send);
        device_name.setText(MainActivity.pair_device.get(position).getName());
        mac_add.setText(MainActivity.pair_device.get(position).getAddress());
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.chosen_send_device=MainActivity.pair_device.get(position);
                     MainActivity.popup.dismiss();
                }
                catch (Exception e) {

                    //Toast.makeText(getApplicationContext(), "unpairing failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}
