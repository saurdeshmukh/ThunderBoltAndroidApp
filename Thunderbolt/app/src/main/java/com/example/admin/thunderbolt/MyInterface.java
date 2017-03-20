package com.example.admin.thunderbolt;

//import android.view.View;

/**
 * Created by Admin on 11/6/2016.
 */


public interface MyInterface {
    // you can define any parameter as per your requirement
     public void myMethod(String lat, String lon);
     public void distanceSpeed(String distance, String speed);
     public void compassReading(String compassReading);
     public void carDirection(String direction);
 }
