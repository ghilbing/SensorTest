package com.hilbing.sensor2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "accelerometer";
    private static final String NOTIFICATION_ID = "notificationID";
    public static String TAG = SensorService.class.getCanonicalName();
    private NotificationManager mNotificationManager;
    boolean stop = false;


    public static final String COUNTDOWN_BR = "com.hilbing.sensor2.DeactivateActivity";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;


    private SensorManager sensorManager;
    private Sensor mAccelerometer;

    public int counter = 0;

    private final IBinder mBinder = new LocalBinder();


    public IBinder onBind() {
        return onBind();
    }

    public class LocalBinder extends Binder{
        SensorService getService(){
            return SensorService.this;
        }
    }

    public SensorService() {

        Log.i(TAG, "SensorService: here I am");
    }

    @Override
    public void onCreate() {
        super.onCreate();
     //   mCallBack.stopCounter(stop);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startCustomForeground();
        } else {
            startForeground(1, new Notification());
        }

    }

    private void startCustomForeground() {
        notification();
    }

    public void startCountdown(){
        Log.i(TAG, "onCreate: Starting timer....");
        cdt = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                Log.i(TAG, "onTick: seconds remaining..." + l);
                bi.putExtra("countdown", l);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                cdt = null;
                Log.i(TAG, "onFinish: Timer finished");
                stop = false;

            }
        };
        cdt.start();
    }

    public void stopCountdown(){
        if(cdt != null){
            cdt.cancel();
            cdt = null;
            Log.i(TAG, "stopCountdown: Finally Stopped");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        stopForeground(true);



       // startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: EXIT");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(mAccelerometer != null){
            sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
          //  Log.d(TAG, "onSensorChanged: Registered accelerometer");
        } else {
         //   Log.d(TAG, "onSensorChanged: Accelerometer not supported");
        }

        //Sensor sensor = sensorEvent.sensor;
       // Log.d(TAG, "onSensorChanged: X " + sensorEvent.values[0] + " Y " + sensorEvent.values[1] + " Z " + sensorEvent.values[2]);
        double rootSquare = Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2)
                + Math.pow(sensorEvent.values[2], 2));
        if(rootSquare < 2.0){
            Toast.makeText(getApplicationContext(), "FALL DETECTED!!!!", Toast.LENGTH_LONG).show();
            notification();
            startCountdown();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void notification(){
        Log.i(TAG, "notification: pass here");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent intent = new Intent(getApplicationContext(), DeactivateActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText("Hello");
        bigTextStyle.setBigContentTitle("Accelerometer");
        bigTextStyle.setSummaryText("Text detail");

        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Accelerometer");
        builder.setContentText("Description");
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(bigTextStyle);
        builder.setAutoCancel(false);
        builder.addAction(R.drawable.ic_deactivate, "Deactivate", makePendingIntent("Deactivate"));



        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Accelerometer", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }
        mNotificationManager.notify(0, builder.build());
    }

    public PendingIntent makePendingIntent(String name){
        Intent intent = new Intent(getApplicationContext(), DeactivateActivity.class);
        intent.setAction(name);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        return pendingIntent;
    }

}
