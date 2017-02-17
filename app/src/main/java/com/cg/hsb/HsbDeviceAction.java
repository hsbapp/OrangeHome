
package com.cg.hsb;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HsbDeviceAction {
	private int mDevID;
	private int mID;
	private int mValue;

	public HsbDeviceAction() {
		mDevID = 0;
		mID = 0;
		mValue = 0;
	}

	public HsbDeviceAction(int devid) {
		mDevID = devid;
		mID = 0;
		mValue = 0;
	}

	public HsbDeviceAction(int devid, int id, int value) {
		mDevID = devid;
		mID = id;
		mValue = value;
	}

	public void Set(HsbDeviceAction action) {
		mDevID = action.GetDevID();
		mID = action.GetID();
		mValue = action.GetValue();
	}

	public void Set(int id, int value, int delay) {
		mID = id;
		mValue = value;
	}

	public int GetDevID() { return mDevID; }

	public int GetID() {
		return mID;
	}

	public int GetValue() {
		return mValue;
	}

	public JSONObject GetObject()
	{
		JSONObject obj = new JSONObject();
		if (null == obj)
			return null;

		try {
			obj.put("devid", mDevID);
			obj.put("epid", mID);
			obj.put("val", mValue);
		} catch (JSONException ex) {
			Log.e("hsbservice", "GetObject fail");
			return null;
		}

		return obj;
	}

	public boolean SetObject(JSONObject obj)
	{
		try {
			if (!obj.has("devid") || !obj.has("epid") || !obj.has("val"))
				return false;

			mDevID = obj.getInt("devid");
			mID = obj.getInt("epid");
			mValue = obj.getInt("val");
		} catch (JSONException ex) {
			Log.e("hsbservice", "SetObject fail");
			return false;
		}

		return true;
	}
}