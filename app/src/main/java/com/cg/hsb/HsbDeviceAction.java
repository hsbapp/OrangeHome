
package com.cg.hsb;

public class HsbDeviceAction {
	private int mDevID;
	private int mID;
	private int mParam1;
	private int mParam2;
	private int mFlag;
	private int mDelay;

	public static final int FLAG_DEFAULT = 0;
	public static final int FLAG_SET_STATUS = 0;
	public static final int FLAG_DO_ACTION = 1;
	public static final int FLAG_MASK = 0x01;

	public HsbDeviceAction(int devid) {
		mDevID = devid;
		mID = 0;
		mParam1 = 0;
		mParam2 = 0;
		mFlag = FLAG_DEFAULT;
		mDelay = 0;
	}
/*
	public HsbDeviceAction(int devid, int id, int param1, int param2) {
		mDevID = devid;
		mID = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = FLAG_DEFAULT;
	}
*/
	public HsbDeviceAction(int devid, int id, int param1, int param2, int flag) {
		mDevID = devid;
		mID = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = flag;
	}

	public void Set(HsbDeviceAction action) {
		mDevID = action.GetDevID();
		mID = action.GetID();
		mParam1 = action.GetParam1();
		mParam2 = action.GetParam2();
		mFlag = action.GetFlag();
	}

	public void Set(int id, int param1, int param2) {
		mID = id;
		mParam1 = param1;
		mParam2 = param2;
	}

	public void Set(int id, int param1, int param2, int flag) {
		mID = id;
		mParam1 = param1;
		mParam2 = param2;
		mFlag = flag;
	}

	public void SetFlag(int flag) {
		mFlag = flag;
	}

	public void SetDelay(int second) {
		mDelay = second;
	}

	public int GetDevID() { return mDevID; }

	public int GetID() {
		return mID;
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

	public int GetDelay() {
		return mDelay;
	}
}