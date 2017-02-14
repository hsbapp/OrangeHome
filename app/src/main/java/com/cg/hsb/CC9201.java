
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class CC9201 extends HsbDevice {
    private static final int STATUS_ID_CHANNEL = HsbConstant.HSB_TV_STATUS_CHANNEL;
    private static final String STATUS_NAME_CHANNEL = "频道";

    public CC9201(Protocol proto, int devid) {
        super(proto, devid);
        mInfo.SetDevType(HsbConstant.HSB_DEV_TYPE_STB_CC9201);
        mChannelList = new ArrayList<HsbChannel>();
        mStatus = new HsbDeviceStatus(1);

        HsbDeviceState state = new HsbDeviceState(devid,
                                                    mStatus,
                                                    STATUS_ID_CHANNEL,
                                                    STATUS_NAME_CHANNEL,
                                                    null,
                                                    HsbDeviceState.TYPE_INT,
                                                    HsbDeviceState.ACCESS_MODE_WRITABLE);
        state.SetRange(0, 999);

        mStates.add(state);

        UpdateCapabilities();
    }

    public void SetChannelStatus(int channel) {
        mStatus.Set(STATUS_ID_CHANNEL, channel);
        SetStatus(mStatus);
    }

    public int ChannelStatus() {
        return mStatus.Get(STATUS_ID_CHANNEL);
    }

    public HsbDeviceAction MakeSetChannelStatusAction(int channel) {
        return new HsbDeviceAction(GetDevId(), STATUS_ID_CHANNEL, channel, 0, HsbDeviceAction.FLAG_DEFAULT);
    }

    public HsbDeviceAction MakeActionPressKey(int key) {
        return new HsbDeviceAction(GetDevId(),
                                     HsbConstant.HSB_TV_ACTION_PRESS_KEY,
                                     key,
                                     0,
                                     HsbDeviceAction.FLAG_DO_ACTION);
    }

    @Override
    public void onStatusUpdated(HsbDeviceStatus status) {
        super.onStatusUpdated(status);

        int channel = status.Get(STATUS_ID_CHANNEL);

       CC9201Listener listener = (CC9201Listener)GetListener();
        if (listener != null)
            listener.onChannelStatusUpdated(channel);
    }

    public void PressKey(int key)
    {
        DoAction(new HsbDeviceAction(GetDevId(), HsbConstant.HSB_TV_ACTION_PRESS_KEY, key, 0, HsbDeviceAction.FLAG_DEFAULT));
    }

    @Override
    public void onSetStatusResult(int errcode) {
        CC9201Listener listener = (CC9201Listener)GetListener();
        if (listener != null)
            listener.onSetChannelStatusResult(errcode);
    }

    @Override
    public void onDoActionResult(int errcode) {
        CC9201Listener listener = (CC9201Listener)GetListener();
        if (listener != null)
            listener.onPressKeyResult(errcode);
    }

    @Override
    public boolean SupportVR() {
        return true;
    }

    @Override
    public void ConfigVR() {
        ArrayList<String> actions = new ArrayList<String>();
        actions.add("看");
        actions.add("关闭");
        actions.add("打开");
        actions.add("加大");
        actions.add("减小");

        ArrayList<String> objects = new ArrayList<String>();
        objects.add("电源");
        objects.add("音量");

        int id;
        HsbChannel channel = null;
        for (id = 0; id < mChannelList.size(); id++) {
            channel = mChannelList.get(id);
            objects.add(channel.GetName());
        }

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
                PressKey(HsbConstant.HSB_TV_KEY_ON_OFF);
            } else if (tmp[0].equals("关闭")) {
                PressKey(HsbConstant.HSB_TV_KEY_ON_OFF);
            }
            return true;
        }

        if (tmp[1].equals("音量")) {
            if (tmp[0].equals("加大")) {
                PressKey(HsbConstant.HSB_TV_KEY_ADD_VOL);
            } else if (tmp[0].equals("减小")) {
                PressKey(HsbConstant.HSB_TV_KEY_DEC_VOL);
            }
            return true;
        }

        if (tmp[0].equals("看")) {
            SwitchChannel(tmp[1]);
            return true;
        }

        return false;
    }
}