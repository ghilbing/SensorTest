package com.hilbing.sensor2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DeactivateActivity extends AppCompatActivity {

    SensorService service;

    TextView countdown;
    EditText number;
    SensorService mSensor;


    boolean mBounded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate);
        Context context = getApplicationContext();
        number = findViewById(R.id.number);

        number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER) && number.getText().toString().equals("1234")) {
                    mSensor.stopCountdown();
                    Log.i("Deactivate", "onKey: CountDown Stopped");
                    return true;
                }
                return false;
            }
        });
    }



    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        //    countdown.setText(intent.getStringExtra("countdown"));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SensorService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Toast.makeText(DeactivateActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mBounded = true;
            SensorService.LocalBinder mLocalBinder = (SensorService.LocalBinder) iBinder;
            mSensor = mLocalBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(DeactivateActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mBounded = false;
            mSensor = null;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded){
            unbindService(mConnection);
            mBounded = false;
        }
    }
}
