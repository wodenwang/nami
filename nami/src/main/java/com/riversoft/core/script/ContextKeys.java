/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

/**
 * 平台脚本引擎上下文可用属性
 * 
 * @author Woden
 * 
 */
enum ContextKeys {

	/**
	 * 当前请求参数
	 */
	REQUEST("request"),
	/**
	 * 当前会话
	 */
	SESSION("session"),
	/**
	 * 全局上下文
	 */
	VARIABLE("variable"),
	/**
	 * 当前时间
	 */
	NOW("now");

	String key;

	private ContextKeys(String key) {
		this.key = key;
	}
}
