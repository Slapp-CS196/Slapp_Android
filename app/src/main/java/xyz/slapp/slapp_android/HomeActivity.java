package xyz.slapp.slapp_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeActivity extends AppCompatActivity {

    private String emailAddress;
    private TextView activeProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        activeProfile = (TextView)findViewById(R.id.home_tvActiveProfile);

        emailAddress = getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).getString(Global.SHARED_PREF_EMAIL_KEY,"");

        activeProfile.setText(getString(R.string.home_active_profile_loading));

        Call<ResponseBody> call = Global.getInstance().getSlappService().getActiveProfile(emailAddress);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null) {
                        if (response.body().string().equals("-1")) {
                            activeProfile.setText(getString(R.string.home_active_profile_none));
                        } else {
                            activeProfile.setText(getString(R.string.home_active_profile_base) + response.body().string());
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
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void homeOnButtonClick(View v) {
        if (v.getId() == R.id.home_btnAddProfile) {
            startActivity(new Intent(this, AddProfileActivity.class));
        }
    }
}
