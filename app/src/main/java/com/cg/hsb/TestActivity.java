package com.cg.hsb;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TestActivity extends Activity {

    private HsbService mService = null;
    private Protocol mProto = null;
    private PlugDevice mPlug = null;
    private CC9201 mCc9201 = null;

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
                    int dev_type = device.GetDevType();
                    Log.d("hsbservice", "onDeviceOnline: " + dev_type);
                    if (dev_type == HsbConstant.HSB_DEV_TYPE_PLUG) {
                        mPlug = (PlugDevice)device;
                        mPlug.SetListener(new PlugDeviceListener() {
                            @Override
                            public void onPowerStatusUpdated(boolean power) {
                                Log.d("hsbservice", "onPowerStatusUpdated");
                            }
                        });
                    } else if (dev_type == HsbConstant.HSB_DEV_TYPE_STB_CC9201) {
                        mCc9201 = (CC9201)device;
                        mCc9201.SetListener(new CC9201Listener() {
                            @Override
                            public void onSetChannelStatusResult(int errcode) {
                                Log.d("hsbservice", "onSetChannelStatusResult: " + errcode);
                            }

                            @Override
                            public void onPressKeyResult(int errcode) {
                                Log.d("hsbservice", "onPressKeyResult: " + errcode);
                            }
                        });
                    }
                }

                @Override
                public void onDeviceOffline(HsbDevice device) {
                    int dev_type = device.GetDevType();
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
        /*setContentView(R.layout.test);

        Intent intent = new Intent();
        intent.setAction("com.cg.hsb.service.HSB_SERVICE");
        intent.setPackage(getPackageName());
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        mText = (TextView)findViewById(R.id.textView);
        mButton = (Button)findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProto.StartVoiceRecognizer();
            }
        });

        mButton2 = (Button)findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCc9201 != null)
                    mCc9201.ConfigVR();
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
        HsbDevice device = null;

        for (int id = 0; id < devList.size(); id++) {
            device = devList.get(id);

            // Get name
            String name = device.GetName();
            // Get location
            String location = device.GetLocation();

            switch (device.GetDevType()) {
                case HsbConstant.HSB_DEV_TYPE_PLUG: {
                    PlugDevice plug = (PlugDevice) device;

                    // get power status
                    boolean power = plug.PowerStatus();

                    // set power status
                    plug.SetPowerStatus(power);

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_SENSOR: {
                    SensorDevice sensor = (SensorDevice) device;

                    // get pm2.5 status
                    int pm25 = sensor.PM25Status();
                    // get temperature status
                    int temp = sensor.TemperatureStatus();
                    // get humidity status
                    int humidity = sensor.HumidityStatus();
                    // get gas status
                    int gas = sensor.GasStatus();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_STB_CC9201: {
                    CC9201 stb = (CC9201) device;

                    // get tv channel id
                    int channel = stb.ChannelStatus();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_GRAY_AC: {
                    GrayAirConditioner ac = (GrayAirConditioner) device;

                    // get work mode
                    int WorkMode = ac.WorkMode();
                    // get power
                    int power = ac.Power();
                    // get wind speed
                    int WindSpeed = ac.WindSpeed();
                    // get temperature
                    int temp = ac.Temperature();
                    // get light
                    int light = ac.Light();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_REMOTE_CTL:
                    // nothing to do
                    break;
                default:
                    break;
            }
        }
    }

    /* 1.Plug */
    private void InitPlugDevice(PlugDevice device) {
        device.SetListener(new PlugDeviceListener() {
            @Override
            public void onPowerStatusUpdated(boolean power) {
                // TODO: add your implementation
                // update power status on UI
            }

            @Override
            public void onSetPowerStatusResult(int errcode)
            {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // set power status success
                } else {
                    // set power status failed
                }
            }

            @Override
            public void onGetPowerStatusError(int errcode)
            {
                // TODO: add your implementation
                // get power status failed
            }
        });
    }

    private void SetPlugDeviceStatus(PlugDevice device, boolean power) {
        device.SetPowerStatus(power);
        // the result will be returned in onSetPowerStatusResult
    }

    private boolean GetPlugDeviceStatus(PlugDevice device) {
        return device.PowerStatus();
    }

   /* private void SetPlugDeviceTimerSample(PlugDevice device) {
        // sample code: set a timer which will be tiggered once at 9;30 AM.
        HsbDeviceTimer timer = new HsbDeviceTimer();
        timer.SetTime(9, 30, 0);  // at 9:30:00 AM
        timer.SetWeekDay(HsbConstant.HSB_TIMER_WEEKDAY_ONE_SHOT); // tigger once
        //timer.SetWeekDay(HsbConst.HSB_TIMER_WEEKDAY_EVERY_DAY); // tigger every day
        timer.SetAction(device.MakeActionSetPowerStatus(true)); // set power on

        device.SetTimer(0, timer);
        // the result will be returned in onSetTimerResult
    }*/

    private void SetDeviceConfigSample(HsbDevice device)
    {
        // sample code: set name and location of a device
        String name = new String("电风扇");
        String location = new String("客厅");
        device.SetConfig(new HsbDeviceConfig(name, location));
        // the result will be returned in onSetConfigResult
    }

    private void GetDeviceConfigSample(HsbDevice device) {
        // sample code: get name and location of a device
        String name = device.GetName();
        String location = device.GetLocation();
    }

    private void GetDeviceInfoSample(HsbDevice device) {
        // sample code: get info of a device
        HsbDeviceInfo info = new HsbDeviceInfo();
        device.GetInfo(info);
    }

    /* 2.Set Channel */
    private void InitCC9201(CC9201 device) {
        device.SetListener(new CC9201Listener() {
            @Override
            public void onSetChannelStatusResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // set channel status success
                } else {
                    // set channel status failed
                }
            }

            @Override
            public void onPressKeyResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // press key success
                } else {
                    // press key failed
                }
            }

            @Override
            public void onSetChannelResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // set channel/id mapping success
                } else {
                    // set channel/id mapping failed
                }
            }

            @Override
            public void onDelChannelResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // delete channel/id mapping success
                } else {
                    // delete channel/id mapping failed
                }
            }

            @Override
            public void onSwitchChannelResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // switch channel/id mapping success
                } else {
                    // switch channel/id mapping failed
                }
            }
        });
    }

    private void SetCC9201ChannelStatusSample(CC9201 device, int channel) {
        // sample code: switch CC9201's channel by id
        device.SetChannelStatus(channel);
        // the result will be returned in onSetChannelStatusResult
    }

    private void CC9201PressKey(CC9201 device) {
        // sample code: press CC9201's keys
        device.PressKey(HsbConstant.HSB_TV_KEY_ADD_VOL); // add volume
        // the result will be returned in onPressKeyResult
    }

    private void CC9201SetChannelSample(CC9201 device) {
        // sample code: set the mapping between channel name and channel id
        // for example, 北京卫视: 312
        String name = new String("北京卫视");
        device.SetChannel(name, 312);
        // the result will be returned in onSetChannelResult
    }

    private void CC9201DelChannelSample(CC9201 device) {
        // sample code: delete the mapping between channel name and id
        String name = new String("北京卫视");
        device.DelChannel(name);
        // the result will be returned in onDelChannelResult
    }

    private void CC9201SwitchChannelSample(CC9201 device) {
        // sample code: switch to channel by name
        String name = new String("北京卫视");
        device.SwitchChannel(name);
        // the result will be returned in onSwitchChannelResult
    }

    /* 3.GrayAirConditioner */
    private void InitGrayAirConditioner(GrayAirConditioner device) {
        device.SetListener(new GrayAirConditionerListener() {
            @Override
            public void onSetStatusResult(int errcode) {
                // TODO: add your implementation
                if (errcode == HsbConstant.HSB_E_OK) {
                    // set status success
                } else {
                    // set status failed
                }
            }
        });
    }

    private void GraySetupSample(GrayAirConditioner device) {
        // sample code; setup the properties of gray air conditioner
        device.SetWorkMode(GrayAirConditioner.WORK_MODE_COLD); // set work mode
        // the result will be returned in onSetStatusResult

        device.SetPower(GrayAirConditioner.POWER_ON); // set power on
        // the result will be returned in onSetStatusResult

        device.SetWindSpeed(GrayAirConditioner.WIND_SPEED_LEVEL_1); // set wind speed
        // the result will be returned in onSetStatusResult

        device.SetTemperature(24); // set temperature, must between 16-30
        // the result will be returned in onSetStatusResult

        device.SetLight(GrayAirConditioner.LIGHT_OFF); // set light off
        // the result will be returned in onSetStatusResult
    }
}