
package com.cg.hsb;

import android.util.Log;

import com.cg.hsb.*;

import java.util.ArrayList;

public class RelayDevice extends HsbDevice {

    static final int MIN_CHANNEL = 0;
    static final int MAX_CHANNEL = 11;

    public RelayDevice(Protocol proto, int devid) {
        super(proto, devid);
    }

    private boolean ChannelValid(int channel)
    {
        return (channel >=  MIN_CHANNEL && channel <= MAX_CHANNEL);
    }

    public boolean SetPowerStatus(int channel, boolean power)
    {
        if (!ChannelValid(channel))
            return false;

        return SetEndpointVal(channel, (power ? 1 : 0));
    }

    public boolean GetPowerStatus(int channel)
    {
        if (!ChannelValid(channel))
            return false;

        int val = GetEndpointVal(channel);
        return (val > 0) ? true : false;
    }
}

