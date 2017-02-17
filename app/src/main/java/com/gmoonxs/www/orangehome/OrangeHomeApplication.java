package com.gmoonxs.www.orangehome;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.cg.hsb.HsbConstant;
import com.cg.hsb.HsbDevice;
import com.cg.hsb.HsbListener;
import com.cg.hsb.HsbScene;
import com.cg.hsb.HsbService;
import com.cg.hsb.Protocol;
import com.cg.hsb.SensorDevice;


import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 威 on 2016/6/25.
 */
public class OrangeHomeApplication extends Application {

    private static OrangeHomeApplication orangeHomeApplication;
    private static Context context;
    private ArrayList<HsbDevice> devList;
    private Protocol mProto = null;
    private ServiceConnection conn;
    private HsbService mService = null;
    private boolean isReady;
    private HsbListener hsbListener;

    private HsbScene middleHsbScene;
    private boolean isSceneEdit;
    private Weather weather;
    private LogsDB logsDB;
    private Cursor logsCursor;

    private SensorDevice sensorDevice;
    private SensorListener sensorListener;
    private SensorListenerForFirstPageFragment sensorListenerForFirstPageFragment;
    private boolean isFristPageMent = false;
    private HsbOffLineListener hsbOffLineListener;
    private SoundPool soundPool;// 声明一个SoundPool,SoundPool最大只能申请1M的内存空间
    private int musicId;// 定义一个整型用load（）；来设置suondID

    @Override
    public void onCreate() {
        super.onCreate();
        orangeHomeApplication = this;
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        soundPool.load(this, R.raw.welcome, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级

        isReady = false;
        initLogDB();

        setServiceConnection();

        Intent intent = new Intent();
        intent.setAction("com.cg.hsb.service.HSB_SERVICE");
        intent.setPackage(getPackageName());
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        isSceneEdit = false;
        playSound();
    }

    public static OrangeHomeApplication getOrangeHomeApplication()
    {
        return orangeHomeApplication;
    }

    public ArrayList<HsbDevice> getDevList()
    {
        return devList;
    }

    public void setDevList(ArrayList<HsbDevice> devList)
    {
        this.devList = devList;
    }

    public boolean isReady()
    {
        return isReady;
    }

    public void setIsReady(boolean isReady)
    {
        this.isReady = isReady;
    }

    private void setServiceConnection(){
        Log.d("startServiceConnection","ServiceConnection");
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((HsbService.HsbBinder)service).getService();
                mProto = ((HsbService.HsbBinder)service).getProtocol();
                mProto.SetListener(new HsbListener() {
                    @Override
                    public void onDeviceOnline(HsbDevice device) {
                        String dev_type = device.GetDevType();
                        Log.d("hsbservice", "onDeviceOnline: " + dev_type);
                        notifyOnline(device);
                    }

                    @Override
                    public void onDeviceOffline(HsbDevice device) {
                        int dev_id = device.GetDevId();
                        Log.d("hsbservice", "onDeviceOffline: " + dev_id);
                        notifyOffline(device);
                    }

                    @Override
                    public void onHsbOnline(boolean online) {
                        OrangeHomeApplication.this.isReady = online;
                        if (online == false) {
                            if (hsbOffLineListener != null) {
                                hsbOffLineListener.hsbOffLine();
                            }
                            notifyHsbOffline();
                        }
                    }

                    @Override
                    public void onDeviceUpdated(HsbDevice device)
                    {

                    }
                });

                isReady = true;
                GetDevicesPrty();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                conn = null;
                isReady = false;
                Intent intent = new Intent(OrangeHomeApplication.this, IndexActivity.class);
                startActivity(intent);
            }
        };
    }


    public HsbScene getMiddleHsbScene()
    {
        if (middleHsbScene == null)
        {
            middleHsbScene = new HsbScene("自定义模式");
        }

        return middleHsbScene;
    }

    public void setMiddleHsbScene(HsbScene middleHsbScene)
    {
        this.middleHsbScene = middleHsbScene;
    }

    private void GetDevicesPrty() {
        devList = mProto.GetDeviceList();
        HsbDevice device = null;

       /* for (int id = 0; id < devList.size(); id++) {
            device = devList.get(id);

            // Get name
            String name = device.GetName();
            // Get location
            String location = device.GetLocation();

            switch (device.GetDevType()) {
                case HsbConstant.HSB_DEV_TYPE_PLUG: {
                    PlugDevice plug = (PlugDevice) device;

                    // get power status
                    boolean power = plug.GetPowerStatus();

                    // set power status
                    plug.SetPowerStatusAsync(power);

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_SENSOR: {
                    SensorDevice sensor = (SensorDevice) device;

                    // get pm2.5 status
                    int pm25 = sensor.GetPM25Status();
                    // get temperature status
                    int temp = sensor.GetTemperatureStatus();
                    // get humidity status
                    int humidity = sensor.GetHumidityStatus();
                    // get gas status
                    int gas = sensor.GetGasStatus();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_STB_CC9201: {
                    CC9201 stb = (CC9201) device;

                    // get tv channel id
                    int channel = stb.GetChannelStatus();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_GRAY_AC: {
                    GrayAirConditioner ac = (GrayAirConditioner) device;

                    // get work mode
                    int WorkMode = ac.GetWorkMode();
                    // get power
                    int power = ac.GetPower();
                    // get wind speed
                    int WindSpeed = ac.GetWindSpeed();
                    // get temperature
                    int temp = ac.GetTemperature();
                    // get light
                    int light = ac.GetLight();

                    break;
                }
                case HsbConstant.HSB_DEV_TYPE_REMOTE_CTL:
                    // nothing to do
                    break;
                default:
                    break;
            }
        }*/
    }

    public Protocol getmProto()
    {
        return mProto;
    }

    public void setmProto(Protocol mProto)
    {
        this.mProto = mProto;
    }

    public void notifyOnline(HsbDevice device)
    {
        notifyOnlineExecute(device.GetName(), device.GetLocation());

        String devtype = device.GetDevType();
        if (devtype == HsbConstant.HSB_DEV_TYPE_SENSOR)
        {
                /*if (isFristPageMent){
                    Intent intent =new Intent(OrangeHomeApplication.this,IndexActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }*/
            if (sensorListenerForFirstPageFragment != null) {
                sensorListenerForFirstPageFragment.sensorOnLine();
            }

            if (sensorListener != null) {
                sensorListener.sensorOnLine();
            }
        }
    }

    private void notifyOnlineExecute(String devName, String devLocation)
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("设备上线提醒")
                .setContentText("在"+devLocation+"的"+devName+"设备上线了！")
                .setAutoCancel(true)
                .setTicker("在"+devLocation+"的"+devName+"设备上线了！")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.orange);
        Intent intent = new Intent(OrangeHomeApplication.this,IndexActivity.class);
        intent.putExtra("tab",2);
        PendingIntent pendingIntent = PendingIntent.getActivity(OrangeHomeApplication.this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void notifyOffline(HsbDevice device)
    {
        notifyOfflineExecute(device.GetName(), device.GetLocation());

        String devtype = device.GetDevType();
        if (devtype == HsbConstant.HSB_DEV_TYPE_SENSOR)
        {
            sensorDevice = null;

            if (sensorListenerForFirstPageFragment != null) {
                sensorListenerForFirstPageFragment.sensorOffLine();
            }

                /*if (isFristPageMent){
                    Intent intent =new Intent(OrangeHomeApplication.this,IndexActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }*/
            if (sensorListener != null) {
                sensorListener.sensorOffLine();
            }
        }
    }

    private void notifyOfflineExecute(String devName, String devLocation)
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("设备下线提醒")
                .setContentText("在"+devLocation+"的"+devName+"设备下线了！")
                .setAutoCancel(true)
                .setTicker("在"+devLocation+"的"+devName+"设备下线了！")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.orange);
        Intent intent = new Intent(OrangeHomeApplication.this,IndexActivity.class);
        intent.putExtra("tab",2);
        PendingIntent pendingIntent = PendingIntent.getActivity(OrangeHomeApplication.this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void notifyHsbOffline()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("HSB断线提醒")
                .setContentText("HSB已断开连接！")
                .setAutoCancel(true)
                .setTicker("HSB已断开连接！")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.orange);
        Intent intent = new Intent(OrangeHomeApplication.this,IndexActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(OrangeHomeApplication.this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());

        Intent intent1=new Intent(OrangeHomeApplication.this,IndexActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
    }

    private void initLogDB()
    {
        logsDB = new LogsDB(this);
        //logsDB.deleteDatabase(this);
        logsCursor = logsDB.select();
    }

    public LogsDB getLogsDB() {
        return logsDB;
    }

    public Cursor getLogsCursor() {
        return logsCursor;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void addToLogsDB(String operation)
    {
        OrangeHomeApplication.getOrangeHomeApplication().getLogsDB().insert(operation);
        Cursor mCursor = OrangeHomeApplication.getOrangeHomeApplication().getLogsCursor();
        mCursor.requery();
    }

    public SensorDevice getSensorDevice() {
        return sensorDevice;
    }

    public void setSensorDevice(SensorDevice sensorDevice) {
        this.sensorDevice = sensorDevice;
    }

    public boolean isSceneEdit() {
        return isSceneEdit;
    }

    public void setIsSceneEdit(boolean isSceneEdit) {
        this.isSceneEdit = isSceneEdit;
    }

    public boolean isFristPageMent() {
        return isFristPageMent;
    }

    public void setIsFristPageMent(boolean isFristPageMent) {
        this.isFristPageMent = isFristPageMent;
    }

    public SensorListener getSensorListener() {
        return sensorListener;
    }

    public void setSensorListener(SensorListener sensorListener) {
        this.sensorListener = sensorListener;
    }

    public SensorListenerForFirstPageFragment getSensorListenerForFirstPageFragment() {
        return sensorListenerForFirstPageFragment;
    }

    public void setSensorListenerForFirstPageFragment(SensorListenerForFirstPageFragment sensorListenerForFirstPageFragment) {
        this.sensorListenerForFirstPageFragment = sensorListenerForFirstPageFragment;
    }


    public HsbOffLineListener getHsbOffLineListener() {
        return hsbOffLineListener;
    }

    public void setHsbOffLineListener(HsbOffLineListener hsbOffLineListener) {
        this.hsbOffLineListener = hsbOffLineListener;
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public void playSound(){
        new Thread(new Runnable(){
            public void run(){
                try {
                    soundPool.play(1, 1, 1, 0, 0, 1);

                }catch (Exception e){

                }
            }

        }).start();
    }
}
