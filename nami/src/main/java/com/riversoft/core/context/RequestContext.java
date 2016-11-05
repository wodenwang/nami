/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.context;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前请求
 * 
 * @author wodenwang
 * 
 */
public class RequestContext extends BaseContext {

	/**
	 * 私有化request
	 */
	private HttpServletRequest request;

	/**
	 * @param request
	 * @param params
	 */
	protected RequestContext(HttpServletRequest request, Map<String, ?> params) {
		super(params);
		this.request = request;
	}

	/**
	 * 新设值
	 * 
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value) {
		if (request != null) {
			request.setAttribute(name, value);
		}
		values.put(name, value);
	}

	/**
	 * 每次http调用周期所使用的request信息
	 */
	private static ThreadLocal<RequestContext> valueLocal = new ThreadLocal<>();

	/**
	 * 获取request实例
	 * 
	 * @return
	 */
	public static RequestContext getCurrent() {
		return valueLocal.get();
	}

	/**
	 * 由系统框架调用，如filter等
	 * 
	 * @param request
	 * @param params
	 */
	public static void init(HttpServletRequest request, Map<String, ?> params) {
		valueLocal.remove();
		valueLocal.set(new RequestContext(request, params));
	}

	/**
	 * 由系统框架调用，如filter等
	 * 
	 * @param params
	 */
	public static void init(Map<String, ?> params) {
		init(null, params);
	}
}
