/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 数据转换工具类
 * 
 * @author Woden
 * 
 */
public class ValueConvertUtils {

	/**
	 * 对象转换
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> type) {

		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (ClassUtils.isAssignable(type, String.class)) {
			return (T) value;
		} else if (ClassUtils.isAssignable(type, Number.class)) {// 数字
			return (T) convertNumber(value, type);
		} else if (Date.class.isAssignableFrom(type)) {
			return (T) convertDate(value);
		} else if (Boolean.class.isAssignableFrom(type) || type == boolean.class) {
			return (T) convertBoolean(value);
		} else {
			return convertBean(value, type);
		}
	}

	/**
	 * 数字转换
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	private static Number convertNumber(String value, Class<?> type) {

		if (ClassUtils.isAssignable(type, BigDecimal.class)) {
			return NumberUtils.createBigDecimal(value);
		}
		if (ClassUtils.isAssignable(type, Double.class)) {
			return Double.valueOf(value);
		}
		if (ClassUtils.isAssignable(type, Float.class)) {
			return Float.valueOf(value);
		}

		if (ClassUtils.isAssignable(type, Long.class)) {
			return Long.valueOf(value);
		}
		if (ClassUtils.isAssignable(type, Integer.class)) {
			return Integer.valueOf(value);
		}
		if (ClassUtils.isAssignable(type, Short.class)) {
			return Short.valueOf(value);
		}

		try {
			return Long.valueOf(value);
		} catch (Exception e) {
			return NumberUtils.createBigDecimal(value);
		}
	}

	/**
	 * 数组转换
	 * 
	 * @param values
	 * @param type
	 * @return
	 */
	public static <T> List<T> convertArray(String[] values, Class<T> type) {
		List<T> list = new ArrayList<>();
		if (Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)) {// 基础类型
			for (String source : values) {
				list.add(convert(source, type));
			}
		} else {
			list.addAll(convertBeanArray(values, type));
		}
		return list;
	}

	/**
	 * 使用json解析数组
	 * 
	 * @param values
	 * @param type
	 * @return
	 */
	private static <T> List<T> convertBeanArray(String[] values, Class<T> type) {
		List<T> list = new ArrayList<>();
		if (values != null) {
			for (String source : values) {
				list.add(JsonMapper.defaultMapper().fromJson(source, type));
			}
		}
		return list;
	}

	/**
	 * 布尔类型转换
	 * 
	 * @param value
	 * @return
	 */
	private static Boolean convertBoolean(String value) {
		if ("true".equalsIgnoreCase(value)) {
			return true;
		}

		if ("1".equals(value)) {
			return true;
		}

		return false;
	}

	/**
	 * 转换时间类型
	 * 
	 * @param value
	 * @return
	 */
	private static Date convertDate(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		try {
			if (value.indexOf(" ") > 0) {// 含有空格
				return dateTimeFormat.parse(value);
			} else if (value.indexOf("-") > 0) {// 含有-
				return dateFormat.parse(value);
			} else if (value.indexOf(":") > 0) {
				return timeFormat.parse(value);
			} else {
				throw new RuntimeException("将[" + value + "]转换为日期时间类型出错，未知格式");
			}
		} catch (ParseException e) {
			throw new RuntimeException("将[" + value + "]转换为日期时间类型出错.", e);
		}
	}

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 日期
	private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日期时间
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");// 时间

	/**
	 * 使用json解析
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	private static <T> T convertBean(String value, Class<T> type) {
		return JsonMapper.defaultMapper().fromJson(value, type);
	}
}
