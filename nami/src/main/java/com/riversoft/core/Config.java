/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.riversoft.core.exception.SystemRuntimeException;

/**
 * @author Woden
 * 
 */
public class Config {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	/**
	 * 从spring获取配置
	 * 
	 * @return
	 */
	private static Properties getProperties() {
		return (Properties) BeanFactory.getInstance().getBean("config");
	}

	/**
	 * 获取值
	 * 
	 * @param name
	 * @return
	 */
	public static String get(String name) {
		return (String) getProperties().get(name);
	}

	/**
	 * 获取值
	 * 
	 * @param name
	 * @param def
	 *            默认值
	 * @return
	 */
	public static String get(String name, String def) {
		String val = get(name);
		if (StringUtils.isEmpty(val)) {
			return def;
		} else {
			return val;
		}
	}

	/**
	 * 获取资源文件中文值
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	public static String getChinese(String name, String def) {
		String val = fromChinese(get(name));
		if (StringUtils.isEmpty(val)) {
			return def;
		} else {
			return val;
		}
	}

	/**
	 * 将资源文件中的中文格式化
	 * 
	 * @param sourceString
	 * @return
	 */
	private static String fromChinese(String sourceString) {
		if (sourceString == null || "".equals(sourceString)) {
			return "";
		} else {
			String result;
			try {
				result = new String(sourceString.getBytes("iso8859-1"), "GBK");
			} catch (UnsupportedEncodingException e) {
				logger.error("字符串的中文化异常:Formatter.fromChinese : " + e);
				result = "";
			}
			return result;
		}
	}

	/**
	 * 设值
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value) {
		if (StringUtils.isEmpty(value)) {
			getProperties().remove(key);
		} else {
			getProperties().setProperty(key, value);
		}
	}

	/**
	 * 设值
	 * 
	 * @param key
	 * @param value
	 */
	public static void setChinese(String key, String value) {
		if (StringUtils.isEmpty(value)) {
			set(key, null);
		} else {
			String result;
			try {
				result = new String(value.getBytes("GBK"), "iso8859-1");
				set(key, result);
			} catch (UnsupportedEncodingException e) {
				logger.error("字符串的中文化异常:Formatter.fromChinese : " + e);
			}
		}
	}

	/**
	 * 保存回写到properties文件
	 * 
	 * @param pixel
	 *            前缀.如page等
	 */
	public synchronized static void store(String pixel) {
		Properties sys = getProperties();
		Properties prop = new Properties();
		for (Object key : sys.keySet()) {
			if (key.toString().startsWith(pixel + ".")) {
				prop.put(key, sys.get(key));
			}
		}

		try (FileOutputStream fos = new FileOutputStream(ResourceUtils.getFile("classpath:" + pixel + ".properties"));) {
			prop.store(fos, "store by sys.");
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

}
