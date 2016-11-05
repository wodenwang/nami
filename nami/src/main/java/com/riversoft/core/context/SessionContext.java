/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.context;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * 会话
 * 
 * @author wodenwang
 * 
 */
public class SessionContext extends BaseContext {
	/**
	 * 私有化request
	 */
	private HttpSession session;

	/**
	 * @param request
	 * @param params
	 */
	protected SessionContext(HttpSession session, Map<String, ?> params) {
		super(params);
		this.session = session;
	}

	/**
	 * 新设值
	 * 
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value) {
		if (session != null) {
			session.setAttribute(name, value);
		}
		values.put(name, value);
	}

	/**
	 * 每次http调用周期所使用的request信息
	 */
	private static ThreadLocal<SessionContext> valueLocal = new ThreadLocal<>();

	/**
	 * 获取request实例
	 * 
	 * @return
	 */
	public static SessionContext getCurrent() {
		return valueLocal.get();
	}

	/**
	 * 由系统框架调用，如filter等
	 * 
	 * @param session
	 * @param map
	 */
	public static void init(HttpSession session, Map<String, ?> map) {
		valueLocal.remove();
		valueLocal.set(new SessionContext(session, map));
	}

	/**
	 * 由系统框架调用，如filter等
	 * 
	 * @param map
	 */
	public static void init(Map<String, ?> map) {
		init(null, map);
	}
}
