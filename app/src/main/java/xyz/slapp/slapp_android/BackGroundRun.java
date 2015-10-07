package xyz.slapp.slapp_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

public class BackGroundRun extends Service implements SensorEventListener {
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public static int slaps = 0;
    boolean slapActive = false;
    private SensorManager sensorManager;
    private Sensor sensorLinearAcc;
    public BackGroundRun() {
    }
    public static int numberOfSlaps(){
        return slaps;
    }
    @Override
    public void onCreate(){
        //sensor manager starter
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorLinearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener((SensorEventListener) this, sensorLinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }
    //this code allows the service to run in background
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
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

        if (event.values[2] > 9.0) {
            if (!slapActive) {
                slaps++;
                slapActive = true;
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
}
