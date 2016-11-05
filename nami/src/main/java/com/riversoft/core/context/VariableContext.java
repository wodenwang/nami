/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统中间变量
 * 
 * @author Woden
 * 
 */
public class VariableContext extends BaseContext {
	/**
	 * @param params
	 */
	protected VariableContext(Map<String, ?> params) {
		super(params);
	}

	/**
	 * 每次http调用周期所使用的request信息
	 */
	private static ThreadLocal<VariableContext> valueLocal = new ThreadLocal<>();

	/**
	 * 获取request实例
	 * 
	 * @return
	 */
	public static VariableContext getCurrent() {
		return valueLocal.get();
	}

	public void put(String key, Object value) {
		values.put(key, value);
	}

	public Map<String, Object> getAllVariables() {
		return Collections.unmodifiableMap(values);
	}

	public static void init() {
		valueLocal.remove();
		valueLocal.set(new VariableContext(new HashMap<String, Object>()));
	}
}
