
package com.cg.hsb;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HsbDeviceCondition {

    private int mDevID;
    private int mID;
    private int mVal;
    private String mExp;

    public static final String EXP_EQUAL = "eq";
    public static final String EXP_GT = "gt";
    public static final String EXP_GE = "ge";
    public static final String EXP_LT = "lt";
    public static final String EXP_LE = "le";
    public static final String EXP_DEFAULT = EXP_EQUAL;

    public HsbDeviceCondition() {
        mDevID = 0;
        mID = 0;
        mVal = 0;
        mExp = EXP_DEFAULT;
    }

    public HsbDeviceCondition(int devid) {
        mDevID = devid;
        mID = 0;
        mVal = 0;
        mExp = EXP_DEFAULT;
    }

    public HsbDeviceCondition(int devid, int id, int val) {
        mDevID = devid;
        mID = id;
        mVal = val;
        mExp = EXP_DEFAULT;
    }

    public HsbDeviceCondition(int devid, int id, int val, String exp) {
        mDevID = devid;
        mID = id;
        mVal = val;
        mExp = exp;
    }

    public void Set(HsbDeviceCondition condition) {
        mDevID = condition.GetDevID();
        mID = condition.GetID();
        mVal = condition.GetVal();
        mExp = condition.GetExp();
    }

    public void Set(int id, int val) {
        mID = id;
        mVal = val;
    }

    public void Set(int id, int val, String exp) {
        mID = id;
        mVal = val;
        mExp = exp;
    }

    public void SetExp(String exp) {
        mExp = exp;
    }

    public int GetDevID() {
        return mDevID;
    }

    public int GetID() {
        return mID;
    }

    public int GetVal() {
        return mVal;
    }

    public String GetExp() {
        return mExp;
    }

    public JSONObject GetObject()
    {
        JSONObject obj = new JSONObject();
        if (null == obj)
            return null;

        try {
            obj.put("devid", mDevID);
            obj.put("epid", mID);
            obj.put("val", mVal);
            obj.put("expr", mVal);
        } catch (JSONException ex) {
            Log.e("hsbservice", "GetObject fail");
            return null;
        }

        return obj;
    }

    public boolean SetObject(JSONObject obj)
    {
        try {
            if (!obj.has("devid") || !obj.has("epid") || !obj.has("val") || !obj.has("expr"))
                return false;

            mDevID = obj.getInt("devid");
            mID = obj.getInt("epid");
            mVal = obj.getInt("val");
            mExp = obj.getString("expr");
        } catch (JSONException ex) {
            Log.e("hsbservice", "SetObject fail");
            return false;
        }

        return true;
    }
};

