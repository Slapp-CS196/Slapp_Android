package xyz.slapp.slapp_android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = "slapp";
    private static final String PATH = "/slapp/time";
    private static final String TIME_KEY = "slapp_time";

    public DataLayerListenerService() {
        System.out.println("DataLayerListenerService initialized!");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        System.out.println("onDataChanged");
        if(Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = client.blockingConnect(20, TimeUnit.SECONDS);

        if(!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient");
            return;
        }

        for(DataEvent event : events){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                String path = event.getDataItem().getUri().getPath();
                if(path.equals(PATH)) {
                    long time = DataMapItem.fromDataItem(event.getDataItem()).getDataMap().getLong(TIME_KEY);
                    System.out.println("Time received: " + time);
                }
            }
        }
    }
}
