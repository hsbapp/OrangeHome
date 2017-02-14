
package com.cg.hsb;

public class HsbDeviceTimer {
	public int mWorkMode;
	public int mYear; /* Year - 1900 */
	public byte mMon; /* 0 - 11 */
	public byte mDay; /* 1 - 31 */
	public byte mHou; /* 0 - 23 */
	public byte mMin; /* 0 - 59 */
	public byte mSec; /* 0 - 59 */
	public int mWeekday; /* 0 - 6, 0: Sunday */
	public HsbDeviceAction mAction;
	public boolean mActive;
	public int mDevId;

	public static final int WEEKDAY_ONE_SHOT = (1 << 7);
	public static final int WEEKDAY_EVERY_DAY = 0x7F;
	public static final int WEEKDAY_SUNDAY = (1 << 0);
	public static final int WEEKDAY_MONDAY = (1 << 1);
	public static final int WEEKDAY_TUESDAY = (1 << 2);
	public static final int WEEKDAY_WEDNESDAY = (1 << 3);
	public static final int WEEKDAY_THURSDAY = (1 << 4);
	public static final int WEEKDAY_FRIDAY = (1 << 5);
	public static final int WEEKDAY_SATURDAY = (1 << 6);
	public static final int WEEKDAY_DEFAULT = WEEKDAY_EVERY_DAY;

	public HsbDeviceTimer(int devid) {
		mDevId = devid;
		mWorkMode = HsbConstant.HSB_WORK_MODE_DEFAULT;
		mYear = 0;
		mMon = 0;
		mDay = 0;
		mHou = 0;
		mMin = 0;
		mSec = 0;
		mWeekday = WEEKDAY_DEFAULT;
		mAction  = new HsbDeviceAction(devid);
		mActive = true;
	}

	public void SetDate(int year, int mon, int day) {
		if (year < 116 || mon < 0 || mon > 11 || day < 1 || day > 31)
			return;

		mYear = year;
		mMon = (byte)mon;
		mDay = (byte)day;
	}

	public void SetTime(int hou, int min, int sec)
	{
		if (hou < 0 || hou > 23 || min < 0 || min > 59 || sec < 0 || sec > 59)
			return;

		mHou = (byte)hou;
		mMin = (byte)min;
		mSec = (byte)sec;
	}

	public void SetWorkMode(int WorkMode)
	{
		mWorkMode = WorkMode;
	}

	public void SetWeekDay(int WeekDay)
	{
		mWeekday = WeekDay & 0xFF;
	}

	public void SetAction(HsbDeviceAction action)
	{
		mAction.Set(action);
	}

	public void SetActive(boolean active) {
		mActive = active;
	}
}

