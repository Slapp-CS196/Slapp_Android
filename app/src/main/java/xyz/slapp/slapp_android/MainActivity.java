package xyz.slapp.slapp_android;


        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.TextView;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationServices;
        import java.util.Calendar;
        import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleClient;
    TextView longitude, latitude, accuracy, altitude, time, date;
    Location currentPosition;
    int milliseconds, seconds, minutes, hours, day, month, year;

    //API gets build
    protected synchronized void googleApiBuilder() {
        //Google API connection
        googleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Getting location with GPS in case Google API fails
    private void getGpsLocation() {
        //Initialize Location Manager
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        //Find the best GPS Provider - Can be substituted with an easier code since most phones have 1 provider
        Criteria criteria = new Criteria();
        String gpsUsed = locationManager.getBestProvider(criteria, false);

        // Get Last Known Location
        currentPosition = locationManager.getLastKnownLocation(gpsUsed);

        // Opening a location listener to get a new last location
        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        locationManager.requestLocationUpdates(gpsUsed, 0, 0, locationListener);
        //Get location after last location has been updated
        currentPosition = locationManager.getLastKnownLocation(gpsUsed);
        try {
            longitude.setText(String.valueOf(currentPosition.getLongitude()));
            latitude.setText(String.valueOf(currentPosition.getLatitude()));
        }catch(NullPointerException e){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longitude = (TextView)findViewById(R.id.longitude);
        latitude = (TextView)findViewById(R.id.latitude);
        accuracy = (TextView)findViewById(R.id.accuracy);
        altitude = (TextView)findViewById(R.id.altitude);
        time = (TextView)findViewById(R.id.time);
        date = (TextView)findViewById(R.id.date);

        //Set up the connection to google API
        googleApiBuilder();
        Calendar currentTime = Calendar.getInstance();

        //Time set to GMT to get rid of possible confusions due to time zone
        currentTime.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

        //Getting Time Data to be send to database
        milliseconds = currentTime.get(Calendar.MILLISECOND);
        seconds = currentTime.get(Calendar.SECOND);
        minutes = currentTime.get(Calendar.MINUTE);
        hours = currentTime.get(Calendar.HOUR_OF_DAY);
        day = currentTime.get(Calendar.DATE);
        month = currentTime.get(Calendar.MONTH);
        year = currentTime.get(Calendar.YEAR);

        //Getting last known location
        googleClient.connect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Getting the last known location from Google API
    @Override
    public void onConnected(Bundle connectionHint) {
        //if connected is successful get the location and print it
        currentPosition = LocationServices.FusedLocationApi.getLastLocation(googleClient);
        if(currentPosition != null){
            longitude.setText(String.valueOf(currentPosition.getLongitude()));
            latitude.setText(String.valueOf(currentPosition.getLatitude()));
            accuracy.setText(String.valueOf(currentPosition.getAccuracy()));
            altitude.setText(String.valueOf(currentPosition.getAltitude()));
            time.setText(hours + ":" + minutes + ":" + seconds + ":" + milliseconds);
            date.setText(month + "." + day + "." + year);
        }
        else{
            // If network is not working attempt to get location through GPS. It is far less optimal and does not work well at some phones
            getGpsLocation();
        }
    }

    //Function that is there because Google API. Not used
    @Override
    public void onConnectionSuspended(int i) {

    }

    //Function that is there because Google API. Not used
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
