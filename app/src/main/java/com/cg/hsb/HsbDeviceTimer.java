
package com.cg.hsb;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HsbDeviceTimer {
	public int mID;
	public int mYear;
	public int mMon;
	public int mDay;
	public int mHou;
	public int mMin;
	public int mSec;
	public String mType;
	public HsbDeviceAction mAction;

	public static final String TYPE_ONESHOT = "oneshot";
	public static final String TYPE_DAILY = "daily";

	public HsbDeviceTimer() {
		mID = 0;
		mYear = 0;
		mMon = 0;
		mDay = 0;
		mHou = 0;
		mMin = 0;
		mSec = 0;
		mAction  = null;
		mType = TYPE_ONESHOT;
	}

	public void Set(HsbDeviceTimer timer) {
		this.mID = timer.mID;
		this.mYear = timer.mYear;
		this.mMon = timer.mMon;
		this.mDay = timer.mDay;
		this.mHou = timer.mHou;
		this.mMin = timer.mMin;
		this.mSec = timer.mSec;
		this.mAction = timer.mAction;
		this.mType = timer.mType;
	}

	public void SetDate(int year, int mon, int day) {
		mYear = year;
		mMon = mon;
		mDay = day;
	}

	public void SetTime(int hou, int min, int sec)
	{
		mHou = hou;
		mMin = min;
		mSec = sec;
	}

	public void SetAction(HsbDeviceAction action)
	{
		mAction = action;
	}

	public void SetType(String type)
	{
		mType = type;
	}

	public int GetID()
	{
		return mID;
	}

	private String GetDate()
	{
		return String.format("%d-%02d-%02d", mYear, mMon, mDay);
	}

	private String GetTime()
	{
		return String.format("%d:%d:%d", mHou, mMin, mSec);
	}

	public JSONObject GetObject()
	{
		if (null == mAction)
			return null;

		JSONObject obj = new JSONObject();
		if (null == obj)
			return null;

		try {
			obj.put("tmid", mID);
			obj.put("type", mType);
			obj.put("time", GetTime());
			if (mType == TYPE_ONESHOT) {
				obj.put("date", GetDate());
			}

			obj.put("action", mAction.GetObject());
		} catch (JSONException ex) {
			Log.e("hsbservice", "GetObject fail");
			return null;
		}

		return obj;
	}

	public boolean SetObject(JSONObject obj)
	{
		try {
			if (!obj.has("tmid") || !obj.has("type") || !obj.has("time") || !obj.has("action"))
				return false;

			mID = obj.getInt("tmid");
			mType = obj.getString("type");

			SimpleDateFormat sdf1= new SimpleDateFormat("HH:mm:ss");
			Date date1 =sdf1.parse(obj.getString("time"));
			Calendar calendarTime = Calendar.getInstance();
			calendarTime.setTime(date1);
			mHou=calendarTime.get(Calendar.HOUR_OF_DAY);
			mMin=calendarTime.get(Calendar.MINUTE);
			mSec=calendarTime.get(Calendar.SECOND);

			if (obj.has("date")) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				Date date2 = sdf2.parse(obj.getString("date"));
				Calendar calendarDate = Calendar.getInstance();
				calendarDate.setTime(date2);
				mYear = calendarDate.get(Calendar.YEAR);
				mMon = calendarDate.get(Calendar.MONTH);
				mDay = calendarDate.get(Calendar.DAY_OF_MONTH);
			}

			mAction = HsbDeviceAction.CreateFromJson(obj.getJSONObject("action"));
		} catch (JSONException ex) {
			Log.e("hsbservice", "SetObject fail");
			return false;
		}catch (ParseException e){
			Log.e("hsbservice", e.getMessage());
		}

		return true;
	}
}

