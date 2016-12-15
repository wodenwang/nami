/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.sms;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsRequest;
import com.aliyuncs.sms.model.v20160927.SingleSendSmsResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.JsonMapper;
import com.riversoft.util.RandomUtils;

/**
 * 短信(验证码)
 * 
 * @author woden
 *
 */
@ScriptSupport("sms")
public class SmsFunction {

	private static Logger logger = LoggerFactory.getLogger(SmsFunction.class);

	// 暂时基于内存，可靠性考虑可以移到数据库
	private static Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(120, TimeUnit.SECONDS)
			.maximumSize(10000).build();

	/**
	 * 发送模板短信给指定手机号
	 * 
	 * @param mobile
	 *            手机号
	 * @param templateId
	 *            模板ID
	 * @param params
	 *            模板参数
	 */
	public void send(String mobile, String templateId, Map<String, String> params) {
		try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Sms", "sms.aliyuncs.com");
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", Config.get("sms.aliyun.accessKey"),
					Config.get("sms.aliyun.accessSecret"));
			IAcsClient client = new DefaultAcsClient(profile);
			SingleSendSmsRequest request = new SingleSendSmsRequest();

			request.setSignName(Config.get("sms.aliyun.signName"));
			request.setTemplateCode(templateId);
			request.setParamString(JsonMapper.defaultMapper().toJson(params));
			request.setRecNum(mobile);

			SingleSendSmsResponse httpResponse = client.getAcsResponse(request);
		} catch (ClientException e) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, e);
		}
	}

	/**
	 * 发送验证码
	 * 
	 * @param mobile
	 *            手机号
	 */
	public void code(String mobile) {
		String code = RandomUtils.createRandomCode(Integer.valueOf(Config.get("sms.code.length", "6")));
		cache.put(mobile, code);

		Map<String, String> params = new HashMap<>();
		params.put("code", code);
		send(mobile, Config.get("sms.code.template"), params);
	}

	/**
	 * 校验验证码
	 * 
	 * @param mobile
	 *            手机号
	 * @param code
	 *            用户输入的验证码
	 * @return
	 */
	public boolean verify(String mobile, String code) {
		return code.equals(cache.getIfPresent(mobile));
	}
}
