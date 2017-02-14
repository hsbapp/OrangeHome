
package com.cg.hsb;

public class HsbDeviceInfo {
    public int mDrvId;
    public int mDevType;
    public int mClass;
    public int mInterface;
    public byte[] mMac;

    public HsbDeviceInfo() {
        mDrvId = 0;
        mDevType = 0;
        mClass = 0;
        mInterface = 0;
        mMac = new byte[8];
    }

    public HsbDeviceInfo(HsbDeviceInfo info) {
        mMac = new byte[8];
        Set(info);
    }

    public HsbDeviceInfo(int drvid, int devtype, int cls, int intf, byte[] mac) {
        mDrvId = drvid;
        mDevType = devtype;
        mClass = cls;
        mInterface = intf;
        mMac = new byte[8];
        System.arraycopy(mac, 0, mMac, 0, 8);
    }

    public void Set(HsbDeviceInfo info) {
        mDrvId = info.mDrvId;
        mDevType = info.mDevType;
        mClass = info.mClass;
        mInterface = info.mInterface;
        System.arraycopy(info.mMac, 0, mMac, 0, 8);
    }

    public void SetDrvId(int drvid) {
        mDrvId = drvid;
    }

    public int GetDrvId() {
        return mDrvId;
    }

    public void SetDevType(int devtype) {
        mDevType = devtype;
    }

    public int GetDevType() {
        return mDevType;
    }

    public void SetClass(int cls) {
        mClass = cls;
    }

    public int GetClass() {
        return mClass;
    }

    public void SetInterface(int intf) {
        mInterface = intf;
    }

    public int GetInterface() {
        return mInterface;
    }

    public void SetMac(byte[] mac) {
        System.arraycopy(mac, 0, mMac, 0, 8);
    }

    public void GetMac(byte[] mac) {
        System.arraycopy(mMac, 0, mac, 0, 8);
    }
}