
package com.cg.hsb;

public class HsbDeviceConfig {
    private String mName;
    private String mLocation;

    public HsbDeviceConfig() {
        mName = new String("");
        mLocation = new String("");
    }

    public HsbDeviceConfig(String name, String location) {
        mName = name;
        mLocation = location;
    }

    public HsbDeviceConfig(HsbDeviceConfig config) {
        Set(config);
    }

    public String GetName() {
        return mName;
    }

    public String GetLocation() {
        return mLocation;
    }

    public void Set(HsbDeviceConfig config) {
        mName = config.GetName();
        mLocation = config.GetLocation();
    }

    public void SetName(String name) {
        mName = name;
    }

    public void SetLocation(String location) {
        mLocation = location;
    }
}