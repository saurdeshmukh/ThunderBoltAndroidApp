package com.example.admin.thunderbolt;

/**
 * Created by NikhilNamjoshi on 10/20/2016.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
//import android.provider.SyncStateContract;
import android.util.Log;
//import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.text.Normalizer;

//import android.os.Message;


import static android.content.ContentValues.TAG;
//import static java.sql.Types.NULL;

    public class ConnectedThread extends Thread{
    private static BluetoothSocket mmSocket;
    private static InputStream mmInStream;
    private static OutputStream mmOutStream;
    private Handler mHandler;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_READ = 2;
    static MapsActivity m1 = new MapsActivity();

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = new Handler();

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();

        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        // Keep listening to the InputStream until an exception occurs
        byte[] buffer = new byte[100];
        int begin = 0;
        int bytes = 0; // bytes returned from read()
        String latitude = "";
        String longitude = "";
        String speed = "";
        String distance = "";
        String compassReading = "";
        String direction = "";
        String temp = "";
        while (true) {
            try {

                bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                if(bytes > 0) {
                    String loc = new String(buffer);
                    for (int i = begin; i < bytes; i++) {
                        if (loc.charAt(i) == '#' ){

                            latitude = temp;
                            temp = "";
                        }
                        else if (loc.charAt(i) == '$') {
                            longitude = temp;
                            if (latitude != "" && longitude != "") {
                                m1.myMethod(latitude, longitude);
                            }
                            temp = "";
                        }
                        else if(loc.charAt(i) == '%') {
                            distance = temp;
                            temp = "";
                        }
                        else if(loc.charAt(i) == '^') {
                            speed = temp;
                            m1.distanceSpeed(distance, speed);
                            temp = "";
                        }
                        else if(loc.charAt(i) == '&'){
                            compassReading = temp;
                            m1.compassReading(compassReading);
                            temp = "";
                        }
                        else if(loc.charAt(i) == '*'){
                            direction = temp;
                            m1.carDirection(direction);
                            temp = "";
                        }
                        else
                        {
                            temp = temp + loc.charAt(i);
                        }
                        if (i == bytes - 1) {

                            bytes = 0;
                            begin = 0;
                        }
                    }

                }
            } catch (IOException e) {
                break;
            }

        }

    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            Log.d(TAG,"Inside Connected Thread");

            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG,""+e);
        }

    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
            mmInStream.close();
            mmOutStream.close();
        } catch (IOException e) { }
    }
}

