package xyz.slapp.slapp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LogInActivity extends AppCompatActivity {

    EditText etEmailAddress, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etEmailAddress = (EditText)findViewById(R.id.log_in_etEmail);
        etEmailAddress.setText(getSharedPreferences(Global.SHARED_PREF_KEY,Context.MODE_PRIVATE).getString(Global.SHARED_PREF_EMAIL_KEY,""));
        etPassword = (EditText)findViewById(R.id.log_in_etPassword);
    }

    public void logInOnButtonClick(View v) {
        if (etEmailAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Email Address", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etEmailAddress.getText().toString()).matches()) {
            Toast.makeText(this, "Email Address must be valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        final String emailAddress = etEmailAddress.getText().toString();
        Call<ResponseBody> call = Global.getInstance().getSlappService().logIn(emailAddress, etPassword.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null && response.body().string().equals("Login success")) {
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).edit();
                        editor.putString(Global.SHARED_PREF_EMAIL_KEY, emailAddress);
                        editor.putBoolean(Global.SHARED_PREF_LOGGED_IN_KEY, true);
                        editor.commit();
                        Intent i = new Intent(LogInActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else if (response.body() != null && response.body().string().equals("Wrong password")) {
                        Toast.makeText(getApplicationContext(), "Error: Invalid Email Address or Password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Please try again", Toast.LENGTH_SHORT).show();
                        Log.e("Slapp", response.body().string());
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
