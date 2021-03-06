package com.disarm.sanna.pdm.DisarmConnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by hridoy on 19/8/16.
 */
public class WifiScanReceiver extends BroadcastReceiver {
    public static String wifis[]={"None"};
    public void onReceive(Context c, Intent intent) {

        List<ScanResult> wifiScanList = MyService.wifi.getScanResults();
        wifis = new String[wifiScanList.size()];

        for(int i = 0; i < wifiScanList.size(); i++){
            wifis[i] = String.valueOf(wifiScanList.get(i));
            Log.v("Available Networks : ", wifiScanList.get(i).SSID);
        }

    }
}
