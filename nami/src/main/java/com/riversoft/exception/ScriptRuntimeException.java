/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.exception;

/**
 * 脚本异常
 * 
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class ScriptRuntimeException extends SystemRuntimeException {

	private String script;// 脚本
	private String method;// 函数方法

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @param extMessage
	 * @param e
	 */
	public ScriptRuntimeException(String script, Throwable e) {
		super(ExceptionType.SCRIPT, e);
		this.script = script;
	}

	/**
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

}
