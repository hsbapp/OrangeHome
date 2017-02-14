
package com.cg.hsb;

public class HsbDeviceCondition {

    private int mDevId;
    private int mId;
    private int mVal;
    private int mExp;

    public static final int EXP_EQUAL = 0;
    public static final int EXP_GT = 1;
    public static final int EXP_GE = 2;
    public static final int EXP_LT = 3;
    public static final int EXP_LE = 4;
    public static final int EXP_DEFAULT = EXP_EQUAL;

    public HsbDeviceCondition(int devid) {
        mDevId = devid;
        mId = 0;
        mVal = 0;
        mExp = EXP_DEFAULT;
    }

    public HsbDeviceCondition(int devid, int id, int val) {
        mDevId = devid;
        mId = id;
        mVal = val;
        mExp = EXP_DEFAULT;
    }

    public HsbDeviceCondition(int devid, int id, int val, int exp) {
        mDevId = devid;
        mId = id;
        mVal = val;
        mExp = exp;
    }

    public void Set(HsbDeviceCondition condition) {
        mDevId = condition.GetDevID();
        mId = condition.GetID();
        mVal = condition.GetVal();
        mExp = condition.GetExp();
    }

    public void Set(int id, int val) {
        mId = id;
        mVal = val;
    }

    public void Set(int id, int val, int exp) {
        mId = id;
        mVal = val;
        mExp = exp;
    }

    public void SetExp(int exp) {
        mExp = exp;
    }

    public int GetDevID() {
        return mDevId;
    }

    public int GetID() {
        return mId;
    }

    public int GetVal() {
        return mVal;
    }

    public int GetExp() {
        return mExp;
    }

};

