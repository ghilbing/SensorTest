package com.hilbing.sensor2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class MainBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = MainBroadcastReceiver.class.getCanonicalName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: MainActivity OOoooooooopsss!!!");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startActivity(new Intent(context, MainActivity.class));
        } else {
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }
}
