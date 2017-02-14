
package com.cg.hsb;

public class HsbDeviceEvent {

	private int mDevId;
	private int mId;
	private int mParam1;
	private int mParam2;
	private int mFlag;

	public static final int FLAG_STATUS_EQUAL = 0;
	public static final int FLAG_STATUS_GT = 1;
	public static final int FLAG_STATUS_GE = 2;
	public static final int FLAG_STATUS_LT = 3;
	public static final int FLAG_STATUS_LE = 4;
	public static final int FLAG_STATUS_DEFAULT = FLAG_STATUS_EQUAL;

	public HsbDeviceEvent(int devid) {
		mDevId = devid;
		mId = 0;
		mParam1 = 0;
		mParam2 = 0;
		mFlag = FLAG_STATUS_DEFAULT;
	}

	public HsbDeviceEvent(int devid, int id, int param1, int param2) {
		mDevId = devid;
		mId = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = FLAG_STATUS_DEFAULT;
	}

	public HsbDeviceEvent(int devid, int id, int param1, int param2, int flag) {
		mDevId = devid;
		mId = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = flag;
	}

	public void Set(HsbDeviceEvent event) {
		mDevId = event.mDevId;
		mId = event.mId;
		mParam1 = event.mParam1;
		mParam2 = event.mParam2;
		mFlag = event.mFlag;
	}

	public void Set(int id, int param1, int param2) {
		mId = id;
		mParam1 = param1;
		mParam2 = param2;
	}

	public void Set(int id, int param1, int param2, int flag) {
		mId = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = flag;
	}

	public void SetFlag(int flag) {
		mFlag = flag;
	}

	public int GetDevId() {
		return mDevId;
	}

	public int GetID() {
		return mId;
	}

	public int GetParam1() {
		return mParam1;
	}

	public int GetParam2() {
		return mParam2;
	}

	public int GetFlag() {
		return mFlag;
	}
}