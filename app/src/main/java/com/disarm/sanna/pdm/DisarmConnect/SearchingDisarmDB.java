package com.disarm.sanna.pdm.DisarmConnect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;
import java.util.StringTokenizer;

import static com.disarm.sanna.pdm.DisarmConnect.MyService.dbAPName;
import static com.disarm.sanna.pdm.DisarmConnect.MyService.wifiInfo;

/**
 * Created by hridoy on 21/8/16.
 */
public class SearchingDisarmDB implements Runnable {
    private android.os.Handler handler;
    private Context context;
    private int timerDBSearch = 3000;
    public int minDBLevel = 2;
    private String ssid;
    public String connectedSSID = MyService.wifi.getConnectionInfo().getSSID().toString().replace("\"","");
    public String lastConnectedSSID = connectedSSID;


    public SearchingDisarmDB(android.os.Handler handler, Context context)
    {
        this.handler = handler;
        this.context = context;

        this.handler.post(this);
    }
    @Override
    public void run()
    {
        connectedSSID = MyService.wifi.getConnectionInfo().getSSID().toString().replace("\"","");
        lastConnectedSSID.replace("\"","");
        if(lastConnectedSSID.startsWith("DH-") && !(lastConnectedSSID.equals(connectedSSID)))
        {
            Log.v("Disconnected DH:",lastConnectedSSID);
            Logger.addRecordToLog("Disconnected : " + lastConnectedSSID);
            lastConnectedSSID = "";
        }
        else if(lastConnectedSSID.contains("DB") && !(lastConnectedSSID.equals(connectedSSID))) {
            Log.v("Disconnected DB:",lastConnectedSSID);
            Logger.addRecordToLog("Disconnected : " + lastConnectedSSID);
            lastConnectedSSID = "";
        }
        else if(connectedSSID.startsWith("DH-") || connectedSSID.contains("DB"))
        {
            Log.v("Connected SSID:", connectedSSID + "LastConnectedSSID:" + lastConnectedSSID);
            lastConnectedSSID = connectedSSID;
        }
        else
        {
            Log.v("Connected SSID:", connectedSSID);
            lastConnectedSSID = "";
            connectedSSID = "";
        }
        Log.v(MyService.TAG4,"searching DB");
        List<ScanResult> allScanResults = MyService.wifi.getScanResults();
        if (allScanResults.toString().contains(MyService.dbAPName)) {
            // compare signal level
            int level = findDBSignalLevel(allScanResults);
            if (level < minDBLevel)
            {
                if(connectedSSID.contains("DB")) {
                    if (MyService.wifi.disconnect()) {
                        Logger.addRecordToLog("DB Disconnected as Level = " + level);
                        Log.v(MyService.TAG1, "DB Disconnected as Level = " + level);
                    }
                }
            }
            else if(!connectedSSID.contains("DB"))
            {
                Log.v(MyService.TAG4, "Connecting DisarmDB");

                MyService.wifi.disconnect();


              //  handler.removeCallbacksAndMessages(null); !-- Dont use it all other handler will be closed
                for (int i = 0 ; i < allScanResults.size(); i++)
                {
                    if(allScanResults.get(i).SSID.contains("DB")) {
                        ssid = allScanResults.get(i).SSID;
                        break;
                    }
                }
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = "\"" + ssid + "\""; //IMPORTANT! This should be in Quotes!!
               // wc.preSharedKey = "\""+ MyService.dbPass +"\"";
                //wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

                wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);




                int res = MyService.wifi.addNetwork(wc);
                boolean b = MyService.wifi.enableNetwork(res, true);
                Log.v(MyService.TAG4, "Connected to DB");
            }

        }
        else {
            Log.v(MyService.TAG4,"DisarmHotspotDB not found");
        }
        handler.postDelayed(this, timerDBSearch);
    }
    public int findDBSignalLevel(List<ScanResult> allScanResults)
    {
        for (ScanResult scanResult : allScanResults) {
            if(scanResult.SSID.toString().contains(MyService.dbAPName)) {
               int level =  WifiManager.calculateSignalLevel(scanResult.level, 5);
                return level;
            }
        }
        return 0;
    }

}
