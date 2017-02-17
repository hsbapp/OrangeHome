
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class TVDevice extends HsbDevice {

    public TVDevice(Protocol proto, int devid) {
        super(proto, devid);
        mChannelList = new ArrayList<HsbChannel>();
    }

    public boolean PressKey(int key)
    {
        return SetEndpointVal("按键", key);
    }
}