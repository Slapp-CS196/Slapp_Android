package xyz.slapp.slapp_android;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by shreyas on 15-10-25.
 */
public interface SlappService {
    @GET("/api/new")
    Call<ResponseBody> sendSlapp(@Query("user_id") String userId, @Query("time") long time, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);
}