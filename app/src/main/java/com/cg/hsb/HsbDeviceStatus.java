
package com.cg.hsb;

public class HsbDeviceStatus {
	public int[] mId;
	public int[] mVal;
	public int mNum;

	public HsbDeviceStatus(int num) {
		mId = new int[8];
		mVal = new int[8];
		mNum = num;

		for (int id = 0; id < num; id++) {
			mId[id] = id;
			mVal[id] = 0;
		}
	}

	public void Set(int id, int status_id, int val) {
		if (id >= mNum)
			return;

		mId[id] = status_id;
		mVal[id] = val;
	}

	public void Set(int id, int val) {
		if (id >= mNum)
			return;

		mVal[id] = val;
	}

	public int Get(int id) {
		if (id >= mNum)
			return 0;

		return mVal[id];
	}

	public void Updated(HsbDeviceStatus status) {
		for (int id = 0; id < status.mNum; id++) {
			this.Set(status.mId[id], status.mVal[id]);
		}
	}
}