package xyz.slapp.slapp_android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AddProfileActivity extends AppCompatActivity {

    private String emailAddress;
    private int profileId = -1;
    EditText etProfileName, etFirstNameLink, etLastNameLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        
        etProfileName = (EditText)findViewById(R.id.add_profile_etProfileName);
        etFirstNameLink = (EditText)findViewById(R.id.add_profile_etFirstNameLink);
        etLastNameLink = (EditText)findViewById(R.id.add_profile_etLastNameLink);

        emailAddress = getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).getString(Global.SHARED_PREF_EMAIL_KEY,"");
    }

    public void addProfileOnButtonClick (View v) {
        if (v.getId() == R.id.add_profile_btnAddProfile) {
            if (etProfileName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter Profile Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etFirstNameLink.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etLastNameLink.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                return;
            }
            Call<ResponseBody> addProfileCall = Global.getInstance().getSlappService().addProfile(emailAddress, etProfileName.getText().toString());
            addProfileCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                    try {
                        if (response.body() != null) {
                            profileId = Integer.parseInt(response.body().string());

                            // Generic callback
                            Callback<ResponseBody> addLinkCallback = new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                    try {
                                        if (response.body() != null) {
                                            String responseString = response.body().string();
                                            if (responseString.contains("added")) {
                                                Log.i("Slapp", "Link added");
                                            } else if (responseString.contains("Updated")) {
                                                Log.i("Slapp", "Active profile set");
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                                            Log.e("Slapp", response.message());
                                        }
                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                                        Log.e("Slapp", response.message());
                                    }
                                }
                                @Override
                                public void onFailure(Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Network Error: Please try again", Toast.LENGTH_SHORT).show();
                                }
                            };

                            // Add first and last name links
                            Global.getInstance().getSlappService().addLink(profileId, "first", etFirstNameLink.getText().toString()).enqueue(addLinkCallback);
                            Global.getInstance().getSlappService().addLink(profileId, "last", etLastNameLink.getText().toString()).enqueue(addLinkCallback);

                            // Set active profile
                            Global.getInstance().getSlappService().setActiveProfile(emailAddress, profileId).enqueue(addLinkCallback);

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                            Log.e("Slapp", response.message());
                        }
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                        Log.e("Slapp", response.message());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getApplicationContext(), "Network Error: Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
