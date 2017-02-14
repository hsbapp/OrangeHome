
package com.cg.hsb;

import android.os.SystemClock;

import com.cg.hsb.*;

import java.util.ArrayList;

public class  HsbDevice {
    protected int mDevId;

    protected HsbDeviceInfo mInfo;
    protected HsbDeviceConfig mConfig;
    protected HsbDeviceStatus mStatus;

    protected boolean mOnline;
    protected ArrayList<HsbChannel> mChannelList;
    protected ArrayList<HsbDeviceState> mStates;

    protected Protocol mProto = null;
    protected HsbDeviceListener mListener = null;
    protected boolean mSupportCondition;
    protected boolean mSupportAction;

    public HsbDevice(Protocol proto, int devid) {
        mProto = proto;
        mOnline = true;
        mDevId = devid;
        mInfo = new HsbDeviceInfo();
        mConfig = new HsbDeviceConfig();
        mChannelList = null;
        mStatus = null;
        mStates = new ArrayList<HsbDeviceState>();
        mSupportCondition = false;
        mSupportAction = false;
    }

    public void Offline()
    {
        mOnline = false;
    }

    public boolean Available() {
        if (null != mProto && mOnline)
            return true;
        return false;
    }

    public void SetListener(HsbDeviceListener listener) {
        mListener = listener;
    }

    public HsbDeviceListener GetListener() {
        return mListener;
    }

    public int GetDevId() {
        return mDevId;
    }

    public int GetDevType() {
        return mInfo.GetDevType();
    }

    public HsbDeviceState GetState(int id) {
        HsbDeviceState state = null;
        int cnt;

        for (cnt = 0; cnt < mStates.size(); cnt++) {
            state = mStates.get(cnt);
            if (state.GetID() == id)
                return state;
        }

        return null;
    }

    public ArrayList<HsbDeviceState> GetState() {
        return mStates;
    }

    public void UpdateCapabilities() {
        int id;
        HsbDeviceState state = null;
        for (id = 0; id < mStates.size(); id++) {
            state = mStates.get(id);
            if (!mSupportCondition && state.Readable())
                mSupportCondition = true;
            if (!mSupportAction && state.Writable())
                mSupportAction = true;
        }
    }

    public boolean SupportCondition() {
        return mSupportCondition;
    }

    public boolean SupportAction() {
        return mSupportAction;
    }

    public void GetStatus() {
        mProto.GetDeviceStatus(mDevId);
    }

    public void SetStatus(HsbDeviceStatus status) {
        if (!Available())
            return;
        mProto.SetDeviceStatus(mDevId, status);
    }

    public void onStatusUpdated(HsbDeviceStatus status) {
        if (mStatus != null) {
            mStatus.Updated(status);
        }
    }

    public boolean SupportPower() {
        return false;
    }

    public void SetPowerStatus(boolean power) {}

    public HsbDeviceAction MakePowerAction(boolean power) {
        return null;
    }

    public void GetInfo(HsbDeviceInfo info) {
        info.Set(mInfo);
    }

    public void GetInfo() {
        if (!Available())
            return;
        mProto.GetDeviceInfo(mDevId);
    }

    public void onInfoUpdated(HsbDeviceInfo info)
    {
        mInfo.Set(info);
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onInfoUpdated(info);
    }

    public void SetConfig(HsbDeviceConfig config)
    {
        if (!Available())
            return;
        mProto.SetDeviceConfig(mDevId, config);

        onConfigUpdated(config);
    }

    public void GetConfig()
    {
        if (!Available())
            return;
        mProto.GetDeviceConfig(mDevId);
    }

    public void onConfigUpdated(HsbDeviceConfig config)
    {
        mConfig.Set(config);
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onConfigUpdated(config);
    }

    public void GetConfig(HsbDeviceConfig config) {
        config.Set(mConfig);
    }

    public String GetName() {
        return mConfig.GetName();
    }

    public String GetLocation() {
        return mConfig.GetLocation();
    }

    public boolean SupportChannel() {
        return (mChannelList != null);
    }

    public ArrayList<HsbChannel> GetChannelList() {
        return mChannelList;
    }

    public void GetChannel() {
        if (!Available() || !SupportChannel())
            return;

        mProto.GetDeviceChannel(mDevId);
    }

    public void onChannelUpdated(String name, int id) {
        if (!SupportChannel())
            return;

        HsbChannel channel = new HsbChannel(name, id);
        int index = mChannelList.indexOf(channel);

        if (index < 0) {
            mChannelList.add(channel);
        } else {
            channel = mChannelList.get(index);
            channel.mId = id;
        }
    }

    public void SetChannel(String name, int id)
    {
        if (!Available() || !SupportChannel())
            return;

        mProto.SetDeviceChannel(mDevId, name, id);
    }

    public void DelChannel(String name)
    {
        if (!Available() || !SupportChannel())
            return;

        mProto.DelDeviceChannel(mDevId, name);

        int index = mChannelList.indexOf(new HsbChannel(name, 0));
        if (index < 0)
            return;
        mChannelList.remove(index);
    }

    public void SwitchChannel(String name)
    {
        if (!Available() || !SupportChannel())
            return;

        mProto.SwitchDeviceChannel(mDevId, name);
    }

    public void GetTimer(int id)
    {
        if (!Available())
            return;
        mProto.GetDeviceTimer(mDevId, id);
    }

    public void SetTimer(int id, HsbDeviceTimer timer)
    {
        if (!Available())
            return;
        mProto.SetDeviceTimer(mDevId, id, timer);
    }

    public void DelTimer(int id)
    {
        if (!Available())
            return;
        mProto.DelDeviceTimer(mDevId, id);
    }

    public void onTimerUpdated(int id, HsbDeviceTimer timer)
    {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onTimerUpdated(id, timer);
    }

    public void GetDelay(int id)
    {
        if (!Available())
            return;
        mProto.GetDeviceDelay(mDevId, id);
    }

    public void SetDelay(int id, HsbDeviceDelay delay)
    {
        if (!Available())
            return;
        mProto.SetDeviceDelay(mDevId, id, delay);
    }

    public void DelDelay(int id)
    {
        if (!Available())
            return;
        mProto.DelDeviceDelay(mDevId, id);
    }

    public void onDelayUpdated(int id, HsbDeviceDelay delay)
    {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDelayUpdated(id, delay);
    }

    public void GetLinkage(int id)
    {
        if (!Available())
            return;
        mProto.GetDeviceLinkage(mDevId, id);
    }

    public void SetLinkage(int id, HsbDeviceLinkage link)
    {
        if (!Available())
            return;
        mProto.SetDeviceLinkage(mDevId, id, link);
    }

    public void DelLinkage(int id)
    {
        if (!Available())
            return;
        mProto.DelDeviceLinkage(mDevId, id);
    }

    public void onLinkageUpdated(int id, HsbDeviceLinkage link)
    {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onLinkageUpdated(id, link);
    }

    public void DoAction(HsbDeviceAction action)
    {
        if (!Available())
            return;
        mProto.DoAction(mDevId, action);
    }

    public void onSensorTiggered(int param1, int param2) {

    }

    public void onSensorRecovered(int param1, int param2) {

    }

    public void onGetInfoError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetInfoError(errcode);
    }

    public void onSetConfigResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetConfigResult(errcode);
    }

    public void onGetConfigError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetConfigError(errcode);
    }

    public void onGetStatusError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetStatusError(errcode);
    }

    public void onSetStatusResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetStatusResult(errcode);
    }

    public void onSetChannelResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetChannelResult(errcode);
    }

    public void onDelChannelResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDelChannelResult(errcode);
    }

    public void onSwitchChannelResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSwitchChannelResult(errcode);
    }

    public void onGetChannelResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetChannelResult(errcode);
    }

    public void onGetTimerError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetTimerError(errcode);
    }

    public void onSetTimerResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetTimerResult(errcode);
    }

    public void onDelTimerResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDelTimerResult(errcode);
    }

    public void onGetDelayError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetDelayError(errcode);
    }

    public void onSetDelayResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetDelayResult(errcode);
    }

    public void onDelDelayResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDelDelayResult(errcode);
    }

    public void onGetLinkageError(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onGetLinkageError(errcode);
    }

    public void onSetLinkageResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onSetLinkageResult(errcode);
    }

    public void onDelLinkageResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDelLinkageResult(errcode);
    }

    public void onDoActionResult(int errcode) {
        HsbDeviceListener listener = GetListener();
        if (listener != null)
            listener.onDoActionResult(errcode);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof HsbDevice)
        {
            sameSame = (this.mDevId == ((HsbDevice) object).mDevId);
        }

        return sameSame;
    }

    /* Voice Recognizer related APIs */
    public boolean SupportVR() {
        return false;
    }

    public void ConfigVR() {}

    public void ConfigVoiceRecognizer(ArrayList<String> actions, ArrayList<String> objects) {
        mProto.ConfigVoiceRecognizer(this, actions, objects);
    }

    public boolean onVoiceRecognizerResult(String result) {
        return false;
    }

    /* Channel */
    public class HsbChannel {
        private String mName;
        private int mId;

        public HsbChannel(String name, int id) {
            mName = name;
            mId = id;
        }

        public String GetName(){
            return mName;
        }

        public int GetId() {
            return mId;
        }

        @Override
        public boolean equals(Object object)
        {
            boolean sameSame = false;

            if (object != null && object instanceof HsbChannel)
            {
                sameSame = (this.mName.equals(((HsbChannel)object).mName));
            }

            return sameSame;
        }
    }
}