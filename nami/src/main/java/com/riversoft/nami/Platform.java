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
	private static File PATH_REQUEST;// 请求
	private static File PATH_FUNCTION;// 函数文件夹
	private static File PATH_MP;// 公众号转发文件夹

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
	 * 公众号转发
	 * 
	 * @return
	 */
	static File getMpPath() {
		return PATH_MP;
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

		URL initFileUrl = Thread.currentThread().getContextClassLoader().getResource("logback-test.xml");// 开发模式特征
		if (initFileUrl == null) { // 标准部署
			initFileUrl = Thread.currentThread().getContextClassLoader().getResource("jdbc.properties");
			logger.info("切换目录参照系:{}", initFileUrl);
			PATH_ROOT = new File(initFileUrl.getFile()).getParentFile().getParentFile();
		} else {// 开发环境
			PATH_ROOT = new File(initFileUrl.getFile()).getParentFile();
		}
		logger.info("NAMI平台根目录初始化成功:{}", PATH_ROOT);

		PATH_REQUEST = new File(PATH_ROOT, "request");
		if (!PATH_REQUEST.exists()) {
			PATH_REQUEST.mkdirs();
		}
		logger.info("NAMI request path:{}", PATH_REQUEST);

		PATH_FUNCTION = new File(PATH_ROOT, "function");
		if (!PATH_FUNCTION.exists()) {
			PATH_FUNCTION.mkdirs();
		}
		logger.info("NAMI function path:{}", PATH_FUNCTION);

		PATH_MP = new File(PATH_ROOT, "mp");
		if (!PATH_MP.exists()) {
			PATH_MP.mkdirs();
		}
		logger.info("NAMI mp path:{}", PATH_MP);

	}

}
