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
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.nami.mp.SessionHelper;
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

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 确认微信来源
		if (!fromWx(request)) {
			throw new ServletException("只允许微信访问");
		}

		String requestUri = request.getRequestURI();
		Properties prop = findConfigFromUrl(requestUri);

		String url = prop.getProperty("url");
		String scope = prop.getProperty("scope", "snsapi_base");

		// 已登录则直接转发
		if (SessionHelper.checkLogin(request)) {
			response.sendRedirect(url);
			return;
		}

		String appId = Config.get("wx.fwh.appid");
		String secrect = Config.get("wx.fwh.secrect");
		AppSetting mpAppSetting = new AppSetting(appId, secrect);

		// 微信传入处理
		String code = request.getParameter("code");
		if (StringUtils.isNotEmpty(code)) {
			logger.debug("微信公众号code自动登录");
			login(request, mpAppSetting, code);
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
		SessionHelper.setUser(request, openUser);
		return openUser;
	}

}
