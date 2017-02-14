
package com.cg.hsb;

public class HsbDeviceListener {
    public HsbDeviceListener() {}

    public void onInfoUpdated(HsbDeviceInfo info) {}
    public void onConfigUpdated(HsbDeviceConfig config) {}
    public void onTimerUpdated(int id, HsbDeviceTimer timer) {}
    public void onDelayUpdated(int id, HsbDeviceDelay delay) {}
    public void onLinkageUpdated(int id, HsbDeviceLinkage link) {}

    public void onGetInfoError(int errcode) {}
    public void onSetConfigResult(int errcode) {}
    public void onGetConfigError(int errcode) {}
    public void onGetStatusError(int errcode) {}
    public void onSetStatusResult(int errcode) {}
    public void onSetChannelResult(int errcode) {}
    public void onDelChannelResult(int errcode) {}
    public void onSwitchChannelResult(int errcode) {}
    public void onGetChannelResult(int errcode) {}
    public void onGetTimerError(int errcode) {}
    public void onSetTimerResult(int errcode) {}
    public void onDelTimerResult(int errcode) {}
    public void onGetDelayError(int errcode) {}
    public void onSetDelayResult(int errcode) {}
    public void onDelDelayResult(int errcode) {}
    public void onGetLinkageError(int errcode) {}
    public void onSetLinkageResult(int errcode) {}
    public void onDelLinkageResult(int errcode) {}
    public void onDoActionResult(int errcode) {}
    public void onAddDevResult(int errcode) {}
}