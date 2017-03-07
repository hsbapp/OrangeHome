package com.cg.hsb;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmoonxs.www.orangehome.R;

import java.util.ArrayList;

public class TestActivity extends Activity {

    private HsbService mService = null;
    private Protocol mProto = null;
    private PlugDevice mPlug = null;
    private TVDevice mTV = null;

    private Button mButton;
    private Button mButton2;
    private TextView mText;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((HsbService.HsbBinder)service).getService();
            mProto = ((HsbService.HsbBinder)service).getProtocol();

            mProto.SetListener(new HsbListener() {
                @Override
                public void onDeviceOnline(HsbDevice device) {
                    String dev_type = device.GetDevType();
                    Log.d("hsbservice", "onDeviceOnline: " + dev_type);
                    if (dev_type == HsbConstant.HSB_DEV_TYPE_PLUG) {
                        mPlug = (PlugDevice)device;
                    }
                }

                @Override
                public void onDeviceOffline(HsbDevice device) {
                    String dev_type = device.GetDevType();
                    int dev_id = device.GetDevId();
                    Log.d("hsbservice", "onDeviceOffline: " + dev_id);
                    if (mPlug != null && mPlug.mDevId == dev_id)
                        mPlug = null;
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            conn = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /*
        setContentView(R.layout.test);

        Intent intent = new Intent();
        intent.setAction("com.cg.hsb.service.HSB_SERVICE");
        intent.setPackage(getPackageName());
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        mText = (TextView)findViewById(R.id.textView);
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        mButton2 = (Button)findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });*/
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(conn);
    }

    /* Examples */

    /* 0.Get all devices and properties */
    private void GetDevicesPrty() {
        ArrayList<HsbDevice> devList = mProto.GetDeviceList();

        for (HsbDevice device : devList)
        {
            // Get name
            String name = device.GetName();
            // Get location
            String location = device.GetLocation();

            String devtype = device.GetDevType();

            if (devtype == HsbConstant.HSB_DEV_TYPE_PLUG) {
                PlugDevice plug = (PlugDevice) device;

                // get power status
                boolean power = plug.GetPowerStatus();

                // set power status
                plug.SetPowerStatus(power);
            } else if (devtype == HsbConstant.HSB_DEV_TYPE_SENSOR) {
                SensorDevice sensor = (SensorDevice) device;

                // get pm2.5 status
                int pm25 = sensor.GetPM25();
                // get temperature status
                int temp = sensor.GetTemperature();
                // get humidity status
                int humidity = sensor.GetHumidity();
                // get gas status
                int gas = sensor.GetGas();
            } else if (devtype == HsbConstant.HSB_DEV_TYPE_REMOTE_CTL) {
                // nothing to do
            } else if (devtype == HsbConstant.HSB_DEV_TYPE_IR) {
                String irtype = device.GetIrType();
                if (irtype == HsbConstant.HSB_IR_TYPE_TV) {
                    TVDevice tv = (TVDevice) device;

                    // press tv key
                    boolean ret = tv.PressKey(HsbConstant.HSB_TV_KEY_OK);

                } else if (irtype == HsbConstant.HSB_IR_TYPE_AC) {
                    ACDevice ac = (ACDevice) device;

                    // get work mode
                    String WorkMode = ac.GetWorkMode();
                    // set work mode
                    ac.SetWorkMode("制冷");

                    // get power
                    boolean power = ac.GetPower();
                    // set power
                    ac.SetPower(true);

                    // get wind speed
                    String WindSpeed = ac.GetWindSpeed();
                    // set wind speed
                    ac.SetWindSpeed("一级");
                    // next wind speed
                    ac.NextWindSpeed();
                    // prev wind speed
                    ac.PrevWindSpeed();

                    // get temperature
                    int temp = ac.GetTemperature();
                    // set temperature
                    ac.SetTemperature(26);
                    // add temperature
                    ac.AddTemperature();
                    // dec temperature
                    ac.DecTemperature();

                    // get light
                    boolean light = ac.GetLight();
                    // set light
                    ac.SetLight(true);
                }
            }
        }
    }

    private void SetPlugDeviceStatus(PlugDevice device, boolean power) {
        device.SetPowerStatus(power);
    }

    private boolean GetPlugDeviceStatus(PlugDevice device) {
        return device.GetPowerStatus();
    }

    private void SetDeviceConfigSample(HsbDevice device)
    {
        // sample code: set name and location of a device
        String name = new String("电风扇");
        String location = new String("客厅");
        device.SetName(name);
        device.SetLocation(location);
    }

    private void GetDeviceConfigSample(HsbDevice device) {
        // sample code: get name and location of a device
        String name = device.GetName();
        String location = device.GetLocation();
    }
}