
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class PlugDevice extends HsbDevice {

    public PlugDevice(Protocol proto, int devid) {
        super(proto, devid);
    }

    public boolean SetPowerStatus(boolean power)
    {
        return SetEndpointVal(0, (power ? 1 : 0));
    }

    public boolean GetPowerStatus()
    {
        int val = GetEndpointVal(0);
        return (val > 0) ? true : false;
    }
}

