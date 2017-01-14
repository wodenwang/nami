/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.JsonMapper;
import com.riversoft.weixin.app.base.AppSetting;
import com.riversoft.weixin.app.user.SessionKey;
import com.riversoft.weixin.app.user.Users;

/**
 * NAMI会话管理器
 * 
 * @author woden
 */
public class SessionManager {

	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);

	// 缓存存储 TODO
	// 暂时用map存储,分布式时放到独立的会话服务器或redis中
	private static Map<String, Map<String, Object>> FULL_SESSION_POOL = new ConcurrentHashMap<>();

	private static AppSetting appSetting = new AppSetting(Config.get("wx.app.appId"), Config.get("wx.app.secrect"));

	/**
	 * 根据界面的jscode鉴权并兑换sessionkey
	 * 
	 * @param code
	 * @return
	 */
	public static String jscode2session(String code) {
		SessionKey sessionKey = Users.with(appSetting).code2Session(code);
		String openId = sessionKey.getOpenId();

		String namiToken = IDGenerator.next();
		Map<String, Object> obj = new HashMap<>();
		obj.put("openId", openId);
		obj.put("sessionKey", sessionKey.getSessionKey());
		FULL_SESSION_POOL.put(namiToken, obj);
		return namiToken;
	}

	/**
	 * 获取缓存的session值
	 * 
	 * @param key
	 * @return
	 */
	public static Map<String, Object> get(String namiToken) {
		if (!FULL_SESSION_POOL.containsKey(namiToken)) {
			throw new SystemRuntimeException(ExceptionType.WX, "登录超时");
		}

		return FULL_SESSION_POOL.get(namiToken);
	}

	/**
	 * 获取更多用户属性
	 * 
	 * @param namiToken
	 * @param rawData
	 * @param signature
	 * @param encryptedData
	 * @param iv
	 */
	public static void syncUserInfo(String namiToken, String rawData, String signature, String encryptedData,
			String iv) {
		Map<String, Object> userInfo = JsonMapper.defaultMapper().fromJson(rawData, Map.class);
		Map<String, Object> obj = get(namiToken);
		if (userInfo != null && obj != null) {
			obj.put("nickName", userInfo.get("nickName"));
			obj.put("avatarUrl", userInfo.get("avatarUrl"));
		}
		logger.debug("更新会话:{}", obj);
	}
}
