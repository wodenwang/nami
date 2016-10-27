/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script.function;

import java.math.BigDecimal;
import java.util.Date;

import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.util.Formatter;
import com.riversoft.util.PinyinUtils;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author woden
 * 
 */
@ScriptSupport("fmt")
public class FormatUtil {

	/**
	 * 格式化日期成字符串（yyyy-MM-dd）
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return Formatter.formatDate(date);
	}

	/**
	 * 格式化日期成字符串（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDatetime(Date date) {
		return Formatter.formatDatetime(date);
	}

	/**
	 * 格式化日期成自定义格式字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDatetime(Date date, String pattern) {
		return Formatter.formatDatetime(date, pattern);
	}

	/**
	 * 数字格式化成大写
	 * 
	 * @param price
	 * @return
	 */
	public static String formatChinesePrice(Number price) {
		if (price == null) {
			return "";
		}

		if (price instanceof BigDecimal) {
			return Formatter.formatChinesePrice((BigDecimal) price);
		} else {
			return Formatter.formatChinesePrice(new BigDecimal(price.doubleValue()));
		}
	}

	/**
	 * 格式化价格
	 * 
	 * @param price
	 * @param pattern
	 * @return
	 */
	public static String formatPrice(Number price, String pattern) {
		if (price == null) {
			return "";
		}

		if (price instanceof BigDecimal) {
			return Formatter.formatPrice((BigDecimal) price, pattern);
		} else {
			return Formatter.formatPrice(new BigDecimal(price.doubleValue()), pattern);
		}
	}

	/**
	 * 格式化价格
	 * 
	 * @param price
	 * @return
	 */
	public static String formatPrice(Number price) {
		if (price == null) {
			return "";
		}

		if (price instanceof BigDecimal) {
			return Formatter.formatPrice((BigDecimal) price);
		} else {
			return Formatter.formatPrice(new BigDecimal(price.doubleValue()));
		}
	}

	/**
	 * 格式化百分比
	 * 
	 * @param num
	 * @return
	 */
	public static String formatPercent(Number num) {
		if (num == null) {
			return "";
		}
		return Formatter.formatPercent(num.doubleValue());
	}

	/**
	 * 格式化数字
	 * 
	 * @param num
	 * @param pattern
	 * @return
	 */
	public static String formatNumber(Number num, String pattern) {
		return Formatter.formatNumber(num, pattern);
	}

	/**
	 * 格式化数字
	 * 
	 * @param num
	 * @return
	 */
	public static String formatNumber(Number num) {
		return Formatter.formatNumber(num);
	}

	/**
	 * 中文格式化成拼音(首字母)
	 * 
	 * @param chinese
	 * @return
	 */
	public static String formatPinyin(String chinese) {
		return PinyinUtils.converterToFirstSpell(chinese);
	}

	/**
	 * 中文转换成拼音(全拼)
	 * 
	 * @param chinese
	 * @return
	 */
	public static String formatPinyinFull(String chinese) {
		return PinyinUtils.converterToSpell(chinese);
	}

	/**
	 * 对象转换成JSON
	 * 
	 * @param o
	 * @return
	 */
	public static String formatJson(Object o) {
		if (o == null) {
			return "{}";
		}
		// TODO:JSON
		try {
			return JsonMapper.defaultMapper().toJson(o);
		} catch (Exception e) {
			return "{}";
		}
	}

	/**
	 * 将秒数格式化成天-小时-分-秒<br>
	 * 返回两段式即可,即:X天Y小时或X小时Y分钟
	 * 
	 * @param s
	 * @return
	 */
	public static String formatDuring(Long s) {
		return Formatter.formatDuring(s);
	}

	/**
	 * 将字符串转换成JSON
	 * 
	 * @param value
	 * @return
	 */
	public static Object toJson(Object value) {
		// TODO:JSON
		if (value instanceof String) {
			return JsonMapper.defaultMapper().json2Map((String) value);
		} else {
			return value;
		}
	}

	/**
	 * 转换成日期
	 * 
	 * @param value
	 * @return
	 */
	public static Date toDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) {
			return (Date) value;
		} else if (value instanceof String) {
			return ValueConvertUtils.convert((String) value, Date.class);
		} else {
			return ValueConvertUtils.convert(value.toString(), Date.class);
		}
	}

	/**
	 * 转换成数字
	 * 
	 * @param value
	 * @return
	 */
	public static Number toNumber(Object value) {
		if (value == null || StringUtils.isEmpty(value.toString())) {
			return null;
		}

		if (value instanceof Number) {
			return (Number) value;
		} else if (value instanceof String) {
			return ValueConvertUtils.convert((String) value, Number.class);
		} else {
			return ValueConvertUtils.convert(value.toString(), Number.class);
		}
	}

}
