/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 系统通用异常
 * 
 * @author wodenwang
 * 
 */
@SuppressWarnings("serial")
public class SystemRuntimeException extends RuntimeException {
	/**
	 * 额外说明<br>
	 * 这里只需要添加有意义的说明，不需要写“请联系管理员”之类的友好信息。
	 */
	private String extMessage;
	/**
	 * 异常类型
	 */
	private ExceptionType type;

	/**
	 * @return the extMessage
	 */
	public String getExtMessage() {
		return extMessage;
	}

	/**
	 * @param extMessage
	 *            the extMessage to set
	 */
	public void setExtMessage(String extMessage) {
		this.extMessage = extMessage;
	}

	/**
	 * @return the type
	 */
	public ExceptionType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ExceptionType type) {
		this.type = type;
	}

	public SystemRuntimeException(ExceptionType type, String extMessage, Throwable e) {
		super(extMessage, e);
		this.type = type;
		this.extMessage = extMessage;
	}

	public SystemRuntimeException(ExceptionType type) {
		this(type, "", null);
	}

	public SystemRuntimeException(ExceptionType type, String extMessage) {
		this(type, extMessage, null);
	}

	public SystemRuntimeException(ExceptionType type, Throwable e) {
		this(type, "", e);
	}

	public SystemRuntimeException(Throwable e) {
		this(SystemRuntimeException.getType(e), e);
	}

	public SystemRuntimeException(String extMessage, Throwable e) {
		this(SystemRuntimeException.getType(e), extMessage, e);
	}

	/**
	 * 根据异常信息确定异常类型
	 * 
	 * @param e
	 * @return
	 */
	public static ExceptionType getType(Throwable e) {
		Throwable t = ExceptionUtils.getRootCause(e);
		if (t == null) {
			t = e;
		}

		for (ExceptionType type : ExceptionType.values()) {
			if (type.getClss() != null) {
				for (Class<? extends Throwable> clazz : type.getClss()) {
					if (clazz.isInstance(e)) {
						return type;
					}
				}
			}
		}
		return ExceptionType.DEFAULT;
	}

}
