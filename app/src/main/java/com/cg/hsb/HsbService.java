package com.cg.hsb;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.cg.hsb.Protocol;

public class HsbService extends Service {

    private boolean mQuit = false;

    private HsbBinder mBinder = new HsbBinder();

    private Protocol mProto = null;
    Handler handler;

    public class HsbBinder extends Binder
    {
        public HsbService getService() {
            return HsbService.this;
        }

        public Protocol getProtocol() {
            return mProto;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {

            }
        };

        mProto = new Protocol(this, handler);
        new Thread(mProto).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mQuit = true;
    }


}
