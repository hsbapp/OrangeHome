
package com.cg.hsb;

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

    public int GetActionNum() {
        return mActionList.size();
    }

    public HsbSceneAction GetAction(int id) {
        if (id < 0 || id >= mActionList.size())
            return null;

        return mActionList.get(id);
    }

    public String GetName() {
        return mName;
    }

    public int GetByteLen() {
        int ret = 20;
        HsbSceneAction action = null;

        int id;
        for (id = 0; id < mActionList.size(); id++) {
            action = mActionList.get(id);
            ret += action.GetByteLen();
        }

        return ret;
    }

    public boolean GetValid() {
        return mValid;
    }

    public boolean Validate(Protocol proto) {
        HsbSceneAction action = null;

        int id;
        for (id = 0; id < mActionList.size(); id++) {
            action = mActionList.get(id);
            if (!action.Validate(proto)) {
                mValid = false;
                return mValid;
            }
        }

        mValid = true;
        return mValid;
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

