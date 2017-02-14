
package com.cg.hsb;

import com.cg.hsb.*;

public class SensorDevice extends HsbDevice {
    public final int PM25_STATUS_ID = 0;
    public final int TEMP_STATUS_ID = 1;
    public final int HUMIDITY_STATUS_ID = 2;
    public final int GAS_STATUS_ID = 3;

    private static final String PM25_STATUS_NAME = "PM2.5";
    private static final String TEMP_STATUS_NAME = "温度";
    private static final String HUMIDITY_STATUS_NAME = "湿度";
    private static final String GAS_STATUS_NAME = "燃气";

    private static final String TEMP_STATUS_UNIT = "度";

    public SensorDevice(Protocol proto, int devid) {
        super(proto, devid);
        mInfo.SetDevType(HsbConstant.HSB_DEV_TYPE_SENSOR);
        mStatus = new HsbDeviceStatus(4);

        HsbDeviceState state = new HsbDeviceState(devid,
                                                    mStatus,
                                                    PM25_STATUS_ID,
                                                    PM25_STATUS_NAME,
                                                    null,
                                                    HsbDeviceState.TYPE_INT,
                                                    HsbDeviceState.ACCESS_MODE_READABLE);
        state.SetRange(0, 1000);
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    TEMP_STATUS_ID,
                                    TEMP_STATUS_NAME,
                                    TEMP_STATUS_UNIT,
                                    HsbDeviceState.TYPE_INT,
                                    HsbDeviceState.ACCESS_MODE_READABLE);
        state.SetRange(0, 50);
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    HUMIDITY_STATUS_ID,
                                    HUMIDITY_STATUS_NAME,
                                    null,
                                    HsbDeviceState.TYPE_INT,
                                    HsbDeviceState.ACCESS_MODE_READABLE);
        state.SetRange(0, 100);
        mStates.add(state);

        state = new HsbDeviceState(devid,
                                    mStatus,
                                    GAS_STATUS_ID,
                                    GAS_STATUS_NAME,
                                    null,
                                    HsbDeviceState.TYPE_INT,
                                    HsbDeviceState.ACCESS_MODE_READABLE);
        state.SetRange(0, 200);
        mStates.add(state);

        UpdateCapabilities();
    }

    public int PM25Status() {
        return mStatus.Get(PM25_STATUS_ID);
    }

    public int TemperatureStatus() {
        return mStatus.Get(TEMP_STATUS_ID);
    }

    public int HumidityStatus() {
        return mStatus.Get(HUMIDITY_STATUS_ID);
    }

    public int GasStatus() {
        return mStatus.Get(GAS_STATUS_ID);
    }

    @Override
    public void onStatusUpdated(HsbDeviceStatus status) {
        super.onStatusUpdated(status);

        SensorDeviceListener listener = (SensorDeviceListener)GetListener();
        if (listener == null)
            return;

        for (int id = 0; id < status.mNum; id++) {
            switch (status.mId[id]) {
                case PM25_STATUS_ID:
                    listener.onPm25StatusUpdated(status.mVal[id]);
                    break;
                case TEMP_STATUS_ID:
                    listener.onTempStatusUpdated(status.mVal[id]);
                    break;
                case HUMIDITY_STATUS_ID:
                    listener.onHumidityStatusUpdated(status.mVal[id]);
                    break;
                case GAS_STATUS_ID:
                    listener.onGasStatusUpdated(status.mVal[id]);
                    break;
                default:
                    break;
            }
        }
    }
}