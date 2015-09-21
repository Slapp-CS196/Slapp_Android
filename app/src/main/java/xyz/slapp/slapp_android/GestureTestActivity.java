package xyz.slapp.slapp_android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GestureTestActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor_linearAcc;

    TextView tv_gestureTest_display1;
    TextView tv_gestureTest_display2;
    TextView tv_gestureTest_display3;
    TextView tv_gestureTest_display4;
    TextView tv_gestureTest_display5;
    TextView tv_gestureTest_display6;
    TextView tv_gestureTest_display7;

    Button btn_gestureTest_reset;

    float x = 0;
    float y = 0;
    float z = 0;
    int slaps = 0;
    boolean slapActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_test);

        tv_gestureTest_display1 = (TextView)findViewById(R.id.tv_gestureTest_display1);
        tv_gestureTest_display2 = (TextView)findViewById(R.id.tv_gestureTest_display2);
        tv_gestureTest_display3 = (TextView)findViewById(R.id.tv_gestureTest_display3);
        tv_gestureTest_display4 = (TextView)findViewById(R.id.tv_gestureTest_display4);
        tv_gestureTest_display5 = (TextView)findViewById(R.id.tv_gestureTest_display5);
        tv_gestureTest_display6 = (TextView)findViewById(R.id.tv_gestureTest_display6);
        tv_gestureTest_display7 = (TextView)findViewById(R.id.tv_gestureTest_display7);

        btn_gestureTest_reset = (Button)findViewById(R.id.btn_gestureTest_reset);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor_linearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor_linearAcc, SensorManager.SENSOR_DELAY_NORMAL);

        btn_gestureTest_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = y = z = 0;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gesture_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_gestureTest_display1.setText("X: " + event.values[0]);
        tv_gestureTest_display2.setText("Y: " + event.values[1]);
        tv_gestureTest_display3.setText("Z: " + event.values[2]);

        x = (Math.abs(x) > Math.abs(event.values[0]))?x:event.values[0];
        y = (Math.abs(y) > Math.abs(event.values[1]))?y:event.values[1];
        z = (Math.abs(z) > Math.abs(event.values[2]))?z:event.values[2];
        tv_gestureTest_display4.setText("Xmax: " + x);
        tv_gestureTest_display5.setText("Ymax: " + y);
        tv_gestureTest_display6.setText("Zmax: " + z);

        if (Math.abs(z) > 10.0) {
            if (!slapActive) {
                slaps++;
                slapActive = true;
            }
        } else if (slapActive) {
            slapActive = false;
        }
        tv_gestureTest_display7.setText("Slaps: " + slaps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor_linearAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
