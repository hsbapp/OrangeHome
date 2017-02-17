

package com.cg.hsb;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject GetObject()
    {
        JSONObject obj = new JSONObject();
        if (null == obj)
            return null;

        JSONArray actions = new JSONArray();
        try {
            obj.put("delay", mDelay);

            if (null != mCondition)
                obj.put("condition", mCondition.GetObject());

            for (HsbDeviceAction action : mActionList)
            {
                JSONObject actobj = action.GetObject();
                actions.put(actobj);
            }

            obj.put("actions", actions);
        } catch (JSONException ex) {
            Log.e("hsbservice", "GetObject fail");
            return null;
        }

        return obj;
    }

    public boolean SetObject(JSONObject obj)
    {
        JSONArray actions = null;
        ArrayList<HsbDeviceAction> actlist = new ArrayList<HsbDeviceAction>();
        HsbDeviceCondition condition = null;

        try {
            if (!obj.has("delay"))
                return false;

            mDelay = obj.getInt("delay");

            if (obj.has("condition")) {
                condition = new HsbDeviceCondition();
                JSONObject cond = (JSONObject)obj.get("condition");
                if (!condition.SetObject(cond))
                    return false;
            }

            actions = obj.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++)
            {
                JSONObject actobj = (JSONObject)actions.opt(i);
                HsbDeviceAction act = new HsbDeviceAction();
                if (act.SetObject(actobj))
                    actlist.add(act);
            }
        } catch (JSONException ex) {
            Log.e("hsbservice", "SetObject fail");
            return false;
        }

        mActionList.clear();
        mActionList = actlist;
        mCondition = condition;

        return true;
    }
}

