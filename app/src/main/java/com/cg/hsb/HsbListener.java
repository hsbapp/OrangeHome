
package com.cg.hsb;

import android.util.Log;

public class HsbListener {

    public HsbListener() {}

    public void onHsbOnline(boolean online) {}

    public void onDeviceOnline(HsbDevice device) {}
    public void onDeviceOffline(HsbDevice device) {}

    public void onProbeResult(int errcode) {}
    public void onAddDevResult(int errcode) {}
    public void onDelDevResult(int errcode) {}

    public void onDeviceStatusUpdated(HsbDevice device, HsbDeviceStatus status) {}
}