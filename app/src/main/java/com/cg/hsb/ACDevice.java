
package com.cg.hsb;

import android.util.Log;

import java.util.ArrayList;

public class ACDevice extends HsbDevice {

    public ACDevice(Protocol proto, int devid) {
        super(proto, devid);
    }

    public String GetWorkMode()
    {
        return GetEndpointValDesc("模式");
    }

    public boolean SetWorkMode(String mode)
    {
        return SetEndpointVal("模式", mode);
    }

    public boolean NextWorkMode()
    {
        return AddEndpointVal("模式");
    }

    public boolean PrevWorkMode()
    {
        return DecEndpointVal("模式");
    }

    public boolean GetPower()
    {
        return GetEndpointVal("电源") > 0 ? true : false;
    }

    public boolean SetPower(boolean power)
    {
        return SetEndpointVal("电源", (power ? 1: 0));
    }

    public String GetWindSpeed()
    {
        return GetEndpointValDesc("风速");
    }

    public boolean SetWindSpeed(String speed) {
        return SetEndpointVal("风速", speed);
    }

    public int GetTemperature()
    {
        return GetEndpointVal("温度");
    }

    public boolean NextWindSpeed()
    {
        return AddEndpointVal("风速");
    }

    public boolean PrevWindSpeed()
    {
        return DecEndpointVal("风速");
    }

    public boolean SetTemperature(int temp)
    {
       return SetEndpointVal("温度", temp);
    }

    public boolean AddTemperature()
    {
        return AddEndpointVal("温度");
    }

    public boolean DecTemperature()
    {
        return DecEndpointVal("温度");
    }

    public boolean GetLight()
    {
        return GetEndpointVal("灯光") > 0 ? true : false;
    }

    public boolean SetLight(boolean light)
    {
        return SetEndpointVal("灯光", (light ? 1: 0));
    }
}
