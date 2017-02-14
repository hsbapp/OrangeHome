
package com.cg.hsb;


public class HsbDeviceLinkage {
    public int mWorkMode;
    public HsbDeviceEvent mEvent;
    public HsbDeviceAction mAction;
    public int mActDevId;

    public HsbDeviceLinkage() {
        mWorkMode = HsbConstant.HSB_WORK_MODE_DEFAULT;
        mActDevId = 0;
        //mEvent = new HsbDeviceEvent();
        //mAction = new HsbDeviceAction();
    }

    public void SetWorkMode(int WorkMode)
    {
        mWorkMode = WorkMode;
    }

    public void SetActDevId(int devid)
    {
        mActDevId = devid;
    }

    public void SetEvent(HsbDeviceEvent event)
    {
        mEvent.Set(event);
    }

    public void SetAction(HsbDeviceAction action)
    {
        mAction.Set(action);
    }
}