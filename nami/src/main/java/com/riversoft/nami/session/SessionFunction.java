/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.session;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * @author woden
 *
 */
@ScriptSupport("session")
public class SessionFunction {

	/**
	 * 获取当前用户
	 * 
	 * @return
	 */
	public Object mpUser() {
		return SessionHelper.getMpUser(RequestContext.getCurrent().getHttpRequest());
	}

	/**
	 * 设置session值
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		RequestContext.getCurrent().getHttpRequest().getSession().setAttribute(key, value);
	}

	/**
	 * 获取session值
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return RequestContext.getCurrent().getHttpRequest().getSession().getAttribute(key);
	}

}
