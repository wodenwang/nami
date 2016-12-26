/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.function;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * @author woden
 * 
 */
@ScriptSupport("date")
public class DateFunction {

	/**
	 * 计算两个时间之间的差值
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Long compare(Date date1, Date date2) {
		return compare(date1, date2, "D");
	}

	/**
	 * 计算两个时间之间的差值
	 * 
	 * @param date1
	 * @param date2
	 * @param pattern
	 * @return
	 */
	public static Long compare(Date date1, Date date2, String pattern) {
		Long gap = date1.getTime() - date2.getTime();
		switch (pattern) {
		case "Y":
		case "y":
			return diffYear(date1, date2);
		case "M":
			return diffMonth(date1, date2);
		case "D":
			return gap / 1000 / 60 / 60 / 24;
		case "d":
			return gap / 1000 / 60 / 60 / 24;
		case "H":
			return gap / 1000 / 60 / 60;
		case "h":
			return gap / 1000 / 60 / 60;
		case "m":
			return gap / 1000 / 60;
		case "s":// 秒
			return gap / 1000;
		case "S":// 毫秒
			return gap;
		default:// 默认是天
			return gap / 1000 / 60 / 60 / 24;
		}
	}

	private static Long diffYear(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		int year1 = calendar1.get(Calendar.YEAR);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		int year2 = calendar2.get(Calendar.YEAR);

		int diff = year1 - year2;
		return Long.valueOf(diff);
	}

	private static Long diffMonth(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		int year2 = calendar2.get(Calendar.YEAR);
		int month2 = calendar2.get(Calendar.MONTH);

		int diff = (year1 - year2) * 12 + (month1 - month2);
		return Long.valueOf(diff);
	}

	/**
	 * 日期加减计算(单位:天)
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date cal(Date date, Integer offset) {
		return cal(date, offset, null);
	}

	/**
	 * 日期加减计算
	 * 
	 * @param date
	 *            待计算时间
	 * @param offset
	 *            正数为加,复数为减
	 * @param pattern
	 *            单位,默认为天
	 * @return
	 */
	public static Date cal(Date date, Integer offset, String pattern) {
		if (pattern == null) {
			// 日
			return DateUtils.addDays(date, offset);
		} else {
			switch (pattern) {
			case "Y":
			case "y":
				return DateUtils.addYears(date, offset);
			case "M":
				return DateUtils.addMonths(date, offset);
			case "D":
			case "d":
				return DateUtils.addDays(date, offset);
			case "H":
			case "h":
				return DateUtils.addHours(date, offset);
			case "m":
				return DateUtils.addMinutes(date, offset);
			case "s":
				return DateUtils.addSeconds(date, offset);
			case "S":
				return DateUtils.addMilliseconds(date, offset);
			default:
				// 日
				return DateUtils.addDays(date, offset);
			}
		}
	}

}
