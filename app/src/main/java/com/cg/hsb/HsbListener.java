
package com.cg.hsb;

import android.util.Log;

public class HsbListener {

    public HsbListener() {}

    public void onHsbOnline(boolean online) {}

    public void onDeviceOnline(HsbDevice device) {}
    public void onDeviceOffline(HsbDevice device) {}

    public void onAddDevResult(int errcode) {}
    public void onDelDevResult(int errcode) {}

    public void onDeviceUpdated(HsbDevice device) {}
}