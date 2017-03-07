
package com.cg.hsb;

import android.text.GetChars;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HsbDeviceEndpoint {
	private int mID;
	private int mVal;
	private boolean mReadable;
	private boolean mWritable;
	private String mName;
	private String mValType;
	private ArrayList<ValDesc> mValDesc;
	private int mMin;
	private int mMax;
	private String mUnit;

	public static String VAL_TYPE_INT = "int";
	public static String VAL_TYPE_LIST = "list";

	public HsbDeviceEndpoint(int id, int val)
	{
		mID = id;
		mVal = val;
		mReadable = true;
		mWritable = true;
		mName = "";
		mValType = VAL_TYPE_LIST;
		mValDesc = null;
		mMin = 0;
		mMax = 65536;
		mUnit = "";
	}

	public HsbDeviceEndpoint(int id)
	{
		mID = id;
		mVal = 0;
		mReadable = true;
		mWritable = true;
		mName = "";
		mValType = VAL_TYPE_LIST;
		mValDesc = null;
		mMin = 0;
		mMax = 65536;
		mUnit = "";
	}

	public void SetVal(int val)
	{
		mVal = val;
	}

	public void SetID(int id)
	{
		mID = id;
	}

	public int GetID()
	{
		return mID;
	}

	public int GetVal()
	{
		return mVal;
	}

	public int GetVal(String valDesc)
	{
		if (mValType != VAL_TYPE_LIST)
			return 0;

		for (ValDesc vd : mValDesc)
		{
			if (valDesc == vd.GetDesc())
				return vd.GetVal();
		}

		return 0;
	}

	public int GetNextVal()
	{
		int val = mVal;

		if (mValType == VAL_TYPE_INT)
		{
			if (val == mMax)
				return val;
			else
				return val + 1;
		}
		else if (mValType == VAL_TYPE_LIST)
		{
			boolean found = false;
			int minval = 0;
			for (ValDesc vd : mValDesc)
			{
				int _val = vd.GetVal();
				if (_val <= val)
					continue;

				if (!found) {
					minval = _val;
					found = true;
				} else if (_val < minval) {
					minval = _val;
				}
			}

			if (found)
				return minval;
			else
				return val;
		}

		return val;
	}

	public int GetPrevVal()
	{
		int val = mVal;

		if (mValType == VAL_TYPE_INT)
		{
			if (val == mMin)
				return val;
			else
				return val - 1;
		}
		else if (mValType == VAL_TYPE_LIST)
		{
			boolean found = false;
			int maxval = 0;
			for (ValDesc vd : mValDesc)
			{
				int _val = vd.GetVal();
				if (_val >= val)
					continue;

				if (!found) {
					maxval = _val;
					found = true;
				} else if (_val > maxval) {
					maxval = _val;
				}
			}

			if (found)
				return maxval;
			else
				return val;
		}

		return val;
	}

	public String GetValDesc(int val)
	{
		if (mValType == VAL_TYPE_INT)
		{
			return "" + mVal + mUnit;
		}
		else if (mValType == VAL_TYPE_LIST)
		{
			for (ValDesc vd : mValDesc)
			{
				if (mVal == vd.GetVal())
					return vd.GetDesc();
			}
		}

		return "";
	}

	public String GetValDesc() { return  GetValDesc(mVal); }

	public boolean Readable()
	{
		return mReadable;
	}

	public boolean Writable()
	{
		return mWritable;
	}

	public String GetName()
	{
		return mName;
	}

	public boolean CheckVal(int val)
	{
		if (mValType == VAL_TYPE_INT)
		{
			if (val < mMin || val > mMax)
				return false;
			else
				return true;
		} else if (mValType == VAL_TYPE_LIST)
		{
			for (ValDesc vd : mValDesc)
			{
				if (vd.GetVal() == val)
					return true;
			}

			return false;
		}

		return false;
	}

	public boolean SetObject(JSONObject object)
	{
		boolean updated = false;
		int val;

		try {
			if (object.has("val")) {
				val = object.getInt("val");
				if (val != mVal) {
					updated = true;
					mVal = val;
				}
			}

			if (object.has("readable"))
				mReadable = object.getBoolean("readable");

			if (object.has("writable"))
				mWritable = object.getBoolean("writable");

			if (object.has("attrs"))
			{
				JSONObject attrs = object.getJSONObject("attrs");
				if (attrs.has("name"))
					mName = attrs.getString("name");
			}

			if (object.has("valtype"))
				mValType = object.getString("valtype");

			if (object.has("min"))
				mMin = object.getInt("min");

			if (object.has("max"))
				mMax = object.getInt("max");

			if (object.has("unit"))
				mUnit = object.getString("unit");

			if (object.has("values"))
			{
				if (mValDesc == null) {
					ArrayList<ValDesc> vallist = new ArrayList<ValDesc>();
					JSONArray values = object.getJSONArray("values");
					for (int i = 0; i < values.length(); i++)
					{
						JSONObject value = (JSONObject)values.opt(i);
						if (!value.has("val") || !value.has("desc"))
							continue;

						ValDesc vd = new ValDesc(value.getInt("val"), value.getString("desc"));
						vallist.add(vd);
					}

					mValDesc = vallist;
				}
			}
		} catch (JSONException e) {
			Log.e("hsbservice", "invalid ep object");
			return false;
		}

		return updated;
	}

	public class ValDesc {
		private int mVal;
		private String mDesc;

		public ValDesc(int val, String desc) {
			mVal = val;
			mDesc = new String(desc);
		}

		public int GetVal() { return mVal; }
		public String GetDesc() { return mDesc; }
	}

	public String getmValType() {
		return mValType;
	}
}