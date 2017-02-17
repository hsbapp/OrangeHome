package com.gmoonxs.www.orangehome;

import android.os.Environment;

import com.cg.hsb.HsbConstant;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 威 on 2016/6/25.
 */
public class Constant {
    public static final String GET_WEATHER_URL="http://wthrcdn.etouch.cn/WeatherApi?citykey=";

    public static final String[] DEVICE_TYPE={"电视","空调"};

    public static final String[] AC_WORK_MODE={"自动","制冷","除湿","通风","加热"};

    public static final String[] AC_WIND_SPEED={"自动","低速","中速","高速"};

    public static final String[] IR_TYPE_ALL = {HsbConstant.HSB_IR_TYPE_TV, HsbConstant.HSB_IR_TYPE_AC};

    public static final String[] DEVICE_TYPE_ALL={"插座","感应器","遥控器","CC9201","空调"};

    public static final String[] DEVICE_OPERATION={"开","关"};

    public static final String[] DEVICE_CONDITION_EXP={"等于","大于","大于等于","小于","小于等于"};

    public static final int LOCK_SCREEN_TIME=300;
}
