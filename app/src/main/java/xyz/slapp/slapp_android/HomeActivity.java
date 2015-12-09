package xyz.slapp.slapp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeActivity extends AppCompatActivity {

    private String emailAddress;
    private TextView tvActiveProfile;
    private ListView lvProfiles;
    private Button btnToggleService;
    private ArrayList<Integer> profileIds;
    private ArrayList<String> profileNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvActiveProfile = (TextView)findViewById(R.id.home_tvActiveProfile);
        lvProfiles = (ListView)findViewById(R.id.home_lvProfiles);
        btnToggleService = (Button)findViewById(R.id.home_btnToggleService);

        profileIds = new ArrayList<>(5);
        profileNames = new ArrayList<>(5);

        emailAddress = getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).getString(Global.SHARED_PREF_EMAIL_KEY,"");

        tvActiveProfile.setText(getString(R.string.home_active_profile_loading));

        getProfiles();

        lvProfiles.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_profile, R.id.list_item_profile_textview, profileNames));
        lvProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    private void getProfiles() {
        profileIds.clear();
        profileNames.clear();

        Call<ResponseBody> getActiveProfileCall = Global.getInstance().getSlappService().getActiveProfileName(emailAddress);
        getActiveProfileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null) {
                        String responseString = response.body().string();
                        if (responseString.equals("-1")) {
                            tvActiveProfile.setText(getString(R.string.home_active_profile_none));
                        } else {
                            tvActiveProfile.setText(getString(R.string.home_active_profile_base) + " " + responseString);
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

        Call<ResponseBody> getProfilesCall = Global.getInstance().getSlappService().getUserProfiles(emailAddress);
        getProfilesCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null) {
                        String responseString = response.body().string();
                        while (responseString.contains(",")) {
                            profileIds.add(Integer.parseInt(responseString.substring(0,responseString.indexOf(","))));
                            responseString = responseString.substring(responseString.indexOf(",")+1);
                        }
                        profileIds.add(Integer.parseInt(responseString));
                        for (int profileId : profileIds) {
                            Call<ResponseBody> getProfileNameCall = Global.getInstance().getSlappService().getProfileName(profileId);
                            getProfileNameCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                    try {
                                        if (response.body() != null) {
                                            profileNames.add(response.body().string());
                                            ((ArrayAdapter)lvProfiles.getAdapter()).notifyDataSetChanged();
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
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void homeOnButtonClick(View v) {
        if (v.getId() == R.id.home_btnAddProfile) {
            startActivity(new Intent(this, AddProfileActivity.class));
        } else if (v.getId() == R.id.home_btnToggleService) {
            if (!Global.getInstance().getServiceRunning()) {
                startService(new Intent(HomeActivity.this, BackgroundService.class));
                btnToggleService.setText(getString(R.string.home_toggle_stop));
                Global.getInstance().setServiceRunning(true);
                Log.w("Slapp","Service started");
            } else {
                stopService(new Intent(HomeActivity.this, BackgroundService.class));
                btnToggleService.setText(getString(R.string.home_toggle_start));
                Global.getInstance().setServiceRunning(false);
                Log.w("Slapp", "Service stopped");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            getApplicationContext().getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).edit().putBoolean(Global.SHARED_PREF_LOGGED_IN_KEY, false).commit();
            stopService(new Intent(HomeActivity.this, BackgroundService.class));
            Global.getInstance().setServiceRunning(false);
            Log.w("Slapp", "Service stopped");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    private class Profile {
        String name;
        int id;

        Profile(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getProfiles();
    }
}
