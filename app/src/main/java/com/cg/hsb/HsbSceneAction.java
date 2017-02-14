

package com.cg.hsb;

import android.util.Log;

import java.util.ArrayList;

public class HsbSceneAction {

    private HsbDeviceCondition mCondition;
    private ArrayList<HsbDeviceAction> mActionList;
    private int mDelay;

    public HsbSceneAction() {
        mCondition = null;
        mActionList = new ArrayList<HsbDeviceAction>();
        mDelay = 0;
    }

    public void SetCondition(HsbDeviceCondition condition) {
        mCondition = condition;
    }

    public void AddAction(HsbDeviceAction action) {
        mActionList.add(action);
    }

    public HsbDeviceCondition GetCondition() {
        return mCondition;
    }

    public ArrayList<HsbDeviceAction> GetActionList() {
        return mActionList;
    }

    public HsbDeviceAction GetAction(int id) {
        return mActionList.get(id);
    }
    public int GetActionNum() {
        return mActionList.size();
    }

    public int GetDelay() {
        return mDelay;
    }

    public void SetDelay(int delay) {
        mDelay = delay;
    }

    public boolean Validate(Protocol proto) {
        if (mCondition != null) {
            if (!proto.ValidateDevice(mCondition.GetDevID()))
                return false;
        }

        int id;
        HsbDeviceAction action = null;
        for (id = 0; id < mActionList.size(); id++) {
            action = mActionList.get(id);
            if (!proto.ValidateDevice(action.GetDevID()))
                return false;
        }

        return true;
    }

    public int GetByteLen() {
        int ret = 4;

        if (null != mCondition)
            ret += 8;

        ret += mActionList.size() * 12;

        return ret;
    }

    public int FillBuffer(byte[] buf, int offset) {
        int ptr = offset;

        buf[ptr] = (byte)0xFF;
        ptr += 1;
        buf[ptr] = (byte)mDelay;
        ptr += 1;
        buf[ptr] = (byte)(mCondition != null ? 1 : 0);
        ptr += 1;
        buf[ptr] = (byte)mActionList.size();
        ptr += 1;

        if (mCondition != null) {
            buf[ptr] = (byte)0xFE;
            ptr++;

            buf[ptr] = (byte)mCondition.GetExp();
            ptr++;

            Protocol.Decode2LE(buf, ptr, mCondition.GetDevID());
            ptr += 2;
            Protocol.Decode2LE(buf, ptr, mCondition.GetID());
            ptr += 2;
            Protocol.Decode2LE(buf, ptr, mCondition.GetVal());
            ptr += 2;
        }

        int id;
        HsbDeviceAction action = null;
        for (id = 0; id < mActionList.size(); id++) {
            action = mActionList.get(id);

            buf[ptr] = (byte)0xFD;
            ptr += 1;

            buf[ptr] = (byte)action.GetFlag();
            ptr += 1;

            Protocol.Decode2LE(buf, ptr, action.GetDevID());
            ptr += 2;
            Protocol.Decode2LE(buf, ptr, action.GetID());
            ptr += 2;
            Protocol.Decode2LE(buf, ptr, action.GetParam1());
            ptr += 2;
            Protocol.Decode4LE(buf, ptr, action.GetParam2());
            ptr += 4;
        }

        return GetByteLen();
    }

    public int ParseBuffer(byte[] buf, int offset) {
        int ptr = offset;
        HsbDeviceCondition condition = null;
        boolean bCondition = false;
        int actNum = 0;

        if (buf[ptr] != (byte)0xFF)
            Log.e("hsbservice", "HsbSceneAction head error: " + (int)buf[ptr]);

        SetDelay((int)buf[ptr + 1]);
        if (buf[ptr + 2] > 0)
            bCondition = true;
        actNum = (int)buf[ptr + 3];
        if (actNum > 8)
            Log.e("hsbservice", "HsbSceneAction actNum error: " + actNum);

        ptr += 4;

        if (bCondition) {
            condition = new HsbDeviceCondition(Protocol.Make2LE(buf, ptr + 2),
                                                Protocol.Make2LE(buf, ptr + 4),
                                                Protocol.Make2LE(buf, ptr + 6),
                                                (int)buf[ptr + 1]);
            ptr += 8;
            SetCondition(condition);
        }

        HsbDeviceAction action = null;
        byte header;

        int id;
        for (id = 0; id < actNum; id++) {
            header = buf[ptr];
            if (header != (byte)0xFD) {
                Log.e("hsbservice", "HsbSceneAction actNum error: " + actNum);
                continue;
            }

            action = new HsbDeviceAction(Protocol.Make2LE(buf, ptr + 2),
                    Protocol.Make2LE(buf, ptr + 4),
                    Protocol.Make2LE(buf, ptr + 6),
                    Protocol.Make4LE(buf, ptr + 8),
                    (int) buf[ptr + 1]);
            ptr += 12;
            AddAction(action);
        }

        return (ptr - offset);
    }

    public void setmActionList(ArrayList<HsbDeviceAction> mActionList) {
        this.mActionList = mActionList;
    }
}

