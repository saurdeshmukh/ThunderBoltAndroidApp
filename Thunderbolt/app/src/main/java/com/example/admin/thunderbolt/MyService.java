package com.example.admin.thunderbolt;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
import android.os.IBinder;
//import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
//import android.content.Intent;
//import android.util.Log;
//import android.widget.TextView;
//import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.ContentValues.TAG;

//import static android.content.ContentValues.TAG;

/**
 * Created by NikhilNamjoshi on 10/21/2016.
 */

public class MyService extends Service {
    BluetoothDevice ConnectingDevice;
    private  static BluetoothSocket mmSocket;
    BluetoothAdapter mBluetoothAdapter;
    static ConnectedThread mConnectedThread;
    public MyService() {
            Log.d(TAG,"MyService Constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    @Override
    public void onStart(Intent intent, int startId) {
    //public void onHandleIntent(Intent intent) {
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        Log.d(TAG,"IN Service");
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        }
        ConnectingDevice = intent.getParcelableExtra("Device");
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            //tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("492515c1-5c1e-4e57-868c-5691b674411f"));
            //UUID idOne = UUID.randomUUID()

            mmSocket = ConnectingDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            //tmp = device.createInsecureRfcommSocketToServiceRecord(idOne);


        } catch (IOException e) {
            Log.d(TAG, ""+e);
        }
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            Log.d(TAG, "Connecting");
            mmSocket.connect();
            //ConnectedThread cn = new ConnectedThread(mmSocket);
            //cn.start();
            Toast.makeText(this, "Thunderbolt is online", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            Log.d(TAG, "Exception"+connectException);
            try {
                //mmSocket.close();
                mmSocket =(BluetoothSocket) ConnectingDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(ConnectingDevice,1);
                Log.d(TAG,"Connecting2");
                mmSocket.connect();
                Log.i(TAG,"Connected2");
                Toast.makeText(this, "Thunderbolt is online", Toast.LENGTH_LONG).show();

            }
            catch(NoSuchMethodException r)
            {
                Log.d(TAG, ""+r);
            }
            catch(IllegalAccessException i)
            {
                Log.d(TAG, ""+i);
            }
            catch(InvocationTargetException i)
            {
                Log.d(TAG, ""+i);
            }
            catch(IOException f)
            {
                Log.d(TAG, ""+f);
            }
            return;
        }
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

    }

    public void writeData(String data)
    {

        Double d = 45.0;
        Log.d(TAG,""+mmSocket);
        byte[] msgBuffer = data.getBytes();
        Log.d(TAG,"Inside WriteData");
        try {
            mConnectedThread.write(msgBuffer);
        }
        catch(Exception e)
        {
            Log.d(TAG,""+e);
        }

    }



    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        try
        {
            mmSocket.close();
        }
        catch(Exception  e)
        {
            Log.d(TAG,""+e);
        }

    }
}