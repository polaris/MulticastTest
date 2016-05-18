package com.sennheiser.connect.multicasttest;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "MulticastTask";

    private final Context context;

    public MulticastTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TAG);
        WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
        multicastLock.setReferenceCounted(true);

        wifiLock.acquire();
        multicastLock.acquire();

        try {
            MulticastSocket socket = new MulticastSocket(32123);

            InetAddress group = InetAddress.getByName("224.1.2.3");
            socket.joinGroup(group);

            DatagramPacket packet;
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);

            Log.d(TAG, "before calling receive ----------------------------------------");

            socket.receive(packet);

            Log.d(TAG, "after calling receive ----------------------------------------");

            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        multicastLock.release();
        wifiLock.release();

        return null;
    }
}
