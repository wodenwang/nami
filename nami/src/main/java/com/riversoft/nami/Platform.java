/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NAMI平台类<br>
 * 保存NAMI对应的各类路径
 * 
 * @author woden
 *
 */
public class Platform {

	static Logger logger = LoggerFactory.getLogger(Platform.class);

	private static File PATH_ROOT;// 根路径
	private static File PATH_CONFIG;// 配置
	private static File PATH_REQUEST;// 请求
	private static File PATH_FUNCTION;// 函数文件夹

	/**
	 * 函数目录
	 * 
	 * @return
	 */
	static File getFunctionPath() {
		return PATH_FUNCTION;
	}

	/**
	 * 请求目录
	 * 
	 * @return
	 */
	static File getRequestPath() {
		return PATH_REQUEST;
	}

	/**
	 * NAMI平台根目录
	 * 
	 * @return
	 */
	static File getRootPath() {
		return PATH_ROOT;
	}

	/**
	 * NAMI平台初始化,tomcat启动时调用
	 */
	protected static void init() {
		// 已初始化,则无需重复处理
		if (PATH_ROOT != null) {
			logger.warn("NAMI平台无需重复初始化.");
			return;
		}

		URL initFileUrl = Thread.currentThread().getContextClassLoader().getResource("logback-test.xml");// 获取得到logback-test
		if (initFileUrl == null) { // 标准部署
			URL url = Thread.currentThread().getContextClassLoader().getResource("production.properties");
			PATH_ROOT = getPlatformRootPath(url, 4);
		} else {// 客户自定义部署
			PATH_ROOT = new File(initFileUrl.getFile()).getParentFile();
		}
		logger.info("NAMI平台根目录初始化成功:{}", PATH_ROOT);

		PATH_CONFIG = new File(PATH_ROOT, "config");
		if (!PATH_CONFIG.exists()) {
			PATH_CONFIG.mkdirs();
		}

		PATH_REQUEST = new File(PATH_ROOT, "request");
		if (!PATH_REQUEST.exists()) {
			PATH_REQUEST.mkdirs();
		}

		PATH_FUNCTION = new File(PATH_ROOT, "function");
		if (!PATH_FUNCTION.exists()) {
			PATH_FUNCTION.mkdirs();
		}
	}

	/**
	 * 获取生产系统平台路径
	 * 
	 * @param url
	 * @param parentLevel
	 * @return
	 */
	private static File getPlatformRootPath(URL url, int parentLevel) {
		if (url == null) {
			return null;
		}

		File file = new File(url.getFile());
		if (file.exists()) {

			File root = file;
			for (int i = 0; i < parentLevel; i++) {
				root = root.getParentFile();
				if (!root.exists()) {
					return null;
				}
			}
			if (root.exists() && root.isDirectory()) {
				return root;
			}
		}
		return null;
	}
}
