
package com.cg.hsb;


public class HsbDeviceDelay {
    public int mWorkMode;
    public HsbDeviceEvent mEvent;
    public HsbDeviceAction mAction;
    public int mDelaySec;

    public HsbDeviceDelay() {
        mWorkMode = HsbConstant.HSB_WORK_MODE_DEFAULT;
        mDelaySec = 0;
        //mEvent = new HsbDeviceEvent();
        //mAction = new HsbDeviceAction();
    }

    public void SetWorkMode(int WorkMode)
    {
        mWorkMode = WorkMode;
    }

    public void SetDelaySec(int delaySec)
    {
        mDelaySec = delaySec;
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