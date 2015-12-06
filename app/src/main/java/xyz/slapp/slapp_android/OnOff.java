package xyz.slapp.slapp_android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.RemoteViews;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;

/**
 * Implementation of App Widget functionality.
 */
public class OnOff extends AppWidgetProvider {
    private Intent i;
    public int count = 0;
    Context initialContext;
    public static final String buttoni = "xyz.slapp.slapp_android.WIDGET_BUTTON";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;
        remoteViews = new RemoteViews(context.getPackageName(),R.layout.on_off);
        watchWidget = new ComponentName(context, OnOff.class);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, buttoni));
        appWidgetManager.updateAppWidget(watchWidget,remoteViews);

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.on_off);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        if (count == 0){
            i = new Intent(context, BackGroundRun.class);
            initialContext = context;
            count++;
        }
        if (buttoni.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.on_off);
            watchWidget = new ComponentName(context, OnOff.class);


            if (BackGroundRun.status) {
                BackGroundRun.status = !BackGroundRun.status;
                initialContext.stopService(i);
                remoteViews.setTextViewText(R.id.appwidget_text, "" + BackGroundRun.status);
            } else {
                BackGroundRun.status = !BackGroundRun.status;
                initialContext.startService(i);
                remoteViews.setTextViewText(R.id.appwidget_text, "" + BackGroundRun.status);
            }

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }
    private PendingIntent getPendingSelfIntent(Context context, String action){
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context,0,intent,0);
    }

}