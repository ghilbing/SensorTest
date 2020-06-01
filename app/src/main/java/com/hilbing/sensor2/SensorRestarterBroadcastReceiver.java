package com.hilbing.sensor2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = SensorRestarterBroadcastReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: Service Stops OOoooooooopsss!!!");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(new Intent(context, SensorService.class));
        } else {
            context.startService(new Intent(context, SensorService.class));
        }

    }
}
