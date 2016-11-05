/*
 * File Name  :InitServlet.java
 * Create Date:2012-11-6 上午12:06:17
 * Author     :woden
 */

package com.riversoft.nami;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;

/**
 * 初始化Servlet容器.
 * 
 */
@WebServlet(loadOnStartup = 0, urlPatterns = "/InitServlet", name = "InitServlet", displayName = "InitServlet", description = "用于NAMI WEB容器启动初始化")
@SuppressWarnings("serial")
public class InitServlet extends HttpServlet {

	static Logger logger = LoggerFactory.getLogger(InitServlet.class);

	public void init(ServletConfig config) throws ServletException {

		logger.info("========== NAMI 平台安全初始化  开始 ==========");
		Platform.init();
		logger.info("========== NAMI 平台安全初始化  结束 ==========");

		logger.info("========== NAMI Spring容器初始化  开始 ==========");
		BeanFactory.init("classpath:applicationContext.xml");
		logger.info("========== NAMI Spring容器初始化  结束 ==========");

	}

}
