package com.example.admin.thunderbolt;

/**
 * Created by NikhilNamjoshi on 10/20/2016.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
import android.provider.Settings;
//import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import java.util.UUID;

import java.util.Set;

import static android.R.attr.action;
import static android.content.ContentValues.TAG;



//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.util.Log;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.util.UUID;
//
//import static android.content.ContentValues.TAG;
//import static android.graphics.Color.BLACK;
import static android.graphics.Color.RED;
//import static android.provider.Telephony.Carriers.NAME;


public class SecondActivity extends AppCompatActivity {

    String VarId = "";
    String address = "";
    private TextView txview;
    private Button mapButton;
    static boolean flag = false;
    BluetoothAdapter btAdapter;
    BluetoothDevice ConnectingDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.d(TAG,"In Second Activity");

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mReceiver, filter1);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter2);

        mapButton = (Button)findViewById(R.id.Map);
        mapButton.setVisibility(View.INVISIBLE);

        txview = (TextView) findViewById(R.id.DeviceNameID);
        VarId = "HC-05";
        address = "20:16:01:06:54:31";
        Log.d(TAG, ""+VarId);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice btd : pairedDevices) {
            if(btd.getAddress().toString().equals(address)){
                ConnectingDevice = btd;
                break;
            }
        }

        Log.d(TAG, "Starting Service");
        try {
            Intent service = new Intent(SecondActivity.this, MyService.class);

            service.putExtra("Device",ConnectingDevice);
            startService(service);
            txview.setText("Disconnected. Please go back and Connect");

        }
        catch(Exception e) {
            Log.d(TAG, "" + e);
        }
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(SecondActivity.this, MapsActivity.class);
                startActivity(map);
        }
    });

    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                txview.setText("Connected To Thunderbolt");
                mapButton.setVisibility(View.VISIBLE);

            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                mapButton.setVisibility(View.INVISIBLE);
                txview.setTextColor(RED);
                txview.setText("Connection Lost. Please Go Back and Connect Again");
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                Toast.makeText(context, "Bluetooth Disconnected. Please connect again", Toast.LENGTH_LONG).show();
            }

            if(!chkStatus())
                createNetErrorDialog();

        }
    };

    boolean chkStatus() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);

                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                SecondActivity.this.finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();


    }



}


