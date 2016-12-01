/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常类型枚举
 * 
 * @author wodenwang
 * 
 */
@SuppressWarnings("unchecked")
public enum ExceptionType {
	/**
	 * 获取到异常,界面采用友好提示
	 */
	INFO(0, "非异常"),
	/**
	 * 页面采用警告样式
	 */
	WARN(1, "非异常"),

	DEFAULT(9999, "未知异常"),

	/**
	 * 上下文属性异常
	 */
	CONTEXT(100, "上下文属性异常。"),
	CONTEXT_EMPTY(101, "属性不存在。"),

	/**
	 * 脚本执行属性异常
	 */
	SCRIPT(200, "脚本执行异常。"),
	SCRIPT_ATTRIBUTE_EMPTY(201, "属性不存在。"),
	SCRIPT_METHOD_EMPTY(202, "方法不存在。"),
	SCRIPT_COMPILE_ERROR(203, "脚本编译出错。"),
	SCRIPT_BUSI(204, "脚本业务异常。"),

	/**
	 * 微信交互相关异常
	 */
	WX(300, "微信交互相关异常.")

	/**/
	;
	/**
	 * 异常码，格式如：100,1203.<br>
	 * 前两位为类型编码，后两位为明细编码。如：100表示数据库类型异常，101表示数据库新增异常。
	 */
	private int code;
	/**
	 * 通用提示信息。
	 */
	private String msg;
	/**
	 * 异常类型匹配。 考虑使用类名正则匹配，如DataAccess*Exception之类。
	 */
	private List<Class<? extends Throwable>> clss;

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the clss
	 */
	public List<Class<? extends Throwable>> getClss() {
		return clss;
	}

	/**
	 * 枚举构造
	 * 
	 * @param code
	 * @param msg
	 * @param clss
	 */
	private ExceptionType(int code, String msg, Class<? extends Throwable>... clss) {
		this.code = code;
		this.msg = msg;
		if (clss != null && clss.length > 0) {
			this.clss = Arrays.asList(clss);
		} else {
			this.clss = new ArrayList<>();
		}
	}

}
