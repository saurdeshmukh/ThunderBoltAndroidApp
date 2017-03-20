package com.example.admin.thunderbolt;

import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private  BluetoothAdapter btAdapter;
    public TextView StatusUpdate;
    public Button connect;
    public Button disconnect;
    public Button Devices;
    ArrayAdapter<String> mAdapter;
    int REQUEST_CODE=1;
    //Bundle bn;
    //String pList[];
    //Set<BluetoothDevice> pairedDevices;
    //protected static final int DISCOVERY_REQUEST=1;
    BroadcastReceiver blueToothState=new BroadcastReceiver() {
        Intent intent1=new Intent();
        @Override
        public void onReceive(Context context, Intent intent) {
            String prvStateExtra=BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra=BluetoothAdapter.EXTRA_STATE;
            int state=intent.getIntExtra(stateExtra,-1);
            int previousExtra=intent.getIntExtra(prvStateExtra,-1);
            String toastText="";

            switch(state){
                case(BluetoothAdapter.STATE_TURNING_ON):
                {
                    toastText="Bluetooth Turning On";
                    Toast.makeText(MainActivity.this,toastText,Toast.LENGTH_SHORT).show();
                    break;
                }
                case(BluetoothAdapter.STATE_ON):
                {
                    toastText="Bluetooth On";
                    Toast.makeText(MainActivity.this,toastText,Toast.LENGTH_SHORT).show();
                    setupUI();
                    break;
                }
                case(BluetoothAdapter.STATE_TURNING_OFF):
                {

                    toastText="Bluetooth tuning Off";
                    Toast.makeText(MainActivity.this,toastText,Toast.LENGTH_SHORT).show();
                    break;
                }
                case(BluetoothAdapter.STATE_OFF):
                {
                    toastText="Bluetooth Off";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    setupUI();
                    break;
                }
            }
        }




    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }
    private void setupUI()
    {
        final TextView StatusUpdate=(TextView) findViewById(R.id.status);
        final Button connect=(Button) findViewById(R.id.buttonConnect);
        final Button disconnect=(Button)findViewById(R.id.buttonDisconnect);
        final Button Devices=(Button)findViewById(R.id.Devices);
        final TextView txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtInfo.setText("   !!!ThunderBolt Welcomes You!!!   " +
                "Make sure you are connected to the internet. " +
                "Click below button to connect to Thunderbolt.");
        disconnect.setVisibility(View.GONE);
        Devices.setVisibility(View.GONE);
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        if(btAdapter.isEnabled())
        {
            String address=btAdapter.getAddress();
            String name=btAdapter.getName();
            //String statusText=name+":"+address;
            //StatusUpdate.setText(statusText);
            disconnect.setVisibility(View.VISIBLE);
            connect.setVisibility(View.GONE);
            Devices.setVisibility(View.VISIBLE);

        }
        else {
            StatusUpdate.setText("Please turn on Bluetooth");
            connect.setVisibility(View.VISIBLE);
        }
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                IntentFilter inFt = new IntentFilter(actionStateChanged);
                registerReceiver(blueToothState, inFt);
                startActivityForResult(new Intent(actionRequestEnable), REQUEST_CODE);
                StatusUpdate.setText("Bluetooth Active");
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAdapter.disable();
                connect.setVisibility(View.VISIBLE);
                disconnect.setVisibility(View.GONE);
                StatusUpdate.setText("Please turn on Bluetooth");
                Devices.setVisibility(View.GONE);
            }
        });
        Devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent second = new Intent(MainActivity.this, SecondActivity.class);
                //second.putExtras(bn);
                startActivity(second);
            }
        });
    }

}
