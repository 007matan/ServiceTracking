package com.cohen.servicetracking;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class TapService extends Service {

    public static final String START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    private boolean isServiceRunningRightNow = false;

    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager;

    private TapDetector tapDetector;

    public static int NOTIFICATION_ID = 154;
    private int lastShownNotificationId = -1;
    public static String CHANNEL_ID = "com.guy.servicetracking.CHANNEL_ID_FOREGROUND";
    public static String MAIN_ACTION = "com.guy.servicetracking.tapservice.action.main";
    private NotificationCompat.Builder notificationBuilder;


    private TapDetector.CallBack_taps callBack_taps = new TapDetector.CallBack_taps() {
        @Override
        public void threeTap() {
            Log.d("pttt", "Three Hand-Shake");
        }
    };


//OnStartCommand - waiting to start by Activities
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopForeground(true);
            return START_NOT_STICKY;
        }

        Log.d("pttt", "onStartCommand A");
        if (intent.getAction().equals(START_FOREGROUND_SERVICE)) {
            if (isServiceRunningRightNow) {
                return START_STICKY;
            }
            Log.d("pttt", "onStartCommand B");


            isServiceRunningRightNow = true;
            notifyToUserForForegroundService();
            startRecording();

        } else if (intent.getAction().equals(STOP_FOREGROUND_SERVICE)) {
            stopRecording();
            stopForeground(true);
            stopSelf();

            isServiceRunningRightNow = false;
            return START_NOT_STICKY;
        }



        return START_STICKY;
    }

    private void startRecording() {
        // Keep CPU working
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PassiveApp:tag");
        wakeLock.acquire();

        //Active TAP Detector
        tapDetector = new TapDetector(this, callBack_taps);
        tapDetector.start();

    }
    private void stopRecording() {
        // Release CPU Holding
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    // // // // // // // // // // // // // // // // Notification  // // // // // // // // // // // // // // //

    private void notifyToUserForForegroundService() {
        // On notification click
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = getNotificationBuilder(this,
                CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top

        notificationBuilder
                .setContentIntent(pendingIntent) // Open activity
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_cycling)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle("App in progress")
                .setContentText("Tap tracking")
        ;

        Notification notification = notificationBuilder.build();

        startForeground(NOTIFICATION_ID, notification);

        if (NOTIFICATION_ID != lastShownNotificationId) {
            // Cancel previous notification
            final NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.cancel(lastShownNotificationId);
        }
        lastShownNotificationId = NOTIFICATION_ID;
    }

    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String notifications_channel_description = "Cycling map channel";
        String description = notifications_channel_description;
        final NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if(nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);

                // from another answer
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.BLUE);

                nm.createNotificationChannel(nChannel);
            }
        }
    }

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runs = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TapService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}


