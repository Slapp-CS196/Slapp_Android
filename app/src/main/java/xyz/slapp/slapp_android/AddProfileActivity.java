package xyz.slapp.slapp_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
    }

    public void addProfileOnButtonClick (View v) {
        if (v.getId() == R.id.add_profile_btnAddProfile) {

        }
    }
}
