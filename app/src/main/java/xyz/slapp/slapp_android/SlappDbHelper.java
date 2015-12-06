package xyz.slapp.slapp_android;

/**
 * Created by shreyas on 15-11-15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SlappDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3; // increment to trigger DB rebuild on app update
    public static final String DATABASE_NAME = "Slapps.db";

    public static final String TABLE_NAME = "slapps";

    public static final String COLUMN_NAME_USER_ID = "userid";
    public static final String COLUMN_NAME_TIMESTAMP = "slapptime";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_RADIUS = "radius";

    public SlappDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "\n(\n"
                + COLUMN_NAME_USER_ID + " VARCHAR(64) PRIMARY KEY,\n"
                + COLUMN_NAME_TIMESTAMP + " INTEGER,\n"
                + COLUMN_NAME_LATITUDE + " FLOAT,\n"
                + COLUMN_NAME_LONGITUDE + " FLOAT,\n"
                + COLUMN_NAME_RADIUS + " INTEGER,\n"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearDatabase(db);
    }

    public void addSlapp(String userId, long timestamp, double latitude, double longitude, int radius) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_USER_ID, userId);
        values.put(COLUMN_NAME_TIMESTAMP, timestamp);
        values.put(COLUMN_NAME_LATITUDE, latitude);
        values.put(COLUMN_NAME_LONGITUDE, longitude);
        values.put(COLUMN_NAME_RADIUS, radius);
        db.insert(TABLE_NAME, null, values);
    }

    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetchAllSubmissions() {
        Cursor mCursor = this.getReadableDatabase().rawQuery("SELECT rowid _id,* FROM " + TABLE_NAME, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}