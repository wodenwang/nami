/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.session;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
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
		logger.info("登录:openid={},sessionKey={}", sessionKey.getOpenId(), sessionKey.getSessionKey());
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
		logger.debug("获取NAMI_TOKEN:{}", namiToken);
		if (!FULL_SESSION_POOL.containsKey(namiToken)) {
			throw new SystemRuntimeException(ExceptionType.WX, "登录超时");
		}

		return FULL_SESSION_POOL.get(namiToken);
	}

	/**
	 * 获取更多用户属性
	 * 
	 * @param namiToken
	 * @param encryptedData
	 * @param iv
	 */
	public static void syncUserInfo(String namiToken, String encryptedData, String iv) {
		// logger.debug("获取更多属性:rawData={},signature={},encryptedData={},iv={}",
		// rawData, signature, encryptedData, iv);

		Map<String, Object> obj = get(namiToken);
		Map<String, Object> userInfo = JsonMapper.defaultMapper()
				.fromJson(decrypt(encryptedData, (String) obj.get("sessionKey"), iv), Map.class);
		// logger.debug("获取userInfo:{}", userInfo);
		// logger.debug("token兑换sessionKey:token={}", namiToken);
		if (userInfo != null && obj != null) {
			obj.put("nickName", userInfo.get("nickName"));
			obj.put("avatarUrl", userInfo.get("avatarUrl"));
			obj.put("unionId", userInfo.get("unionId"));
			obj.put("gender", userInfo.get("gender"));
			obj.put("language", userInfo.get("language"));
			obj.put("city", userInfo.get("city"));
			obj.put("province", userInfo.get("province"));
			obj.put("country", userInfo.get("country"));
		}
		logger.debug("更新会话:{}", obj);
	}

	private static String decrypt(String encryptedData, String sessionKey, String iv) {
		Decoder decoder = Base64.getDecoder();
		try {
			byte[] result = AES.decrypt(decoder.decode(encryptedData), decoder.decode(sessionKey),
					AES.generateIV(decoder.decode(iv)));

			return StringUtils.toString(result, "utf-8");
		} catch (Exception e) {
			throw new SystemRuntimeException(e);
		}
	}
}
