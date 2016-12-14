/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.weixin.app.base.AppSetting;
import com.riversoft.weixin.app.user.SessionKey;
import com.riversoft.weixin.app.user.Users;

/**
 * NAMI会话管理器
 * 
 * @author woden
 */
public class SessionManager {

	// 缓存存储 TODO
	// 暂时用map存储
	private static Map<String, SessionKey> SESSION_KEYS = new ConcurrentHashMap<>();
	private static AppSetting appSetting = new AppSetting(Config.get("wx.app.appid"), Config.get("wx.app.secrect"));

	/**
	 * 根据界面的jscode鉴权并兑换sessionkey
	 * 
	 * @param code
	 * @return
	 */
	public static String jscode2session(String code) {
		SessionKey sessionKey = Users.with(appSetting).code2Session(code);
		String namiKey = IDGenerator.next();
		// TODO 校验OPEN_ID,清除重复缓存

		SESSION_KEYS.put(namiKey, sessionKey);
		return namiKey;
	}

	/**
	 * 获取缓存的session值
	 * 
	 * @param key
	 * @return
	 */
	public static SessionKey get(String namiKey) {
		if (!SESSION_KEYS.containsKey(namiKey)) {
			throw new SystemRuntimeException(ExceptionType.WX, "登录超时");
		}

		return SESSION_KEYS.get(namiKey);
	}

	/**
	 * 解密获取unionid并保存
	 * 
	 * @param namiKey
	 * @param rawData
	 * @param signature
	 */
	public static void saveUnionId(String namiKey, String rawData, String signature) {
		// TODO
	}
}
