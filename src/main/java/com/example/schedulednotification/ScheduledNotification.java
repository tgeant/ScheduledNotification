package com.example.schedulednotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class ScheduledNotification {
    private static final ScheduledNotification ourInstance = new ScheduledNotification();

    private static  final String LOGTAG = "MyPluginTest";
    public static ScheduledNotification getInstance() {
        return ourInstance;
    }

    private static ScheduledNotificationService mService;
    private  static  boolean mBound = false;

    private long startTime;

    private ScheduledNotification() {
        Log.i(LOGTAG, "Created MyPlugin");
        startTime = System.currentTimeMillis();

    }

    public void startMyService(Activity myActivity){
        Log.i(LOGTAG, "Start service");

        Intent intentService = new Intent(myActivity,ScheduledNotificationService.class);
        myActivity.startService(intentService);
        myActivity.bindService(intentService, connection, Context.BIND_NOT_FOREGROUND);


        if( ContextCompat.checkSelfPermission( myActivity, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY ) == PackageManager.PERMISSION_GRANTED) {
            Log.i(LOGTAG, "Permission granted :)");

        } else {
            Log.i(LOGTAG, "No permission for notifications");
        }

    }

    public void scheduleNotification(String date, String title, String text){
        if(mService!=null)
            mService.scheduleNotification(date, title, text);
        else
            Log.i(LOGTAG, "no scheduleNotification");
    }

    public void deleteAllScheduleNotifications(){
        if(mService!=null)
            mService.deleteAllScheduleNotifications();
        else
            Log.i(LOGTAG, "no deleteAllScheduleNotifications");
    }

    public double getElapsedTime(){
        return (System.currentTimeMillis()-startTime)/1000.0f;
    }



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(LOGTAG, "Connection etablished !");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ScheduledNotificationService.LocalBinder binder = (ScheduledNotificationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}