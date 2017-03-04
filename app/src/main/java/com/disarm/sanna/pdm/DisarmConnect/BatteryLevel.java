package com.disarm.sanna.pdm.DisarmConnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by hridoy on 19/8/16.
 */
public class BatteryLevel extends BroadcastReceiver {
    @Override
    public void onReceive(Context arg0, Intent intent) {

        // Set the Battery Level
        MyService.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }
}
