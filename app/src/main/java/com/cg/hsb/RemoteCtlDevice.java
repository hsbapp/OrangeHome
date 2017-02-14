
package com.cg.hsb;

import com.cg.hsb.*;

public class RemoteCtlDevice extends HsbDevice {

    public RemoteCtlDevice(Protocol proto, int devid) {
        super(proto, devid);
        mInfo.SetDevType(HsbConstant.HSB_DEV_TYPE_REMOTE_CTL);
    }
}
