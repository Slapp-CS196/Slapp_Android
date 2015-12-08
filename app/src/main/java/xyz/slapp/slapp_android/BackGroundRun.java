package xyz.slapp.slapp_android;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BackGroundRun extends Service implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
,DataApi.DataListener{
    GoogleApiClient googleClient;
    GoogleApiClient messageApi;
    Location currentPosition;
    private static final String wearName = "cs196slapp.slapp";
    private long time = 0;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public static int slaps = 0;
    boolean slapActive = false;
    public SensorManager sensorManager;
    public static boolean status = false;
    Retrofit retrofit;

    SlappService slappService;

    protected synchronized void googleApiBuilder() {
        //Google API connection
        googleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        messageApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }



    public BackGroundRun() {
    }
    public static int numberOfSlaps(){
        return slaps;
    }
    @Override
    public void onCreate(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.slapp.xyz")
                .build();
        slappService = retrofit.create(SlappService.class);
        googleApiBuilder();
        googleClient.connect();
        //sensor manager starter
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorLinearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensorLinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //this code allows the service to run in background
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //gesture recognition code
        x = (Math.abs(x) > Math.abs(event.values[0]))?x:event.values[0];
        y = (Math.abs(y) > Math.abs(event.values[1]))?y:event.values[1];
        z = (Math.abs(z) > Math.abs(event.values[2]))?z:event.values[2];

        //Y
        if (event.values[2] > 9.0 && event.values[0]<5) {
            if (!slapActive) {
                googleApiBuilder();
                slaps++;
                slapActive = true;
                sendTestSlapp(System.currentTimeMillis());
            }
        } else if (slapActive) {
            slapActive = false;
        }
        //change the text in main activity
        MainActivity.getSlapCount(slaps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onDestroy(){
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    public void sendTestSlapp(long time) {
        String userId = "JOHN CENA";
        googleClient.connect();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.slapp.xyz")
                .build();
        slappService = retrofit.create(SlappService.class);

        Call<ResponseBody> call = slappService.sendSlapp(userId, time, 10.0, 10.0, 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null) {
                        Log.w("Slapps", response.body().string());

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.w("Slapp", t.getMessage());
            }
        });
//        SlappDbHelper slappDbHelper = new SlappDbHelper(BackGroundRun.this);
//        slappDbHelper.addSlapp(userId, System.currentTimeMillis(), Math.random() * 100 - 50, Math.random() * 100 - 50, 50);
//        Log.w("Slapp", slappDbHelper.fetchAllSubmissions().getCount() + " Slapps in local DB");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        currentPosition = LocationServices.FusedLocationApi.getLastLocation(googleClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for(DataEvent event: dataEventBuffer){
            if(event.getType()==DataEvent.TYPE_CHANGED){
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/time")==0){
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    time = dataMap.getLong(wearName);
                    sendTestSlapp(time);
                    System.out.println("this is the time we got: " + time);
                    slaps++;
                }
            }
        }
    }
}