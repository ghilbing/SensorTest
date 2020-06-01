package com.hilbing.sensor2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    Intent mServiceIntent;
    private SensorService mSensorService;
    Context context;
    boolean mServiceBound = false;

    public Context getContext() {
        return context;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SensorService.LocalBinder myBinder = (SensorService.LocalBinder) iBinder;
            mSensorService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService();
        mServiceIntent = new Intent(getContext(), mSensorService.getClass());
        if(!isMyServiceRunning(mSensorService.getClass())){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(mServiceIntent);
            } else {
                startService(mServiceIntent);
            }

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                Log.i(TAG, "isMyServiceRunning: true");
                return true;
            }
        }
        Log.i(TAG, "isMyServiceRunning: false");
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy");
        super.onDestroy();
    }
}
