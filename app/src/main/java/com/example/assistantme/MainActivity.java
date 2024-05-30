package com.example.assistantme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class MainActivity extends AppCompatActivity {
//Initialize variables
  public static  Dialog popup;
  public static  Dialog popup2;
    public static final int REQUEST_CODE_ENABLE_BT=3;
    public static final int Result_load_image=4;
    public static Bitmap bmp=null;
    static final int REQUEST_IMAGE_CAPTURE = 20;
    static final int REQUEST_CODE_QR_SCAN = 25;
    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    static ArrayList<String> chat_messages;
    AutoCompleteTextView message;
    CardView btn;
    String command;

    String newString;
    RecyclerView recyclerView;
    chat_adapter chat_adapter;
    ImageView bot_image;
    HashMap<String, String> info = new HashMap<String, String>();
    ArrayList<String> names = new ArrayList<>();
     ArrayList<String> phones = new ArrayList<>();
     //public static BluetoothDevice chosen_send_device;
     public static Intent dataaa;
   public static ArrayList<BluetoothDevice> scan_device = new ArrayList<BluetoothDevice>();
   public static ArrayList<BluetoothDevice> pair_device ;
   public static BluetoothDevice chosen_send_device ;
    ArrayList<String> names_for_suggestion = new ArrayList<>();
    String text;
    public static  BroadcastReceiver Receiver;
    public static paired_devices_adapter adapter;
    public static scanned_devices_adapter scan_adapter;
    public static send_to_device send_adapter;
    BluetoothSocket bs;

    final boolean[] rigth = {false};
    final boolean[] left = {false};
    final boolean[] up = {false};
    final boolean[] down = {false};

    RelativeLayout layout_joystick;
    RelativeLayout layout_joystick_arm;
    TextView  textView5;
    static Boolean arm_open=true;
    static Boolean sec_tun=false;
    static Boolean follow_me=false;
    joystick js;
    joystick js2;
    String angles;
    public BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    UUID myUUID =null;

      int notificationId=20;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    InputStream socketInputStream = null;


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 4*1000; //Delay for 15 seconds.  One second = 1000 milliseconds.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // dataaa=new Intent();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        adapter=new paired_devices_adapter(this);
        scan_adapter=new scanned_devices_adapter(this);
        send_adapter=new send_to_device(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.actionbar_layout, null);
        getSupportActionBar().setCustomView(v);
        btn = findViewById(R.id.card_btn_send);
        bot_image = findViewById(R.id.bot_image);
        message = findViewById(R.id.chat_input);
        popup=new Dialog(this);
        popup2=new Dialog(this);
        recyclerView = findViewById(R.id.recyclerview);
        message.setHint("Enter your command");
        chat_messages = new ArrayList<String>();
        chat_messages.add("How can i help you?");
        RecyclerView chat = findViewById(R.id.recyclerview);
        chat_adapter = new chat_adapter();
        chat.setLayoutManager(new LinearLayoutManager(this));
        chat.setAdapter(chat_adapter);
        info.put("Hi", "Hello... Pleased to meet you!");
        info.put("Hello", "Hey you");
        info.put("how are you?", "Great!");
        info.put("what time is it", "Look at your watch!");
        //step_count_test();
        ////notifications
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


            builder = new NotificationCompat.Builder(this, "Assistant")
                .setSmallIcon(R.drawable.chat_head_logo)
                .setContentTitle("Security Alert!")
                .setContentText("Movement detected")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

         notificationManager = NotificationManagerCompat.from(this);

                        createNotificationChannel();



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command = message.getText().toString().trim();
                try {
                    fn_command(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        Runnable runnable = new Runnable(){
            public void run() {   message.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text = message.getText().toString().toLowerCase();
                    if (text.equalsIgnoreCase("dial") || text.equalsIgnoreCase("call")||text.equalsIgnoreCase("send sms to")) {
                        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            readcontacts();

                            for (String st : names) {
                                newString = text + " " + st;
                                if (!names_for_suggestion.contains(newString))
                                    names_for_suggestion.add(newString);
                            }
                            List<String> name = names_for_suggestion;
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.contact_suggestion_layout, R.id.list_item, name);
                            message.setAdapter(adapter);
                            message.setThreshold(6);
                        }
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


    //startService(new Intent(getApplicationContext(),floating_chat_head.class));
}
    @SuppressLint({"Range", "MissingPermission"})
    private void fn_command(String command) throws IOException {
        if (!command.equals("")) {
            bot_image.setVisibility(View.INVISIBLE);
        }
        if (command.equals("")) {
            Toast.makeText(getApplicationContext(), "please enter a command", Toast.LENGTH_SHORT).show();
        }
         else if (command.toLowerCase().contains("alarm") || (command.toLowerCase().contains("set") && command.contains("alarm"))) {
            String[] commands = command.split(" ");
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            try {
                chat_messages.add(command);
                String[] commandss = commands[3].split(":");
                intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(commandss[0]));
                intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(commandss[1]));
                startActivity(intent);
                chat_messages.add("alarm set for " + commandss[0] + ":" + commandss[1]);

            } catch (Exception e) {
                e.printStackTrace();
                chat_messages.add("Please specify the alarm time");

            }
            chat_adapter.notifyDataSetChanged();
        } else if (command.toLowerCase().contains("what") && (command.toLowerCase().contains("time"))) {
            chat_messages.add(command);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String time = dtf.format(now);
            chat_messages.add("The time now is " + time);
            chat_adapter.notifyDataSetChanged();
        } else if (command.toLowerCase().contains("dial") && !command.toLowerCase().contains("call") || command.toLowerCase().contains("call") && !command.toLowerCase().contains("dial")) {
            chat_messages.add(command);

            try {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS}, 0);
                    chat_messages.add("please allow contacts permission to complete your request");
                } else {
                    readcontacts();
                    boolean exist = false;
                    String[] commands = command.split(" ", 2);
                    for (int i = 0; i < names.size(); i++) {
                        if (names.get(i).equalsIgnoreCase(commands[1])) {
                            if (exist == false) {
                                if (commands[0].equalsIgnoreCase("dial")) {
                                    chat_messages.add("dialing " + names.get(i));
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + phones.get(i)));
                                    startActivity(intent);
                                } else if (commands[0].equalsIgnoreCase("call")) {
                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(this,
                                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                                        chat_messages.add("please allow calls permission to complete your request");
                                    } else {
                                        chat_messages.add("calling " + names.get(i));
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + phones.get(i)));
                                        startActivity(intent);
                                    }

                                }
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        chat_messages.add("contact doesn't exist");
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                chat_messages.add("please specify the contact name");
            }
            // names.clear();
            //  phones.clear();
            chat_adapter.notifyDataSetChanged();
        }
        else if (command.toLowerCase().contains("send sms to")) {
            chat_messages.add(command);

            try {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS}, 0);
                    chat_messages.add("please allow contacts permission to complete your request");
                } else {
                    readcontacts();
                    boolean exist = false;
                    String[] commands = command.split(" ", 4);
                    for (int i = 0; i < names.size(); i++) {
                        if (names.get(i).equalsIgnoreCase(commands[3])) {
                            if (exist == false) {
                                if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.SEND_SMS}, 2);
                                    chat_messages.add("please allow send sms permission to complete your request");
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: " + phones.get(i)));
                                    startActivity(intent);
                                    chat_messages.add("sms sent to " + names.get(i));


                                }
                                exist = true;
                            }
                        }
                    }
                    if (exist == false) {
                        chat_messages.add("contact doesn't exist");
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                chat_messages.add("please specify the contact name");
            }
            // names.clear();
            //  phones.clear();
            chat_adapter.notifyDataSetChanged();

        }
        else if (command.toLowerCase().contains("send email to")) {
            chat_messages.add(command);
            try {
                    String[] commands = command.split(" ", 4);
                                    Intent email = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:"));
                                     email.putExtra(Intent.EXTRA_EMAIL, new String[]{commands[3].trim()});
                                    startActivity(email);
                                    chat_messages.add("email sent to "+commands[3].trim());
                    }
             catch (Exception e) {
                e.printStackTrace();
                chat_messages.add("sending email failed");
            }
            chat_adapter.notifyDataSetChanged();
        }

else if(command.toLowerCase().contains("open"))
        {
            chat_messages.add(command);
            String[] commands = command.split(" ",2);

            try {
                Intent mainIntent = new Intent(Intent.ACTION_VIEW, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                for ( ResolveInfo info : getPackageManager().queryIntentActivities( mainIntent, 0) ) {
                    if ( info.loadLabel(getPackageManager()).equals(commands[1]) ) {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(info.activityInfo.applicationInfo.packageName);
                        startActivity(launchIntent);
                        chat_messages.add("opening "+commands[1]);
                        return;
                    }
                }
            }
             catch (Exception e) {
            chat_messages.add("app doesn't exist");
        }
        }
           else if(command.toLowerCase().contains("search for"))
        {
            chat_messages.add(command);
            String[] commands = command.split(" ",3);

            try {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, commands[2]);
                startActivity(intent);
                chat_messages.add("searching for "+commands[2]);
            }
             catch (Exception e) {
            chat_messages.add("searching failed");
        }
        }

        else if(command.toLowerCase().contains("turn on bluetooth"))
        {
            chat_messages.add(command);

            try {
                BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
                if(bluetooth!=null){
                    if (bluetooth.isEnabled()) {
                        chat_messages.add("Bluetooth already enabled");
                    }
                    else
                    {
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult (turnOn,REQUEST_CODE_ENABLE_BT);
                        chat_messages.add("press allow to enable bluetooth");
                    }
                }

                }
            catch (Exception e) {
                chat_messages.add("Bluetooth not found");
            }
        }
        else if (command.toLowerCase().contains("show paired devices")) {
            chat_messages.add(command);

            try {
                if (bluetooth.isEnabled()) {

                    popup.setContentView(R.layout.paired_devices_list);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ListView paired_devices = popup.findViewById(R.id.paired_devices_list);
                   Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
                    pair_device=new ArrayList<>(pairedDevices);

                    if(pair_device.size()==0)
                        chat_messages.add("no paired devices");
                    else {
                        chat_messages.add("Showing Paired Devices");
                        paired_devices.setAdapter(adapter);
                        popup.show();
                    }
                }
             else
                {
                    tun_on_bluetooth();
                }
            }
            catch (Exception e) {

            }
            chat_adapter.notifyDataSetChanged();
        }
   else if (command.toLowerCase().contains("scan for devices")) {
            chat_messages.add(command);
            if (bluetooth.isEnabled()) {
                try {
                     scan_device.clear();
                 
                    bluetooth.startDiscovery();
                    Receiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                // Get the BluetoothDevice object from the Intent
                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (!scan_device.contains(device) )
                                    scan_device.add(device);

                                scan_adapter.notifyDataSetChanged();
                            }
                        }
                    };

                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(Receiver, filter);
                    chat_messages.add("Scanning for devices");
                    popup.setContentView(R.layout.scanned_devices_list);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ListView scanned_devices = popup.findViewById(R.id.scanned_devices_list);
                    scanned_devices.setAdapter(scan_adapter);
                    popup.show();

                }catch (Exception e){

                }
                }
             else
                {
                    tun_on_bluetooth();
                }
             chat_adapter.notifyDataSetChanged();
            }

   else if (command.toLowerCase().contains("send image to device")) {
            chat_messages.add(command);
            if (bluetooth.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
                pair_device=new ArrayList<>(pairedDevices);
                if(pair_device.size()==0)
                    chat_messages.add("you need to pair a device");
                else{
                    try{
                        Intent i = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Result_load_image);


                    }catch(Exception exp){
                        System.out.println(exp.fillInStackTrace());
                    }
                }
                }
             else
                {
                    tun_on_bluetooth();
                }
             chat_adapter.notifyDataSetChanged();
            }
   else if (command.toLowerCase().contains("receive image from device")) {
       /*     chat_messages.add(command);
            if (bluetooth.isEnabled()) {
                Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
                pair_device=new ArrayList<>(pairedDevices);
                if(pair_device.size()==0)
                    chat_messages.add("you need to be paired to a device");
                 else{
                    try{
                            pair_device=new ArrayList<>(pairedDevices);
                            System.out.println(pair_device.get(0).getName());
                            popup.setContentView(R.layout.sendto_device_list);
                            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ListView sendto_devices = popup.findViewById(R.id.send_devices_list);
                            sendto_devices.setAdapter(send_adapter);
                            popup.show();
                            popup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(final DialogInterface arg0) {

                                    try {

                                        bluetooth.startDiscovery();
                                        BluetoothDevice device = bluetooth.getRemoteDevice(chosen_send_device.getAddress());
                                        bluetooth.cancelDiscovery();
                                     //   BluetoothAdapter a2=BluetoothAdapter.getDefaultAdapter();
                                   //     a2.startDiscovery();
                                 //       BluetoothDevice mydevice = a2.getRemoteDevice("f4:60:e2:bd:9d:8b");
                                 //       a2.cancelDiscovery();
                                       Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                        bs = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
                                        bs.connect();


                                             try{

                        chat_messages.add("recieving");
                        popup2.setContentView(R.layout.recieved_image);
                        popup2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ImageView recieved_img = popup2.findViewById(R.id.recieved_imageview);
                        Runnable runnable = new Runnable(){
                            public void run() {   try {
                         final InputStream   driverInputStream = bs.getInputStream();
                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            byte[] imgBuffer = new byte[1024 * 1024];
                            int pos = 0;
                            boolean eof = false;
                           // Keep listening to the InputStream while connected
                           while (!eof) {
                                try {
                                    int offset = bytes - 11;
                                    byte[] eofByte = new byte[11];
                                    eofByte = Arrays.copyOfRange(buffer, offset, bytes);
                                    String messagend = new String(eofByte, 0, 11);
                                    if(messagend.equals("end of image")) {
                                        eof = true;
                                    }
                                    else {
                                        bytes = driverInputStream.read(buffer);
                                        System.arraycopy(buffer,0,imgBuffer,pos,bytes);
                                        pos += bytes;
                                    }
                                } catch (IOException e) {
                                    break;
                                }
                            }
                            if(eof==true){
                                byte[] readBuf = (byte[]) imgBuffer;
                                Bitmap bmp = BitmapFactory.decodeByteArray(readBuf, 0,imgBuffer[0]);
                                recieved_img.setImageBitmap(bmp);
                            }

                        } catch (IOException e) {
                        }     }
                                            };
                        Thread thread = new Thread(runnable);
                        thread.start();
                        popup2.show();
                                             }catch(Exception exp){
                        System.out.println(exp.fillInStackTrace());
                    }

                                    }
                                    catch (Exception g) {
                                        chat_messages.add("Can't connect to the chosen device ");
                                        chat_adapter.notifyDataSetChanged();
                                    }
                                }});



                } catch (Exception e) {
                       e.printStackTrace();
                   }
                }
            }
             else
                {
                    tun_on_bluetooth();
                }
             chat_adapter.notifyDataSetChanged(); */
            }

  else if (command.toLowerCase().contains("generate qr code for")) {
            chat_messages.add(command);
            String s[]=command.split("generate qr code for",2);
            if(s[1].isEmpty())
                chat_messages.add("please specify value to be converted to Qr code");
            else{
                QRGEncoder qrgEncoder = new QRGEncoder(s[1], null, QRGContents.Type.TEXT, 1000);
                popup.setContentView(R.layout.qrcode_layout);
                popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Button save = popup.findViewById(R.id.btn_save_to_gallerry);
                ImageView qrimage = popup.findViewById(R.id.qrcode_image);
                popup.show();
                try {
                    // Getting QR-Code as Bitmap
                  Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                    // Setting Bitmap to ImageView
                    qrimage.setImageBitmap(bitmap);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                            }
                            else{
                                QRGSaver qrgSaver = new QRGSaver();
                                String path= getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
                                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, " Qr code generated for value "+ s[1] , "qr code");
                                Toast.makeText(getApplicationContext(),"qr code saved successfully",Toast.LENGTH_SHORT).show();
                                popup.dismiss();
                            }

                        }
                    });
                } catch (Exception e) {
                   // e.printStackTrace();
                }
                chat_messages.add("qr code generated");
           }
             chat_adapter.notifyDataSetChanged();
            }


  else if (command.toLowerCase().contains("read qr code")) {
            chat_messages.add(command);
              //  QRGEncoder qrgEncoder = new QRGEncoder(s[1], null, QRGContents.Type.TEXT, 1000);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
                try {
                    popup.setContentView(R.layout.qrcode_read);
                    popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    SurfaceView camera_view = (SurfaceView) popup.findViewById(R.id.surfaceView);
                    TextView result_qr=(TextView) popup.findViewById(R.id.result_qrcode);
                    result_qr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!result_qr.getText().toString().equals("No Qr detected")){
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label",result_qr.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getApplicationContext(),"saved to clipboard",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    QREader qrEader = new QREader.Builder(this, camera_view, new QRDataListener() {
                        @Override
                        public void onDetected(final String data) {
                           // Log.d("QREader", "Value : " + data);
                            result_qr.post(new Runnable() {
                                @Override
                                public void run() {
                                    result_qr.setText(data);
                                }
                            });
                        }
                    }).facing(QREader.BACK_CAM)
                            .enableAutofocus(true)
                            .height(camera_view.getHeight())
                            .width(camera_view.getWidth())
                            .build();
                    qrEader.start();
                    qrEader.initAndStart(camera_view);
                    popup.show();

                    chat_messages.add("searching for qr code");
                } catch (Exception e) {
                }
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 33);
                chat_messages.add("Please accept permission to complete your request");
            }
             chat_adapter.notifyDataSetChanged();
            }

        else if(chat_messages.get(chat_messages.size()-1)=="oh, That's not a command! How should i reply?"&&chat_messages.get(chat_messages.size()-1)!="Thanks for letting me know."){
            chat_messages.add(command);
            info.put(chat_messages.get(chat_messages.size()-3), command);
            chat_messages.add("Thanks for letting me know.");
        }

        else if(command.toLowerCase().contains("connect to car robot")){
            chat_messages.add(command);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    try
                    {
                        if (btSocket == null || !isBtConnected)
                        { try {
                            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

                            if(uuids != null) {

                                    // Toast.makeText(getApplicationContext(), "uuid is "+uuid.getUuid().toString(), Toast.LENGTH_SHORT).show();
                                    myUUID=uuids[2].getUuid();

                            }else{
                                //  Toast.makeText(getApplicationContext(), "error in uuid", Toast.LENGTH_SHORT).show();
                            }

                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                            String address="00:19:10:09:0F:1F";
                            bluetooth.startDiscovery();
                            BluetoothDevice device = bluetooth.getRemoteDevice(address);
                            bluetooth.cancelDiscovery();

                            //  Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();

                            btSocket = device.createRfcommSocketToServiceRecord(myUUID);
                            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                            btSocket = (BluetoothSocket) m.invoke(device, 1);

                            // btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                            btSocket.connect();//start connection
                            chat_messages.add("connected to car robot successfully");
                        }
                    }
                    catch (IOException | NoSuchMethodException e )
                    {
                        // Toast.makeText(getApplicationContext(), "error in connect", Toast.LENGTH_SHORT).show();
                        chat_messages.add("failed to connect to car robot");
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        else if(command.toLowerCase().contains("turn on car robot following")){
            chat_messages.add(command);

            chat_messages.add("car robot following turned on successfully");
        }
        else if(command.toLowerCase().contains("car robot remote control")){
            chat_messages.add(command);
            if (btSocket!=null)
            {      popup2.setContentView(R.layout.remote_control_layout);

            Button btn_security_mode=popup2.findViewById(R.id.btn_secButton);
            Button btn_flash=popup2.findViewById(R.id.btn_follow_me);
            Button btn_Arm=popup2.findViewById(R.id.btn_arm_state);
            Button btn_security=popup2.findViewById(R.id.btn_secButton);
            Button btn_follow=popup2.findViewById(R.id.btn_follow_me);
            TextView txt_security=popup2.findViewById(R.id.txt_sStatus);
            TextView txt_follow=popup2.findViewById(R.id.txt_follow_me);
            btn_Arm.setText("CLOSE ARM");

            btn_follow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (follow_me){
                        follow_me=false;
                        txt_follow.setText("OFF");
                        try {
                            btSocket.getOutputStream().write("Z".toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(!follow_me){
                        follow_me=true;
                        txt_follow.setText("ON");
                        try {
                            btSocket.getOutputStream().write("V".toString().getBytes());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
         btn_security.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (sec_tun){
                     sec_tun=false;
                     txt_security.setText("OFF");
                     try {
                         btSocket.getOutputStream().write("Z".toString().getBytes());
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
                 else if(!sec_tun){
                     sec_tun=true;
                     txt_security.setText("ON");
                     try {
                         btSocket.getOutputStream().write("k".toString().getBytes());

                         try {
                             socketInputStream = btSocket.getInputStream();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         listforsecurity();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }


                 }
             }
         });



            btn_Arm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arm_open){
                        arm_open=false;
                        btn_Arm.setText("OPEN ARM");
                        try {
                            btSocket.getOutputStream().write("C".toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        arm_open=true;
                        btn_Arm.setText("CLOSE ARM");
                        try {
                            btSocket.getOutputStream().write("O".toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            SeekBar speed_bar=popup2.findViewById(R.id.Speedbar);
            textView5 = (TextView)popup2.findViewById(R.id.textView9);

            layout_joystick = (RelativeLayout)popup2.findViewById(R.id.layout_joystick);

            js = new joystick(getApplicationContext(), layout_joystick, R.drawable.bot_icon);
            js.setStickSize(150, 150);
            js.setLayoutSize(500, 500);
            js.setLayoutAlpha(150);
            js.setStickAlpha(100);
            js.setOffset(90);
            js.setMinimumDistance(50);

            layout_joystick.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    js.drawStick(arg1);
                    if(arg1.getAction() == MotionEvent.ACTION_DOWN
                            || arg1.getAction() == MotionEvent.ACTION_MOVE) {


                        int direction = js.get8Direction();
                        if(direction == joystick.STICK_UP) {
                            textView5.setText("Direction : Up");
                            try {
                                btSocket.getOutputStream().write("F".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_UPRIGHT) {
                            textView5.setText("Direction : Up Right");
                            try {
                                btSocket.getOutputStream().write("I".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_RIGHT) {
                            textView5.setText("Direction : Right");
                            try {
                                btSocket.getOutputStream().write("R".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_DOWNRIGHT) {
                            textView5.setText("Direction : Down Right");
                            try {
                                btSocket.getOutputStream().write("J".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_DOWN) {
                            textView5.setText("Direction : Down");
                            try {
                                btSocket.getOutputStream().write("B".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_DOWNLEFT) {
                            textView5.setText("Direction : Down Left");
                            try {
                                btSocket.getOutputStream().write("H".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_LEFT) {
                            textView5.setText("Direction : Left");
                            try {
                                btSocket.getOutputStream().write("L".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_UPLEFT) {
                            textView5.setText("Direction : Up Left");
                            try {
                                btSocket.getOutputStream().write("G".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(direction == joystick.STICK_NONE) {
                            textView5.setText("Direction : Center");
                            try {
                                btSocket.getOutputStream().write("S".toString().getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if(arg1.getAction() == MotionEvent.ACTION_UP) {

                        textView5.setText("Direction :");
                        try {
                            btSocket.getOutputStream().write("S".toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });

            layout_joystick_arm= (RelativeLayout)popup2.findViewById(R.id.layout_joystick2);

            js2 = new joystick(getApplicationContext(), layout_joystick_arm, R.drawable.bot_icon);
            js2.setStickSize(150, 150);
            js2.setLayoutSize(500, 500);
            js2.setLayoutAlpha(150);
            js2.setStickAlpha(100);
            js2.setOffset(90);
            js2.setMinimumDistance(50);

            layout_joystick_arm.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    js2.drawStick(arg1);
                    if(arg1.getAction() == MotionEvent.ACTION_DOWN
                            || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                        int direction2 = js.get8Direction();
                        float direction = js2.getAngle();
                        double rotate_angle=(direction/360.0)*180.0;
                        long rotate_angle_angle= Math.round(rotate_angle);
                        String rotate_string=rotate_angle_angle+"";
                        if(rotate_string.trim().length()!=3)
                            rotate_string="0"+rotate_string;
                         angles="a"+rotate_string;
                        System.out.println("direction is:"+rotate_angle_angle);

                        try {
                            btSocket.getOutputStream().write(angles.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                         if(direction2 == joystick.STICK_NONE) {

                        }

                    }
                    else if(arg1.getAction() == MotionEvent.ACTION_UP) {

                       /* try {
                           btSocket.getOutputStream().write(angles.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    return true;
                }
            });

                speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    String progressChangedValue ;

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        String progress_s=progress+"";
                        if(progress_s.trim().length()!=3)
                            progress_s="0"+progress_s;
                        progressChangedValue = "s"+progress_s;
                        try {
                            btSocket.getOutputStream().write(progressChangedValue.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            popup2.show();

            chat_messages.add("on it....");
            }
            else{
                chat_messages.add("Please connect to car robot");
            }
        }
        else if(command.toLowerCase().contains("disconnect car robot")){
            chat_messages.add(command);
             btSocketDisconnect();
            chat_messages.add("car robot disconnected");
        }

        else if(command.toLowerCase().contains("eb3t")){
            chat_messages.add(command);
            String s[]=command.split("eb3t ",2);
            if (btSocket!=null)
            {
                try
                {
                    btSocket.getOutputStream().write(s[1].trim().toString().getBytes());
                    chat_messages.add("eb3t 3zma");
                }
                catch (IOException e)
                {
                    chat_messages.add("eb3t failed");
                }
            }
        }

        else {
            chat_messages.add(command);
            info_answer(command);
            }

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(chat_adapter.getItemCount() - 1);
            }
        });
        message.setText("");

 }

public  void directions(){

}

    public void info_answer(String command) {
        Set<String> keys = info.keySet();
        for (String key : keys){
            String lowerKey = key.toLowerCase();
            String lowerQuestion = command.toLowerCase();
            if (lowerKey.contains(lowerQuestion)) {
                chat_messages.add(info.get(key));
                return;
            }
        }
        chat_messages.add("oh, That's not a command! How should i reply?");
    }


@SuppressLint("MissingPermission")
private void tun_on_bluetooth(){
    try {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn,REQUEST_CODE_ENABLE_BT);
            chat_messages.add("press allow to complete your request");

    }
    catch (Exception e) {

    }
}
    @SuppressLint("Range")
 public void readcontacts(){
        if(names.isEmpty()&&phones.isEmpty()){
            Cursor contacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
            while (contacts.moveToNext())
            {
              names.add(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                phones.add(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            contacts.close();
    }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                if(requestCode==REQUEST_CODE_ENABLE_BT&&requestCode==RESULT_OK) {

                }
                 else if(requestCode==Result_load_image){
                        try{
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = { MediaStore.Images.Media.DATA };
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();
                          Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
                            pair_device=new ArrayList<>(pairedDevices);
                            System.out.println(pair_device.get(0).getName());
                            popup.setContentView(R.layout.sendto_device_list);
                            popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ListView sendto_devices = popup.findViewById(R.id.send_devices_list);
                            sendto_devices.setAdapter(send_adapter);
                            popup.show();
                            popup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(final DialogInterface arg0) {

                                    try {

                                        bluetooth.startDiscovery();
                                        BluetoothDevice device = bluetooth.getRemoteDevice(chosen_send_device.getAddress());
                                        bluetooth.cancelDiscovery();
                                     //   BluetoothAdapter a2=BluetoothAdapter.getDefaultAdapter();
                                   //     a2.startDiscovery();
                                 //       BluetoothDevice mydevice = a2.getRemoteDevice("f4:60:e2:bd:9d:8b");
                                 //       a2.cancelDiscovery();
                                       Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                        bs = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
                                        bs.connect();

                                        Runnable runnable = new Runnable(){
                                            public void run() {
                                                try {

                                                    Bitmap img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                img.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                byte[] b = stream.toByteArray();
                                                img.recycle();
                                                //Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
                                                    final OutputStream driverOutputStream = bs.getOutputStream();
                                                    driverOutputStream.write(b);
                                                     driverOutputStream.write("end of image".getBytes());
                                                } catch (IOException e) {
                                                }
                                            }
                                        };
                                        Thread thread = new Thread(runnable);
                                        thread.start();
                                        chat_messages.add("image sent to "+device.getName());
                                        chat_adapter.notifyDataSetChanged();
                                    }
                                    catch (Exception g) {
                                        chat_messages.add("Can't connect to the chosen device ");
                                        chat_adapter.notifyDataSetChanged();
                                    }
                                }});
                        }catch (Exception e){
                            //
                            //  Toast.makeText(getApplicationContext(), "unpairing failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                 else  if(requestCode==REQUEST_IMAGE_CAPTURE){
                  //  Intent i = new Intent(MainActivity.this,QrCodeActivity.class);
                  //  startActivityForResult( i,REQUEST_CODE_QR_SCAN);

                }

                }

    private void listforsecurity(){
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                if(btSocket!=null&&sec_tun){


                    byte[] buffer = new byte[256];
                    int bytes;
                    try {
                        bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                        String readMessage = new String(buffer, 0, bytes);
                        // Send the obtained bytes to the UI Activity via handler
                        Log.i("loggingghghghghg", readMessage + "");
                        if(readMessage.trim().equals("D")){
                            Log.i("sh8aaaaaaaaaaaaaaaaaaaaaaaaaaaal","");
                            notificationManager.notify(notificationId, builder.build());
                        }
                    } catch (IOException e) {

                    }
                    handler.postDelayed(runnable, delay);
                }
            }
        }, delay);

    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ASSISTANT";
            String description = "ASSISTANT";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Assistant", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(MainActivity.Receiver);
    }
    @Override
    public void onResume(){



        super.onResume();
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(chat_adapter.getItemCount() - 1);
            }
        });

    }
    @Override
    public void onPause(){
        handler.removeCallbacks(runnable);
        super.onPause();
        popup.dismiss();
    }



    private void btSocketDisconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close();
                btSocket=null;//close connection
            }
            catch (IOException e)
            {

            }
        }
    }
 /*   private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    public void step_count_test(){
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent!= null){
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude -MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        stepCount++;
                    }
                    System.out.println(stepCount.toString());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public class RepeatListener implements OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final OnClickListener clickListener;
        private View touchedView;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if(touchedView.isEnabled()) {
                    handler.postDelayed(this, normalInterval);
                    clickListener.onClick(touchedView);
                } else {
                    // if the view was disabled by the clickListener, remove the callback
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                }
            }
        };


        public RepeatListener(int initialInterval, int normalInterval,
                              OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    touchedView = view;
                    touchedView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    touchedView.setPressed(false);
                    touchedView = null;
                    return true;
            }

            return false;
        }

    }
*/
}
