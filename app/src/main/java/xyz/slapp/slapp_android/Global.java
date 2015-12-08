package xyz.slapp.slapp_android;

import retrofit.Retrofit;

public class Global {
    private static Global mInstance = null;

    public static final String API_BASE_URL = "https://api.slapp.xyz";
    public static final  String SHARED_PREF_KEY = "xyz.slapp.android.SHARED_PREF";
    public static final  String SHARED_PREF_EMAIL_KEY = "xyz.slapp.android.SHARED_PREF_EMAIL";
    public static final  String SHARED_PREF_LOGGED_IN_KEY = "xyz.slapp.android.SHARED_PREF_LOGGED_IN";

    private Retrofit retrofit;
    private SlappService slappService;

    private Global() {
        retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .build();
        slappService = retrofit.create(SlappService.class);
    }

    public static Global getInstance() {
        if (mInstance == null) mInstance = new Global();
        return mInstance;
    }

    public SlappService getSlappService() {
        return slappService;
    }
}
