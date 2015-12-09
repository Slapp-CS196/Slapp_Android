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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BackgroundService extends Service implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    Location currentPosition;

    boolean slappActive = false;
    SensorManager sensorManager;
    public static boolean status = false;

    protected synchronized void googleApiBuilder() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[2] > 9.0 && event.values[0] < 5.0) {
            if (!slappActive) {
                googleApiBuilder();
                slappActive = true;
                sendSlapp(System.currentTimeMillis());
            }
        } else if (slappActive) {
            slappActive = false;
        }
    }

    private void sendSlapp(long time) {
        Log.w("Slapp", "Slapp!");
        Toast.makeText(this, "Slapp!", Toast.LENGTH_LONG).show();
        String emailAddress = getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).getString(Global.SHARED_PREF_EMAIL_KEY,"");
        Call<ResponseBody> sendSlappCall = Global.getInstance().getSlappService().sendSlapp(emailAddress, time, currentPosition.getLatitude(), currentPosition.getLongitude(), 500);
        sendSlappCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null) {
                        String responseBody = response.body().string();
                        if (responseBody.contains("added")) {
                            Toast.makeText(getApplicationContext(), "Slapp sent", Toast.LENGTH_SHORT).show();
                            Log.w("Slapp", "Slapp ID: " + responseBody.substring(5,responseBody.indexOf("added")));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        Log.e("Slapp", response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onCreate(){
        googleApiBuilder();
        googleApiClient.connect();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorLinearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensorLinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        currentPosition = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}
