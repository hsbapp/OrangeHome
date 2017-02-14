
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class PlugDevice extends HsbDevice {
    private static final int STATUS_ID_POWER = 0;
    private static final String STATE_NAME_POWER = "电源";

    public PlugDevice(Protocol proto, int devid) {
        super(proto, devid);
        mInfo.SetDevType(HsbConstant.HSB_DEV_TYPE_PLUG);
        mStatus = new HsbDeviceStatus(1);

        HsbDeviceState state = new HsbDeviceState(devid,
                                                  mStatus,
                                                  STATUS_ID_POWER,
                                                  STATE_NAME_POWER,
                                                  null,
                                                  HsbDeviceState.TYPE_LIST,
                                                  HsbDeviceState.ACCESS_MODE_DEFAULT);
        state.AddVal(0, "关");
        state.AddVal(1, "开");

        mStates.add(state);

        UpdateCapabilities();
    }

    @Override
    public boolean SupportPower() {
        return true;
    }

    @Override
    public void SetPowerStatus(boolean power) {
        mStatus.Set(STATUS_ID_POWER, (power ? 1 : 0));
        SetStatus(mStatus);
    }

    public void GetPowerStatus() {
        GetStatus();
    }

    public boolean PowerStatus() {
        return (mStatus.Get(STATUS_ID_POWER) > 0) ? true : false;
    }

    @Override
    public void onStatusUpdated(HsbDeviceStatus status) {
        super.onStatusUpdated(status);

        boolean power = (status.mVal[0] > 0) ? true : false;

        PlugDeviceListener listener = (PlugDeviceListener)GetListener();
        if (listener != null)
            listener.onPowerStatusUpdated(power);
    }

    @Override
    public void onSetStatusResult(int errcode) {
        PlugDeviceListener listener = (PlugDeviceListener)GetListener();
        if (listener != null)
            listener.onSetPowerStatusResult(errcode);
    }

    @Override
    public void onGetStatusError(int errcode) {
        PlugDeviceListener listener = (PlugDeviceListener)GetListener();
        if (listener != null)
            listener.onGetPowerStatusError(errcode);
    }

    @Override
    public HsbDeviceAction MakePowerAction(boolean power) {
        return new HsbDeviceAction(GetDevId(), STATUS_ID_POWER, (power ? 1 : 0), 0, HsbDeviceAction.FLAG_DEFAULT);
    }

    public HsbDeviceEvent MakeEventPowerStatusChange(boolean power) {
        return new HsbDeviceEvent(GetDevId(),
                                    HsbConstant.HSB_EVT_STATUS_UPDATED,
                                    STATUS_ID_POWER,
                                    (power ? 1 : 0));
    }

    @Override
    public boolean SupportVR() {
        return true;
    }

    @Override
    public void ConfigVR() {
        ArrayList<String> actions = new ArrayList<String>();
        actions.add("关闭");
        actions.add("打开");

        ArrayList<String> objects = new ArrayList<String>();
        objects.add("电源");

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
                SetPowerStatus(true);
            } else if (tmp[0].equals("关闭")) {
                SetPowerStatus(false);
            }

            return true;
        }

        return false;
    }
}