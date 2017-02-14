
package com.cg.hsb;


public class HsbConst {
    public static final int HSB_WORK_MODE_DEFAULT = 0xFF;
    public static final int HSB_WORK_MODE_HOME = 0;
    public static final int HSB_WORK_MODE_OUTSIDE = 1;
    public static final int HSB_WORK_MODE_GUARD = 2;
    public static final int HSB_WORK_MODE_ALL = 0xFF;

    public static final int HSB_ACT_FLAG_DEFAULT = 0;
    public static final int HSB_ACT_FLAG_SET_STATUS = 0;
    public static final int HSB_ACT_FLAG_DO_ACTION = 1;
    public static final int HSB_ACT_FLAG_MASK = 0x01;
    public static final int HSB_EVT_FLAG_STATUS_EQUAL = 0;
    public static final int HSB_EVT_FLAG_STATUS_GT = (1 << 1);
    public static final int HSB_EVT_FLAG_STATUS_GE = (2 << 1);
    public static final int HSB_EVT_FLAG_STATUS_LT = (3 << 1);
    public static final int HSB_EVT_FLAG_STATUS_LE = (4 << 1);
    public static final int HSB_EVT_FLAG_MASK = 0x0E;

    public static final int HSB_TIMER_WEEKDAY_DEFAULT = 0x7F;
    public static final int HSB_TIMER_WEEKDAY_ONE_SHOT = 0x80;
    public static final int HSB_TIMER_WEEKDAY_EVERY_DAY = 0x7F;
    public static final int HSB_TIMER_WEEKDAY_SUNDAY = (1 << 0);
    public static final int HSB_TIMER_WEEKDAY_MONDAY = (1 << 1);
    public static final int HSB_TIMER_WEEKDAY_TUESDAY = (1 << 2);
    public static final int HSB_TIMER_WEEKDAY_WEDNESDAY = (1 << 3);
    public static final int HSB_TIMER_WEEKDAY_THURSDAY = (1 << 4);
    public static final int HSB_TIMER_WEEKDAY_FRIDAY = (1 << 5);
    public static final int HSB_TIMER_WEEKDAY_SATURDAY = (1 << 6);

    public static final int HSB_DEV_TYPE_PLUG = 0;
    public static final int HSB_DEV_TYPE_SENSOR = 1;
    public static final int HSB_DEV_TYPE_REMOTE_CTL = 2;
    public static final int HSB_DEV_TYPE_STB_CC9201 = 3;
    public static final int HSB_DEV_TYPE_GRAY_AC = 4;

    public static final int HSB_TV_STATUS_CHANNEL = 0;

    public static final int HSB_TV_ACTION_PRESS_KEY = 0;

    public static final int HSB_TV_KEY_ON_OFF = 0;
    public static final int HSB_TV_KEY_ADD_VOL = 1;
    public static final int HSB_TV_KEY_DEC_VOL = 2;
    public static final int HSB_TV_KEY_OK = 3;
    public static final int HSB_TV_KEY_BACK = 4;
    public static final int HSB_TV_KEY_LEFT = 5;
    public static final int HSB_TV_KEY_RIGHT = 6;
    public static final int HSB_TV_KEY_UP = 7;
    public static final int HSB_TV_KEY_DOWN = 8;
    public static final int HSB_TV_KEY_MUTE = 9;
    public static final int HSB_TV_KEY_0 = 10;
    public static final int HSB_TV_KEY_1 = 11;
    public static final int HSB_TV_KEY_2 = 12;
    public static final int HSB_TV_KEY_3 = 13;
    public static final int HSB_TV_KEY_4 = 14;
    public static final int HSB_TV_KEY_5 = 15;
    public static final int HSB_TV_KEY_6 = 16;
    public static final int HSB_TV_KEY_7 = 17;
    public static final int HSB_TV_KEY_8 = 18;
    public static final int HSB_TV_KEY_9 = 19;

    public static final int HSB_E_OK = 0;
    public static final int HSB_E_INVALID_MSG = 1;
    public static final int HSB_E_NOT_SUPPORTED = 2;
    public static final int HSB_E_BAD_PARAM = 3;
    public static final int HSB_E_NO_MEMORY = 4;
    public static final int HSB_E_ENTRY_EXISTS = 5;
    public static final int HSB_E_ENTRY_NOT_FOUND = 6;
    public static final int HSB_E_ACT_FAILED = 7;
    public static final int HSB_E_OTHERS = 8;

    public static final int HSB_EVT_STATUS_UPDATED = 0;
    public static final int HSB_EVT_DEV_UPDATED = 1;
    public static final int HSB_EVT_SENSOR_TRIGGERED = 2;
    public static final int HSB_EVT_SENSOR_RECOVERED = 3;
    public static final int HSB_EVT_MODE_CHANGED = 4;
    public static final int HSB_EVT_IR_KEY = 5;

    public HsbConst() {}
}