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
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmailAddress, etPassword, etPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstName = (EditText)findViewById(R.id.sign_up_etFirstName);
        etLastName = (EditText)findViewById(R.id.sign_up_etLastName);
        etEmailAddress = (EditText)findViewById(R.id.sign_up_etEmail);
        etPassword = (EditText)findViewById(R.id.sign_up_etPassword);
        etPasswordConfirm = (EditText)findViewById(R.id.sign_up_etPasswordConfirm);
    }

    public void signUpOnButtonClick(View v) {
        if (etFirstName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Pattern.matches("['A-Za-z0-9- ]+", etFirstName.getText().toString())) {
            Toast.makeText(this, "First Name can only contain A-Z, a-z, 0-9, ', \", -", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etLastName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Pattern.matches("['A-Za-z0-9- ]+", etLastName.getText().toString())) {
            Toast.makeText(this, "Last Name can only contain A-Z, a-z, 0-9, ', \", -", Toast.LENGTH_SHORT).show();
            return;
        }
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
        if (etPasswordConfirm.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Password Confirmation", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return;
        }
        final String emailAddress = etEmailAddress.getText().toString();
        Call<ResponseBody> call = Global.getInstance().getSlappService().signUp(emailAddress, etPassword.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    if (response.body() != null && response.body().string().contains("added")) {
                        getApplicationContext().getSharedPreferences(Global.SHARED_PREF_KEY, Context.MODE_PRIVATE).edit().putString(Global.SHARED_PREF_EMAIL_KEY, emailAddress).commit();
                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
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
