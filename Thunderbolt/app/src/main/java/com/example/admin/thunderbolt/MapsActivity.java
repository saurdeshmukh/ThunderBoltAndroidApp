package com.example.admin.thunderbolt;

/**
 * Created by NikhilNamjoshi on 10/20/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface; //new
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MyInterface,
        LocationListener {

    private GoogleMap mMap;

    static Toast toast;
    static String tur = "";
    static Double distance = 0.0;
    static Double speed = 0.0;
    static Double compass = 0.0;
    ArrayList<LatLng> MarkerPoints = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker ;
    LocationRequest mLocationRequest;
    static LatLng latLng, lng;
    private static final String TAG = "HI";
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    static Context context ;
    MyService mService ;
    static TextView MapTextView;
    static TextView MapTextView1;
    static TextView MapTextView2;
    static String TextData = "Distance: ";
    static String TextData1 = "Speed: ";
    static String TextData2 = "Compass Angle: ";
    String LatLongString = "";
    LatLng dest;
    SecondActivity s1 = new SecondActivity();
//    static double l1 = 37.337354;
//    static double l2 = -121.882924;
    static double l1 = 0.0;
    static double l2 = 0.0;
    Bitmap mDotMarkerBitmap;
    boolean isStart = false;
    MarkerOptions options1;
    static Marker myMarker;
    static boolean currentLoc = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mService = new MyService();
        context = getApplicationContext();
        options1 = new MarkerOptions();
        int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
        mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = context.getDrawable(R.drawable.map_dot_red);
        shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
        shape.draw(canvas);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        startButton = (Button)findViewById(R.id.Temp);
        startButton.setVisibility(View.VISIBLE);

        stopButton = (Button)findViewById(R.id.Temp1);
        stopButton.setVisibility(View.VISIBLE);

        resetButton = (Button)findViewById(R.id.Temp2);
        resetButton.setVisibility(View.VISIBLE);

        stopButton.setEnabled(false);
        resetButton.setEnabled(true);
        startButton.setEnabled(true);





        // Initializing


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        s1.flag = true;



    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG,"MAP");



        if (!chkStatus()) {
            createNetErrorDialog();

        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(false);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }
        //Alert to turn on mobile data or wif


        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //here start
                // Already two locations
//                Marker marker = mMap.addMarker(new MarkerOptions()
//                        .position(point)
//                        .anchor(.5f, .5f)
//                        .icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap)));
//                MapTextView = (TextView)findViewById(R.id.MapTextView);
//                String a = "" + MarkerPoints.size();
//                MapTextView.setText(a);
                if(latLng != null && isStart == false) {
                    if (MarkerPoints.size() > 0) {
                        MarkerPoints.clear();
                        mMap.clear();
                    }

                    // Adding new item to the ArrayList
                    // Creating MarkerOptions

                    latLng = new LatLng(l1, l2);
                    MarkerOptions options = new MarkerOptions();
                    MarkerPoints.add(latLng);
                    options.position(latLng);
                    Log.d(TAG,"START: "+latLng.latitude+" "+latLng.longitude);
                    options.title("Source");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(options);
                    //Log.d(TAG,"START: "+point.latitude+" "+point.longitude);


                    //Set source marker each time you select different destination
                    LatLongString = "";
                    // Setting the position of the marker


                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                    if (MarkerPoints.size() == 1) {
                        // options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        //} else if (MarkerPoints.size() == 2)
                        options.position(point);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        options.title("Destination");
                        //mCurrLocationMarker = mMap.addMarker(options);
                    }


                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(options.draggable(true));

                    // Checks, whether start and end locations are captured
                    if (MarkerPoints.size() >= 1) {

                        //dest = MarkerPoints.get(0);
                        dest = point;


                        // Getting URL to the Google Directions API
                        String url = getUrl(latLng, dest);
                        Log.d("onMapClick", url.toString());
                        FetchUrl FetchUrl = new FetchUrl();

                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                        double distance = CalculationByDistance(latLng, dest);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                }
            }

        });

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                LatLongString = LatLongString + "B";
                mService.writeData(LatLongString);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
//                resetButton.setEnabled(true);
                LatLongString = "";
                isStart = true;
//                l1 = 37.338549;
//                l2 = -121.880374;


            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                String data = "E";
                stopButton.setEnabled(false);
//                resetButton.setEnabled(false);
                startButton.setEnabled(true);
                mService.writeData(data);
//                l1 = 37.339847;
//                l2 = -121.877669;

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String data = "R";
                mService.writeData(data);
                MarkerPoints.clear();
                mMap.clear();
                Intent intent = getIntent();
                finish();
                startActivity(intent);


            }
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                MapTextView = (TextView)findViewById(R.id.MapTextView);
                                MapTextView.setText(TextData);
                                MapTextView1 = (TextView)findViewById(R.id.MapTextView1);
                                MapTextView1.setText(TextData1);
                                MapTextView2 = (TextView)findViewById(R.id.MapTextView2);
                                MapTextView2.setText(TextData2);

                                if(l1 != 0.0 && l2 != 0.0) {
                                    lng = new LatLng(l1, l2);
                                    //Marker myMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0.0, 0.0)));
                                    //myMarker.remove();
                                    //MarkerPoints.add(lng);
                                    if(currentLoc == true && myMarker != null)
                                        myMarker.remove();
                                    options1.position(lng);
                                    options1.title("Current");
                                    options1.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
                                    myMarker = mMap.addMarker(options1);
                                    currentLoc = false;



                                }



//                                if(tur == "L"){
//                                    toast = Toast.makeText(context, "Turning Left", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                    tur = "";
//                                }
//                                else if(tur == "R"){
//                                    toast = Toast.makeText(context, "Turning Right", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                    tur = "";
//                                }
//                                else if(tur == "S"){
//                                    toast = Toast.makeText(context, "Braking", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                    tur = "";
//                                }
//                                else if(tur == "F"){
//                                    toast = Toast.makeText(context, "Moving Forward", Toast.LENGTH_SHORT);
//                                    toast.show();
//                                    tur = "";
//                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }

    @Override
    public void myMethod(String lat, String lon)
    {

        try {
            MapsActivity.l1 = Double.valueOf(lat).doubleValue();
            MapsActivity.l2 = Double.valueOf(lon).doubleValue();
            currentLoc = true;
        }
        catch(Exception e)
        {
            Log.d(TAG,""+e);
        }

        Log.d(TAG, "L1: " + MapsActivity.l1);
        Log.d(TAG, "L2: " + MapsActivity.l2);

    }

    public void distanceSpeed(String distance, String speed)
    {
        this.speed = 0.0;
        Log.d(TAG, "Distance1: " + this.distance);
        Log.d(TAG, "Speed2: " + this.speed);
        TextData = "  Distance: ";
        TextData1 = "  Speed: ";

        if(s1.flag){

            try {
                this.distance = Double.valueOf(distance).doubleValue();
                this.speed = Double.valueOf(speed).doubleValue();
                TextData = TextData + this.distance + " meters";
                TextData1 = TextData1 + this.speed + " RPM";
            }
            catch(Exception e)
            {
                Log.d(TAG,""+e);
            }
            Log.d(TAG, "Distance1: " + this.distance);
            Log.d(TAG, "Speed2: " + this.speed);
        }
    }
    public void compassReading(String compassReading)
    {
        this.compass = 0.0;
        TextData2 = " Compass Angle: ";
        Log.d(TAG, "Compass Reading1: " + this.compass);
        if(s1.flag){
            try {

                this.compass = Double.valueOf(compassReading).doubleValue();
                Log.d(TAG, "Compass Reading2: " + this.compass);

                //if (this.distance != 0 && this.speed != 0) {
//                    TextData = "Distance: " + this.distance + " inches";
//                    TextData1 = "Speed: " + this.speed + " RPM";
                    TextData2 = TextData2 + this.compass + " degrees";
               // }
            }
            catch(Exception e){
                Log.d(TAG,""+e);
            }
        }
    }

    public void carDirection(String direction) {
        Log.d(TAG, "CarDirection" + direction);
        if (s1.flag) {

            for (int t = 0; t < direction.length(); t++) {
                if (direction.charAt(t) == 'L') {
                    Log.d(TAG, "Left");
                    tur = "L";
                    break;
                } else if (direction.charAt(t) == 'R') {
                    Log.d(TAG, "Right");
                    tur = "R";
                    break;
                } else if (direction.charAt(t) == 'S') {
                    Log.d(TAG, "Stop");
                    tur = "S";
                    break;
                } else if (direction.charAt(t) == 'F') {
                    Log.d(TAG, "Forward");
                    tur = "F";
                    break;
                }
            }

        }
    }

    //Calculates distance between two points
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        return RoundTo2Decimals(Radius * c);
    }

    double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }




    //If Wifi or mobile data is turned off, promptthe user to turn it on
    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                                MapsActivity.this.onPause();
                                MapsActivity.this.onResume();

                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MapsActivity.this.finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();


    }



    //Check status if wifi or mobile data is on
    private boolean chkStatus() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        //The mode parameter will set your maps to walking mode
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&mode=walking";


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            double prevLat, prevLong;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);


                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(position);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    points.add(position);
                    if(j != 0) {

                        HashMap<String, String> point1 = path.get(j - 1);
                        prevLat = Double.parseDouble(point1.get("lat"));
                        prevLong = Double.parseDouble(point1.get("lng"));
                        double newLat = (lat + prevLat)/2;
                        double newLoc = (lng + prevLong)/2;
                        double lat1 = (prevLat + newLat)/2;
                        double lon1 = (prevLong + newLoc)/2;
                        LatLng mockPos1 = new LatLng(lat1,lon1);
                        markerOptions.position(mockPos1);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mCurrLocationMarker = mMap.addMarker(markerOptions);
                        LatLongString = LatLongString + lat1 + "#" + lon1 + "$";
                        LatLng mockPos = new LatLng(newLat, newLoc);
                        LatLongString = LatLongString + newLat + "#" + newLoc + "$";
                        markerOptions.position(mockPos);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mCurrLocationMarker = mMap.addMarker(markerOptions);
                        double lat2 = (newLat + lat)/2;
                        double long2 = (newLoc + lng)/2;
                        LatLng mockPos2 = new LatLng(lat2,long2);
                        markerOptions.position(mockPos2);
                        LatLongString = LatLongString + lat2 + "#" + long2 + "$";
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mCurrLocationMarker = mMap.addMarker(markerOptions);
                        //points.add(mockPos);

                    }
                    //mockPos
                    LatLongString = LatLongString + lat + "#" + lng + "$";



                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Source");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

}
