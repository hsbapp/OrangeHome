
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class CurtainDevice extends HsbDevice {

    public CurtainDevice(Protocol proto, int devid) {
        super(proto, devid);
    }

    public boolean SetOpenStatus(int percent)
    {
        int val = 0;
        if (percent < 0)
            val = 0;
        else if (percent > 100)
            val = 100;
        else
            val = percent;

        return SetEndpointVal(0, percent);
    }

    public int GetOpenStatus()
    {
        int val = GetEndpointVal(0);
        return val;
    }
}

