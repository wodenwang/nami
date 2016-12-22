/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.VariableContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExpressionAndScriptExecutors.ScriptValueObject;
import com.riversoft.nami.session.SessionHelper;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import com.riversoft.weixin.common.oauth2.AccessToken;
import com.riversoft.weixin.common.oauth2.OpenUser;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.oauth2.MpOAuth2s;

/**
 * @author woden
 *
 */
@SuppressWarnings("serial")
@WebServlet(name = "MpRedirectServlet", urlPatterns = "/mp/*")
public class MpRedirectServlet extends HttpServlet {

	static Logger logger = LoggerFactory.getLogger(MpRedirectServlet.class);
	private static AppSetting mpAppSetting = new AppSetting(Config.get("wx.fwh.appId"), Config.get("wx.fwh.secrect"));

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 确认微信来源
		if (!fromWx(request)) {
			throw new ServletException("只允许微信访问");
		}

		String requestUri = request.getRequestURI();
		String queryString = request.getQueryString();

		Properties prop = findConfigFromUrl(requestUri);

		String url = prop.getProperty("url");
		String scope = prop.getProperty("scope", "snsapi_base");
		if (StringUtils.isNotEmpty(queryString)) {
			url = url + "?" + queryString;
		}

		// 已登录则直接转发
		if (SessionHelper.checkMpLogin(request)) {
			// 执行切片
			executeMpAspect(request, requestUri);
			response.sendRedirect(url);
			return;
		}

		// 微信传入处理
		String code = request.getParameter("code");
		if (StringUtils.isNotEmpty(code)) {
			logger.debug("微信公众号code自动登录");
			login(request, mpAppSetting, code);
			// 执行切片
			executeMpAspect(request, requestUri);
			response.sendRedirect(url);
			return;
		}

		String redirectUrl = MpOAuth2s.with(mpAppSetting).authenticationUrl(getFullURL(request), scope);
		logger.info("重定向到微信:{}", redirectUrl);
		response.sendRedirect(redirectUrl);
	}

	/**
	 * 是否微信访问
	 * 
	 * @param request
	 * @return
	 */
	private static boolean fromWx(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent");
		return StringUtils.isNotEmpty(userAgent) && userAgent.toLowerCase().contains("micromessenger");
	}

	/**
	 * 获取当前完整网址
	 * 
	 * @param request
	 * @return
	 */
	private static String getFullURL(HttpServletRequest request) {
		String url;

		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();
		if (StringUtils.isEmpty(queryString)) {
			url = requestURL.toString();
		} else {
			url = requestURL.append('?').append(queryString).toString();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("当前FULL URL:{}", url);
		}
		return url;
	}

	/**
	 * 获取脚本文件内容<br>
	 * 开发模式不考虑缓存
	 * 
	 * @param requestUri
	 * @return
	 */
	private static Properties findConfigFromUrl(String requestUri) {
		String path = StringUtils.substring(requestUri, "/mp".length());

		File file = new File(Platform.getMpPath(), path + ".properties");
		if (!file.exists()) {
			throw new SystemRuntimeException(ExceptionType.WX, "配置[" + file.getAbsolutePath() + "]不存在.");
		}

		// TODO 暂不考虑缓存问题
		Properties prop = new Properties();
		try (InputStream is = new FileInputStream(file)) {
			prop.load(is);
			return prop;
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.WX, "配置[" + file.getAbsolutePath() + "]不存在.");
		}
	}

	/**
	 * 入口脚本文件
	 * 
	 * @param requestUri
	 * @return
	 */
	private ScriptValueObject findScriptFromUrl(String requestUri) {
		String path = StringUtils.substring(requestUri, "/mp".length());

		File file = new File(Platform.getMpPath(), path + ".groovy");
		if (!file.exists()) {
			file = new File(Platform.getMpPath(), path + ".js");
		}

		if (!file.exists()) {
			return null;
		}

		try {
			return new ScriptValueObject(file);
		} catch (IOException e) {
			logger.error("", e);
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]无法读取.", e);
		}
	}

	/**
	 * 入口切片执行
	 * 
	 * @param request
	 * @param requestUri
	 */
	private void executeMpAspect(HttpServletRequest request, String requestUri) {
		ScriptValueObject scriptVo = findScriptFromUrl(requestUri);
		logger.debug("获取到脚本:{}", scriptVo);
		if (scriptVo != null) {
			// 初始化上下文
			initContext(request);
			ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo);// 无上下文
		}
	}

	/**
	 * 微信公众号code自动登录
	 * 
	 * @param request
	 * @param mpKey
	 * @param code
	 */
	private static OpenUser login(HttpServletRequest request, AppSetting appSetting, String code) {
		AccessToken accessToken = MpOAuth2s.with(appSetting).getAccessToken(code);
		OpenUser openUser;
		try {
			openUser = MpOAuth2s.with(appSetting).userInfo(accessToken.getAccessToken(), accessToken.getOpenId());
		} catch (WxRuntimeException e) {
			throw new SystemRuntimeException(ExceptionType.WX, "无法获取公众号信息.");
		}
		SessionHelper.setMpUser(request, openUser);
		return openUser;
	}

	/**
	 * 初始化request和session的localthread
	 * 
	 * @param request
	 */
	private void initContext(HttpServletRequest request) {
		// 设置threadlocal
		// 设置request
		{
			Enumeration<String> names = request.getParameterNames();
			Map<String, Object> params = new HashMap<>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				// logger.debug("当前表单数据[" + name + "]以设置入threadlocal.");
				params.put(name, request.getParameterValues(name));
			}
			RequestContext.init(request, params);// 设置
		}

		// 设置variable
		{
			VariableContext.init();
		}
	}

}
