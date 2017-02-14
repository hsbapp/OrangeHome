
package com.cg.hsb;

public class GrayAirConditionerListener extends HsbDeviceListener {

    public GrayAirConditionerListener() {
        super();
    }

    @Override
    public void onSetStatusResult(int errcode) {}

    public void onWorkModeStatusUpdated(int WorkMode) {}
    public void onPowerStatusUpdated(int power) {}
    public void onTemperatureStatusUpdated(int temperature) {}
    public void onLightStatusUpdated(int light) {}
    public void onWindSpeedStatusUpdated(int WindSpeed) {}
}
