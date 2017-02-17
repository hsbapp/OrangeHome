
package com.cg.hsb;

import com.cg.hsb.*;

public class SensorDevice extends HsbDevice {

    public SensorDevice(Protocol proto, int devid) {
        super(proto, devid);
    }

    public int GetPM25()
    {
        return GetEndpointVal("PM2.5");
    }

    public int GetTemperature()
    {
        return GetEndpointVal("温度");
    }

    public int GetHumidity()
    {
        return GetEndpointVal("湿度");
    }

    public int GetGas()
    {
        return GetEndpointVal("燃气");
    }
}