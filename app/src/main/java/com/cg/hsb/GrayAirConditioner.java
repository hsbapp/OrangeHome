
package com.cg.hsb;

import android.util.Log;

import java.util.ArrayList;

public class GrayAirConditioner extends HsbDevice {
    public static final int WORK_MODE_AUTO = 0;
    public static final int WORK_MODE_COLD = 1;
    public static final int WORK_MODE_HUMI = 2;
    public static final int WORK_MODE_WIND = 3;
    public static final int WORK_MODE_HEATING = 4;
    private static final int WORK_MODE_MASK = 0x07;

    public static final int POWER_OFF = 0;
    public static final int POWER_ON = 1;
    private static final int POWER_MASK = 0x01;

    public static final int WIND_SPEED_AUTO = 0;
    public static final int WIND_SPEED_LEVEL_1 = 1;
    public static final int WIND_SPEED_LEVEL_2 = 2;
    public static final int WIND_SPEED_LEVEL_3 = 3;
    public static final int WIND_SPEED_MIN = 0;
    public static final int WIND_SPEED_MAX = 3;
    private static final int WIND_SPEED_MASK = 0x03;

    public static final int TEMPERATURE_MAX = 30;
    public static final int TEMPERATURE_MIN = 16;

    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;
    private static final int LIGHT_MASK = 0x01;

    public static final int WORK_MODE_DEFAULT = WORK_MODE_COLD;
    public static final int POWER_DEFAULT = POWER_OFF;
    public static final int WIND_SPEED_DEFUALT = WIND_SPEED_LEVEL_1;
    public static final int TEMPERATURE_DEFAULT = 26;
    public static final int LIGHT_DEFAULT = LIGHT_ON;

    private static final int STATUS_ID_WORK_MODE = 0;
    private static final int STATUS_ID_POWER = 1;
    private static final int STATUS_ID_TEMPERATURE = 3;
    private static final int STATUS_ID_LIGHT = 4;
    private static final int STATUS_ID_WIND_SPEED = 2;

    private static final String STATUS_NAME_WORK_MODE = "模式";
    private static final String STATUS_NAME_POWER = "电源";
    private static final String STATUS_NAME_WIND_SPEED = "风速";
    private static final String STATUS_NAME_TEMPERATURE = "温度";
    private static final String STATUS_NAME_LIGHT = "灯光";

    private static final String STATUS_UNIT_TEMPERATURE = "度";

    public GrayAirConditioner(Protocol proto, int devid) {
        super(proto, devid);
        mInfo.SetDevType(HsbConstant.HSB_DEV_TYPE_GRAY_AC);

        mStatus = new HsbDeviceStatus(5);
        mStatus.Set(STATUS_ID_WORK_MODE, WORK_MODE_DEFAULT);
        mStatus.Set(STATUS_ID_WIND_SPEED, WIND_SPEED_DEFUALT);
        mStatus.Set(STATUS_ID_POWER, POWER_DEFAULT);
        mStatus.Set(STATUS_ID_TEMPERATURE, TEMPERATURE_DEFAULT);
        mStatus.Set(STATUS_ID_LIGHT, LIGHT_DEFAULT);

        HsbDeviceState state = new HsbDeviceState(devid,
                                                    mStatus,
                                                    STATUS_ID_WORK_MODE,
                                                    STATUS_NAME_WORK_MODE,
                                                    null,
                                                    HsbDeviceState.TYPE_LIST,
                                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.AddVal(WORK_MODE_AUTO, "自动");
        state.AddVal(WORK_MODE_COLD, "制冷");
        state.AddVal(WORK_MODE_HUMI, "加湿");
        state.AddVal(WORK_MODE_WIND, "送风");
        state.AddVal(WORK_MODE_HEATING, "制热");
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    STATUS_ID_POWER,
                                    STATUS_NAME_POWER,
                                    null,
                                    HsbDeviceState.TYPE_LIST,
                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.AddVal(POWER_OFF, "关");
        state.AddVal(POWER_ON, "开");
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    STATUS_ID_WIND_SPEED,
                                    STATUS_NAME_WIND_SPEED,
                                    null,
                                    HsbDeviceState.TYPE_LIST,
                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.AddVal(WIND_SPEED_AUTO, "自动");
        state.AddVal(WIND_SPEED_LEVEL_1, "一级");
        state.AddVal(WIND_SPEED_LEVEL_2, "二级");
        state.AddVal(WIND_SPEED_LEVEL_3, "三级");
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    STATUS_ID_TEMPERATURE,
                                    STATUS_NAME_TEMPERATURE,
                                    STATUS_UNIT_TEMPERATURE,
                                    HsbDeviceState.TYPE_INT,
                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.SetRange(TEMPERATURE_MIN, TEMPERATURE_MAX);
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    STATUS_ID_LIGHT,
                                    STATUS_NAME_LIGHT,
                                    null,
                                    HsbDeviceState.TYPE_LIST,
                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.AddVal(LIGHT_OFF, "开");
        state.AddVal(LIGHT_ON, "关");
        mStates.add(state);

        UpdateCapabilities();
    }

    private void SetAllStatus() {
        SetStatus(mStatus);
    }

    public int WorkMode() {
        return mStatus.Get(STATUS_ID_WORK_MODE);
    }

    public void SetWorkMode(int WorkMode) {
        mStatus.Set(STATUS_ID_WORK_MODE, (WorkMode & WORK_MODE_MASK));
        mStatus.Set(STATUS_ID_POWER, POWER_ON);
        SetAllStatus();
    }

    public int Power() {
        return mStatus.Get(STATUS_ID_POWER);
    }

    public void SetPower(int power) {
        mStatus.Set(STATUS_ID_POWER, (power & POWER_MASK));
        SetAllStatus();
    }

    public int WindSpeed() {
        return mStatus.Get(STATUS_ID_WIND_SPEED);
    }

    public void SetWindSpeed(int WindSpeed) {
        if (WindSpeed < WIND_SPEED_MIN)
            WindSpeed = WIND_SPEED_MIN;
        else if (WindSpeed > WIND_SPEED_MAX)
            WindSpeed = WIND_SPEED_MAX;

        mStatus.Set(STATUS_ID_WIND_SPEED, WindSpeed);
        mStatus.Set(STATUS_ID_POWER, POWER_ON);
        SetAllStatus();
    }

    public int Temperature() {
        return mStatus.Get(STATUS_ID_TEMPERATURE);
    }

    public void AddWindSpeed() {
        SetWindSpeed(WindSpeed() + 1);
    }

    public void DecWindSpeed() {
        SetWindSpeed(WindSpeed() - 1);
    }

    public void SetTemperature(int temp) {
        if (temp < TEMPERATURE_MIN)
            temp = TEMPERATURE_MIN;
        else if (temp > TEMPERATURE_MAX)
            temp = TEMPERATURE_MAX;

        mStatus.Set(STATUS_ID_TEMPERATURE, temp);
        mStatus.Set(STATUS_ID_POWER, POWER_ON);
        SetAllStatus();
    }

    public void AddTemperature() {
        SetTemperature(Temperature() + 1);
    }

    public void DecTemperature() {
        SetTemperature(Temperature() - 1);
    }

    public int Light() {
        return mStatus.Get(STATUS_ID_LIGHT);
    }

    public void SetLight(int light) {
        mStatus.Set(STATUS_ID_LIGHT, (light & LIGHT_MASK));
        SetAllStatus();
    }

    @Override
    public void onStatusUpdated(HsbDeviceStatus status) {
        super.onStatusUpdated(status);

        GrayAirConditionerListener listener = (GrayAirConditionerListener)GetListener();
        if (listener == null)
            return;

        int id;
        for (id = 0; id < status.mNum; id++) {
            int StatusId = status.mId[id];
            int StatusVal = status.mVal[id];
            switch (StatusId) {
                case STATUS_ID_WORK_MODE:
                    listener.onWorkModeStatusUpdated(StatusVal);
                    break;
                case STATUS_ID_POWER:
                    listener.onPowerStatusUpdated(StatusVal);
                    break;
                case STATUS_ID_TEMPERATURE:
                    listener.onTemperatureStatusUpdated(StatusVal);
                    break;
                case STATUS_ID_LIGHT:
                    listener.onLightStatusUpdated(StatusVal);
                    break;
                case STATUS_ID_WIND_SPEED:
                    listener.onWindSpeedStatusUpdated(StatusVal);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public HsbDeviceAction MakePowerAction(boolean power) {
        return new HsbDeviceAction(GetDevId(), STATUS_ID_POWER, (power ? 1 : 0), 0, HsbDeviceAction.FLAG_DEFAULT);
    }

    @Override
    public boolean SupportVR() {
        return true;
    }

    @Override
    public void ConfigVR() {
        ArrayList<String> actions = new ArrayList<String>();
        actions.add("打开");
        actions.add("关闭");
        actions.add("升高");
        actions.add("降低");
        actions.add("加大");
        actions.add("减小");
        actions.add("设置");

        ArrayList<String> objects = new ArrayList<String>();
        objects.add("自动模式");
        objects.add("制冷模式");
        objects.add("制热模式");
        objects.add("加湿模式");
        objects.add("送风模式");
        objects.add("风速");
        objects.add("电源");
        objects.add("温度");
        objects.add("灯光");

        ConfigVoiceRecognizer(actions, objects);
    }

    @Override
    public boolean onVoiceRecognizerResult(String result) {
        Log.e("hsbservice", "onVoiceRecognizerResult:" + result);

        String[] tmp = result.split(",");
        if (2 != tmp.length)
            return false;

        if (tmp[1].equals("电源")) {
            if (tmp[0].equals("打开")) {
                SetPower(POWER_ON);
            } else if (tmp[0].equals("关闭")) {
                SetPower(POWER_OFF);
            }
            return true;
        }

        if (tmp[1].equals("灯光")) {
            if (tmp[0].equals("打开")) {
                SetLight(LIGHT_ON);
            } else if (tmp[0].equals("关闭")) {
                SetLight(LIGHT_OFF);
            }
            return true;
        }

        if (tmp[1].equals("温度")) {
            if (tmp[0].equals("升高")) {
                AddTemperature();
            } else if (tmp[0].equals("降低")) {
                DecTemperature();
            }
            return true;
        }

        if (tmp[1].equals("风速")) {
            if (tmp[0].equals("加大")) {
                AddWindSpeed();
            } else if (tmp[0].equals("减小")) {
                DecWindSpeed();
            }
            return true;
        }

        if (tmp[0].equals("设置")) {
            if (tmp[1].equals("制冷模式")) {
                SetWorkMode(WORK_MODE_COLD);
            } else if (tmp[1].equals("制热模式")) {
                SetWorkMode(WORK_MODE_HEATING);
            } else if (tmp[1].equals("加湿模式")) {
                SetWorkMode(WORK_MODE_HUMI);
            } else if (tmp[1].equals("送风模式")) {
                SetWorkMode(WORK_MODE_WIND);
            } else if (tmp[1].equals("自动模式")) {
                SetWorkMode(WORK_MODE_AUTO);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean SupportPower() {
        return true;
    }

    @Override
    public void SetPowerStatus(boolean power) {
        SetPower(power ? POWER_ON : POWER_OFF);
    }
}
