
package com.cg.hsb;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HsbScene {

    private String mName;
    private ArrayList<HsbSceneAction> mActionList;
    private boolean mValid;

    public HsbScene(String name) {
        mName = new String(name);
        mActionList = new ArrayList<HsbSceneAction>();
        mValid = true;
    }

    public void AddAction(HsbSceneAction action) {
        mActionList.add(action);
    }

    public ArrayList<HsbSceneAction> GetActionList() {
        return mActionList;
    }

    public String GetName() {
        return mName;
    }

    public boolean GetValid() {
        return mValid;
    }

    public boolean Validate(Protocol proto)
    {
        for (HsbSceneAction action : mActionList)
        {
            if (!action.Validate(proto)) {
                mValid = false;
                return mValid;
            }
        }

        mValid = true;
        return mValid;
    }

    public JSONObject GetObject()
    {
        JSONObject obj = new JSONObject();
        if (null == obj)
            return null;

        JSONArray actions = new JSONArray();
        try {
            obj.put("name", mName);

            for (HsbSceneAction action : mActionList)
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
        ArrayList<HsbSceneAction> actlist = new ArrayList<HsbSceneAction>();

        try {
            actions = obj.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++)
            {
                JSONObject actobj = (JSONObject)actions.opt(i);
                HsbSceneAction act = new HsbSceneAction();
                if (act.SetObject(actobj))
                    actlist.add(act);
            }
        } catch (JSONException ex) {
            Log.e("hsbservice", "SetObject fail");
            return false;
        }

        mActionList.clear();
        mActionList = actlist;

        return true;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof HsbScene)
        {
            sameSame = (this.mName.equals(((HsbScene)object).GetName()));
        }

        return sameSame;
    }
}

