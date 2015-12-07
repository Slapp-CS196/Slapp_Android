package xyz.slapp.slapp_android;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SlappService {
    @GET("/api/newUser")
    Call<ResponseBody> signUp(@Query("email") String emailAddress, @Query("password") String password, @Query("first_name") String firstName, @Query("last_name") String lastName);

    @GET("/api/getLogin")
    Call<ResponseBody> logIn(@Query("email") String emailAddress, @Query("password") String password);

    @GET("/api/newSlapp")
    Call<ResponseBody> sendSlapp(@Query("email") String emailAddress, @Query("time") long time, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);
}