/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.function;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.Formatter;
import com.riversoft.weixin.app.base.AppSetting;
import com.riversoft.weixin.common.util.XmlObjectMapper;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.pay.payment.Payments;
import com.riversoft.weixin.pay.payment.Signatures;
import com.riversoft.weixin.pay.payment.bean.PaymentNotification;
import com.riversoft.weixin.pay.payment.bean.Signature;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderRequest;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderResponse;

/**
 * 小程序函数库
 * 
 * @author woden
 *
 */
@ScriptSupport("app")
public class WeAppFunction {
	private static Logger logger = LoggerFactory.getLogger(WeAppFunction.class);

	private final AppSetting getSetting() {
		AppSetting appSetting = new AppSetting();
		appSetting.setAppId((String) Config.get("wx.app.appId"));
		appSetting.setSecret((String) Config.get("wx.app.secrect"));
		return appSetting;
	}

	/**
	 * 支付函数库
	 * 
	 * @return
	 */
	public WeAppPayFunction getPay() {
		return new WeAppPayFunction();
	}

	/**
	 * 支付
	 * 
	 * @author woden
	 *
	 */
	public static class WeAppPayFunction {

		private final PaySetting getSetting() {
			PaySetting paySetting = new PaySetting();
			paySetting.setAppId((String) Config.get("wx.app.pay.appId"));
			paySetting.setMchId((String) Config.get("wx.app.pay.mchId"));
			paySetting.setKey((String) Config.get("wx.app.pay.paySecret"));
			paySetting.setCertPath((String) Config.get("wx.app.pay.certPath"));
			paySetting.setCertPassword((String) Config.get("wx.app.pay.certPassword"));
			return paySetting;
		}

		/**
		 * 获取jssdk验证串
		 *
		 * @param prepayId
		 *            预付订单ID
		 * @return
		 */
		public Signature signature(String prepayId) {
			return Signatures.with(getSetting()).createJsSignature(prepayId);
		}

		public Map<String, Object> order(Map<String, Object> params) {
			PaySetting paySetting = getSetting();
			UnifiedOrderRequest orderRequest = buildUnifiedOrderRequest(paySetting.getMchId(), params);
			UnifiedOrderResponse orderResponse = Payments.with(paySetting).unifiedOrder(orderRequest);
			return buildUnifiedOrderResponseMap(orderRequest, orderResponse);
		}

		private static Map<String, Object> buildUnifiedOrderResponseMap(UnifiedOrderRequest orderRequest,
				UnifiedOrderResponse orderResponse) {
			Map<String, Object> result = new HashMap<>();
			result.put("codeUrl", orderResponse.getCodeUrl());
			result.put("deviceInfo", orderResponse.getDeviceInfo());
			result.put("errorCode", orderResponse.getErrorCode());
			result.put("errorCodeDesc", orderResponse.getErrorCodeDesc());
			result.put("prepayId", orderResponse.getPrepayId());
			result.put("resultCode", orderResponse.getResultCode());
			result.put("returnMessage", orderResponse.getReturnMessage());
			result.put("tradeType", orderResponse.getTradeType());
			result.put("openId", orderRequest.getOpenId());
			result.put("tradeNumber", orderRequest.getTradeNumber());
			result.put("productId", orderRequest.getProductId());
			return result;
		}

		private static UnifiedOrderRequest buildUnifiedOrderRequest(String mchId, Map<String, Object> map) {
			UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
			try {
				unifiedOrderRequest.setBillCreatedIp(Inet4Address.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				unifiedOrderRequest.setBillCreatedIp("127.0.0.1");
			}
			unifiedOrderRequest.setBody((String) map.get("body"));
			unifiedOrderRequest.setTotalFee((Integer) map.get("total"));

			String tradeNumber = null;
			if (map.containsKey("tradeNumber")) {
				tradeNumber = map.get("tradeNumber").toString();
			} else {
				tradeNumber = mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS")
						+ new Random().nextInt(10);
			}

			unifiedOrderRequest.setTradeNumber(tradeNumber);
			if (map.containsKey("detail")) {
				unifiedOrderRequest.setDetail((String) map.get("detail"));
			}
			if (map.containsKey("attach")) {
				unifiedOrderRequest.setAttach((String) map.get("attach"));
			}
			if (map.containsKey("start")) {
				unifiedOrderRequest.setTimeStart((String) map.get("start"));
			}
			if (map.containsKey("expire")) {
				unifiedOrderRequest.setTimeExpire((String) map.get("expire"));
			}
			if (map.containsKey("tag")) {
				unifiedOrderRequest.setGoodsTag((String) map.get("tag"));
			}

			if (map.containsKey("type")) {
				unifiedOrderRequest.setTradeType((String) map.get("type"));
			} else {
				unifiedOrderRequest.setTradeType("JSAPI");
			}

			if (map.containsKey("product")) {
				unifiedOrderRequest.setProductId((String) map.get("product"));
			} else {
				unifiedOrderRequest.setProductId(
						mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(10));
			}

			// 回调地址
			unifiedOrderRequest.setNotifyUrl(notificationUrl((String) map.get("notify")));

			if (map.containsKey("limit")) {
				unifiedOrderRequest.setLimitPay((String) map.get("limit"));
			}

			unifiedOrderRequest.setOpenId((String) map.get("openId"));// 支付人
			// 必要验证
			if ("JSAPI".equalsIgnoreCase(unifiedOrderRequest.getTradeType())
					&& StringUtils.isEmpty(unifiedOrderRequest.getOpenId())) {
				throw new SystemRuntimeException(ExceptionType.WX, "OPEN_ID不允许空.");
			}

			return unifiedOrderRequest;
		}

		private static String notificationUrl(String notify) {
			// http或https打头的直接返回
			if (StringUtils.isNotEmpty(notify) && (StringUtils.startsWith(notify.toLowerCase(), "http://")
					|| StringUtils.startsWith(notify.toLowerCase(), "https://"))) {
				return notify;
			}

			HttpServletRequest request = RequestContext.getCurrent().getHttpRequest();
			String domain = request.getScheme() + "://" + request.getServerName()
					+ ((request.getServerPort() == 80 || request.getServerPort() == 443) ? ""
							: (":" + request.getServerPort()))
					+ request.getContextPath();
			// 默认值
			if (StringUtils.isEmpty(notify)) {
				return domain + "/request/callback.groovy";
			}

			return domain + notify;
		}

		/**
		 * 获取当前支付回调请求.在回调中使用.
		 * 
		 * @return
		 */
		public PaymentNotification currentNotify() {
			HttpServletRequest request = RequestContext.getCurrent().getHttpRequest();
			String content;
			PaymentNotification paymentNotification;
			try {
				content = IOUtils.toString(request.getInputStream(), "UTF-8");
				logger.info("微信支付通知结果:\n{}", content);
				paymentNotification = XmlObjectMapper.defaultMapper().fromXml(content, PaymentNotification.class);
			} catch (IOException e) {
				throw new SystemRuntimeException(ExceptionType.WX_PAY_NOTIFY, e);
			}

			String appId = paymentNotification.getAppId();
			PaySetting paySetting = getSetting();
			if (!StringUtils.equals(paySetting.getAppId(), appId)) {
				throw new SystemRuntimeException(ExceptionType.WX_PAY_NOTIFY, "支付通知不匹配.");
			}

			if (!Payments.with(paySetting).checkSignature(paymentNotification)) {
				throw new SystemRuntimeException(ExceptionType.WX_PAY_NOTIFY, "签名不匹配.");
			}

			if (!paymentNotification.success()) {
				throw new SystemRuntimeException(ExceptionType.WX_PAY_NOTIFY, "支付没有成功.");
			}

			return paymentNotification;
		}

		/**
		 * 支付通知成功
		 * 
		 * @return
		 */
		public String notifySuccess() {
			return notifyError(null);
		}

		/**
		 * 支付通知失败
		 * 
		 * @param errorMsg
		 * @return
		 */
		public String notifyError(String errorMsg) {
			String template = "<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[%s]]></return_msg></xml>";
			if (StringUtils.isEmpty(errorMsg)) {
				return String.format(template, "SUCCESS", "OK");
			}

			// 错误
			return String.format(template, "FAIL", errorMsg);
		}
	}
}
