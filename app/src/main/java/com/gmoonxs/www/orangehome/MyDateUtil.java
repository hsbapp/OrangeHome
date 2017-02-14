package com.gmoonxs.www.orangehome;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期常量类
 *
 * @author zihao
 *
 */
@SuppressLint("SimpleDateFormat")
public class MyDateUtil {

	/**
	 * 转换日期格式为yyyy-MM-dd
	 */
	public static String getChangeDateFormat(Date date) {
		String str = null;
		if (date != null && !"".equals(date)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			str = sd.format(date);
		}
		return str;
	}

	/**
	 * 转换日期格式为HH:mm
	 */
	public static String getChangeTimeFormat(Date date) {
		String str = null;
		if (date != null && !"".equals(date)) {
			SimpleDateFormat sd = new SimpleDateFormat("HH:mm");
			str = sd.format(date);
		}
		return str;
	}

	/**
	 * 获取当前为星期几
	 *
	 * @param date
	 * @return
	 */
	public static String getChangeWeekFormat(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.DAY_OF_WEEK);
		String str = "" + hour;
		if ("1".equals(str)) {
			str = "星期日";
		} else if ("2".equals(str)) {
			str = "星期一";
		} else if ("3".equals(str)) {
			str = "星期二";
		} else if ("4".equals(str)) {
			str = "星期三";
		} else if ("5".equals(str)) {
			str = "星期四";
		} else if ("6".equals(str)) {
			str = "星期五";
		} else if ("7".equals(str)) {
			str = "星期六";
		}
		return str;
	}
}