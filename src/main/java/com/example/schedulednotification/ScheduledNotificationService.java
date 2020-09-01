package com.example.schedulednotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledNotificationService extends Service{
    private static  final String LOGTAG = "MyPluginTest";

    public static boolean isServiceRunning = false;

    private  NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    private final IBinder binder = new LocalBinder();


    public class LocalBinder extends Binder {
        ScheduledNotificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ScheduledNotificationService.this;
        }
    }



    public void deleteAllScheduleNotifications(){
        Log.i(LOGTAG, "Delete all scheduled notifications");
        try {
            JSONObject jsonRoot = loadData();
            jsonRoot.put("listScheduledNotifications", new JSONArray());

            saveData(jsonRoot);

        } catch(Exception e){

        }
    }



    public void scheduleNotification(String date, String title, String text){

        Log.i(LOGTAG, "Date: "+date);

        try {
            JSONObject jsonRoot = loadData();
            JSONArray jaListScheduled = jsonRoot.getJSONArray("listScheduledNotifications");

            JSONObject jsonNotification = new JSONObject();
            jsonNotification.put("date",date);
            jsonNotification.put("title",title);
            jsonNotification.put("text",text);
            jaListScheduled.put(jsonNotification);

            saveData(jsonRoot);

            Log.i(LOGTAG, this.getExternalFilesDir(null).getAbsolutePath());
            Log.i(LOGTAG, "File content:\n" + AndroidLocalFile.readFileOnInternalStorage(this, "scheduledNotifications.json") + "\n");

        } catch (Exception e){
            Log.e(LOGTAG, e.getMessage());
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOGTAG, "Service launched");

        // Set the Notification Channel and manager
        createNotificationChannel(this);

        notificationManager = NotificationManagerCompat.from(this);

        // Set the timer
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }

        },5000,60000*5); //Every 5 minutes
    }


    private void update(){
        Log.i(LOGTAG, "update");

        try {
            JSONObject jRoot = new JSONObject(AndroidLocalFile.readFileOnInternalStorage(this, "scheduledNotifications.json"));
            JSONArray jListSheduledNotif = jRoot.getJSONArray("listScheduledNotifications");

            int i=0;
            while(i<jListSheduledNotif.length()){
                JSONObject notif = (JSONObject) jListSheduledNotif.get(i);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                Date dateNotif = formatter.parse(notif.getString("date"));

                Date dateNow = new Date(System.currentTimeMillis());

                if(dateNotif.before(dateNow)){
                    Log.i(LOGTAG, "/!\\ NOTIFICATION");

                    builder = new NotificationCompat.Builder(this, "MyChannelPlugin")
                            .setContentTitle(notif.getString("title"))
                            .setContentText(notif.getString("text"))
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    notificationManager.notify(0, builder.build());

                    jListSheduledNotif.remove(i);
                }
                i++;
            }

            // Update the json file
            jRoot.put("listScheduledNotifications",jListSheduledNotif);
            saveData(jRoot);

        } catch (JSONException e) {
            Log.e(LOGTAG, "Cannot load json file");
        } catch (ParseException e) {
            Log.e(LOGTAG, e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "onStartCommand");

        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }



    private void saveData(JSONObject jsonRoot){
        try {
            AndroidLocalFile.writeFileOnInternalStorage(this, "scheduledNotifications.json", jsonRoot.toString(1));
        } catch (JSONException e) {
            Log.e(LOGTAG, e.getMessage());
        }
    }

    private JSONObject loadData(){
        if(AndroidLocalFile.isFileOnInternalStorageExist(this, "scheduledNotifications.json")){
            JSONObject jo = null;
            try {
                jo = new JSONObject(AndroidLocalFile.readFileOnInternalStorage(this, "scheduledNotifications.json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo;
        } else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject();
                JSONArray jaListScheduled = new JSONArray();
                jsonObject.put("listScheduledNotifications", jaListScheduled);
            } catch(JSONException e){

            }

            return jsonObject;
        }
    }


    private void createNotificationChannel(Service myActivity) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "JustDoItChannel";
            String description = "Channel for Just Do It";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MyChannelPlugin", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(myActivity);
            notificationManager.createNotificationChannel(channel);
        }
    }

}


