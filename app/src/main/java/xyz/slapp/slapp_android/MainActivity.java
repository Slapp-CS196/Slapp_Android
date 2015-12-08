package xyz.slapp.slapp_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).getBoolean(Global.SHARED_PREF_LOGGED_IN_KEY, false)) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }



    public void mainOnButtonClick(View v) {
        if (v.getId() == R.id.main_btnSignUp) {
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (v.getId() == R.id.main_btnLogIn) {
            startActivity(new Intent(this, LogInActivity.class));
        }

    }
}
