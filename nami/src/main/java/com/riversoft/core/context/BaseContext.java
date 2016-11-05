/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.util.JsonMapper;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author wodenwang
 * 
 */
abstract class BaseContext {

	/**
	 * 实际值
	 */
	protected Map<String, Object> values = new HashMap<>();

	protected BaseContext(Map<String, ?> params) {
		values.putAll(params);
	}

	/**
	 * 获取对象
	 * 
	 * @param name
	 * @return
	 */
	public Object get(String name) {
		Object value = values.get(name);
		return value;
	}

	/**
	 * 获取字符串
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		Object obj = get(name);
		String[] values;
		if (obj == null) {
			values = null;
		} else if (obj instanceof String[]) {
			values = (String[]) obj;
		} else {
			values = new String[] { obj.toString() };
		}

		if (values != null && values.length > 0) {
			return values[0];
		} else {
			return null;
		}
	}

	/**
	 * 获取字符串数组
	 * 
	 * @param name
	 * @return
	 */
	public String[] getStrings(String name) {
		Object obj = get(name + "[]");
		if (obj == null) {
			obj = get(name);
		}

		if (obj instanceof String[]) {
			return (String[]) obj;
		} else if (obj != null) {
			return new String[] { obj.toString() };
		} else {
			return null;
		}
	}

	/**
	 * 获取长整形值
	 * 
	 * @param name
	 * @return
	 */
	public Long getLong(String name) {
		return ValueConvertUtils.convert(getString(name), Long.class);
	}

	/**
	 * 获取长整形数组
	 * 
	 * @param name
	 * @return
	 */
	public List<Long> getLongs(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), Long.class);
	}

	/**
	 * 获取浮点值
	 * 
	 * @param name
	 * @return
	 */
	public Float getFloat(String name) {
		return ValueConvertUtils.convert(getString(name), Float.class);
	}

	/**
	 * 获取浮点数组
	 * 
	 * @param name
	 * @return
	 */
	public List<Float> getFloats(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), Float.class);
	}

	/**
	 * 获取双精度浮点值
	 * 
	 * @param name
	 * @return
	 */
	public Double getDouble(String name) {
		return ValueConvertUtils.convert(getString(name), Double.class);
	}

	/**
	 * 获取双精度浮点数组
	 * 
	 * @param name
	 * @return
	 */
	public List<Double> getDoubles(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), Double.class);
	}

	/**
	 * 获取大数据值
	 * 
	 * @param name
	 * @return
	 */
	public BigDecimal getBigDecimal(String name) {
		return ValueConvertUtils.convert(getString(name), BigDecimal.class);
	}

	/**
	 * 获取大树据数组
	 * 
	 * @param name
	 * @return
	 */
	public List<BigDecimal> getBigDecimals(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), BigDecimal.class);
	}

	/**
	 * 获取整形值
	 * 
	 * @param name
	 * @return
	 */
	public Integer getInteger(String name) {
		return ValueConvertUtils.convert(getString(name), Integer.class);
	}

	/**
	 * 获取整形数组
	 * 
	 * @param name
	 * @return
	 */
	public List<Integer> getIntegers(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), Integer.class);
	}

	/**
	 * 获取时间日期类型
	 * 
	 * @param name
	 * @return
	 */
	public Date getDate(String name) {
		return ValueConvertUtils.convert(getString(name), Date.class);
	}

	/**
	 * 获取时间日期数组
	 * 
	 * @param name
	 * @return
	 */
	public List<Date> getDates(String name) {
		return ValueConvertUtils.convertArray(getStrings(name), Date.class);
	}

	/**
	 * 获取JSON值
	 * 
	 * @param name
	 * @return
	 */
	public HashMap<String, Object> getJson(String name) {
		String value = getString(name);
		return JsonMapper.defaultMapper().json2Map(value);
	}

	/**
	 * 获取JSON数组
	 * 
	 * @param name
	 * @return
	 */
	public List<HashMap<String, Object>> getJsons(String name) {
		String[] values = getStrings(name);
		if (values == null || values.length < 1) {
			return null;
		}

		List<HashMap<String, Object>> list = new ArrayList<>();
		for (String value : values) {
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			if (value.startsWith("[")) { // 是数组
				list.addAll(JsonMapper.defaultMapper().fromJson(value, List.class));
			} else {// 单个
				list.add(JsonMapper.defaultMapper().fromJson(value, HashMap.class));
			}
		}

		return list;
	}

	/**
	 * 根据数据类型获取值
	 * 
	 * @param name
	 * @param type
	 */
	public <T> T getBean(String name, Class<T> type) {
		return ValueConvertUtils.convert(getString(name), type);
	}

	/**
	 * 根据数据类型获取值
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public <T> List<T> getBeans(String name, Class<T> type) {
		return ValueConvertUtils.convertArray(getStrings(name), type);
	}

	/**
	 * 当前键
	 * 
	 * @return
	 */
	public Set<String> keySet() {
		return values.keySet();
	}
}
