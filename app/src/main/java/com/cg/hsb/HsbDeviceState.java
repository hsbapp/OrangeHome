

package com.cg.hsb;


import java.util.ArrayList;

public class HsbDeviceState {

    private int mDevID;
    private int mID;
    private boolean mSync;
    private String NOT_SYNC = "unknown";

    private String mName;
    private String mUnit;
    private int mMin;
    private int mMax;

    public static final int TYPE_INT = 0;
    public static final int TYPE_LIST = 1;
    public static final int TYPE_DEFAULT = TYPE_INT;
    private int mType;

    private ArrayList<StateVal> mValList;
    private HsbDeviceStatus mDevStatus;

    public static final int ACCESS_MODE_READABLE = (1 << 0);
    public static final int ACCESS_MODE_WRITABLE = (1 << 1);
    public static final int ACCESS_MODE_DEFAULT = ACCESS_MODE_READABLE | ACCESS_MODE_WRITABLE;
    private int mAccessMode;

    public HsbDeviceState(int devid, HsbDeviceStatus devStatus, int id, String name, String unit, int type, int accessMode) {
        mDevID = devid;
        mDevStatus = devStatus;
        mID = id;
        mName = new String(name);
        if (unit == null)
            mUnit = new String("");
        else
            mUnit = new String(unit);
        mType = type;
        mAccessMode = accessMode;

        /* Set default value */
        mMin = 0;
        mMax = 65535;
        mValList = new ArrayList<StateVal>();
        mSync = false;
    }

    public void SetRange(int min, int max) {
        mMin = min;
        mMax = max;
    }

    public void AddVal(int val, String desc) {
        mValList.add(new StateVal(val, desc));
    }

    public void Update(int val) {
        if (TYPE_INT == mType) {
            if (val >= mMin && val <= mMax) {
                // TODO
                mSync = true;
                return;
            }
        } else if (TYPE_LIST == mType) {
            int id;
            for (id = 0; id < mValList.size(); id++) {
                StateVal _val = mValList.get(id);
                if (val == _val.mVal) {
                    // TODO
                    mSync = true;
                    return;
                }
            }
        }
    }

    public ArrayList<StateVal> GetValList() {
        return mValList;
    }

    public int GetVal() {
        return mDevStatus.Get(mID);
    }

    public String GetValDesc(int val) {
        String ret = null;

        if (TYPE_INT == mType) {
            ret = new String("" + val);
        } else if (TYPE_LIST == mType) {
            int id;
            for (id = 0; id < mValList.size(); id++) {
                StateVal _val = mValList.get(id);
                if (val == _val.mVal)
                    return _val.mDesc;
            }
        }

        return ret;
    }

    public String GetValDesc() {
        if (!mSync)
            return NOT_SYNC;

        return GetValDesc(GetVal());
    }

    public String GetUnit() {
        return new String(mUnit);
    }

    public int GetType() {
        return mType;
    }

    public int GetDevID() {
        return mDevID;
    }

    public int GetID() {
        return mID;
    }

    public String GetName() {
        return new String(mName);
    }

    public boolean Readable() {
        return ((mAccessMode & ACCESS_MODE_READABLE) > 0) ? true : false;
    }

    public boolean Writable() {
        return ((mAccessMode & ACCESS_MODE_WRITABLE) > 0) ? true : false;
    }

    public boolean Synchronized() {
        return mSync;
    }

    public HsbDeviceAction MakeAction(int val) {
        return new HsbDeviceAction(GetDevID(), mID, val, 0, HsbDeviceAction.FLAG_DEFAULT);
    }

    public HsbDeviceCondition MakeCondition(int val, int exp) {
        return new HsbDeviceCondition(GetDevID(), mID, val, exp);
    }

    public int getmMax() {
        return mMax;
    }

    public int getmMin() {
        return mMin;
    }

    public class StateVal {
        public int mVal;
        public String mDesc;

        public StateVal(int val, String desc) {
            mVal = val;
            mDesc = new String(desc);
        }
    }
};
