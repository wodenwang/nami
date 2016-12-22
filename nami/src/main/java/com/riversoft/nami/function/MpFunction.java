/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.function;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.riversoft.weixin.pay.payment.Payments;
import com.riversoft.weixin.pay.payment.bean.Signature;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderRequest;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderResponse;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.Formatter;
import com.riversoft.weixin.common.jsapi.JsAPISignature;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.jsapi.JsAPIs;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.pay.mp.JsSigns;

/**
 * @author woden
 *
 */
@ScriptSupport("mp")
public class MpFunction {

	private final AppSetting getSetting() {
		AppSetting appSetting = new AppSetting();
		appSetting.setAppId((String) Config.get("wx.fwh.appId"));
		appSetting.setSecret((String) Config.get("wx.fwh.secrect"));
		appSetting.setToken((String) Config.get("wx.fwh.token"));
		appSetting.setAesKey((String) Config.get("wx.fwh.encodingAESKey"));
		return appSetting;
	}

	/**
	 * 获取jssdk验证串
	 * 
	 * @param url
	 * @return
	 */
	public JsAPISignature signature(String url) {
		return JsAPIs.with(getSetting()).createJsAPISignature(url);
	}

	/**
	 * 公众号支付函数库
	 * 
	 * @return
	 */
	public MpPayFunction getPay() {
		return new MpPayFunction();
	}

	/**
	 * 服务号支付
	 * 
	 * @author woden
	 *
	 */
	public static class MpPayFunction {

		private final PaySetting getSetting() {
			PaySetting paySetting = new PaySetting();
			paySetting.setAppId((String) Config.get("wx.fwh.pay.appId"));
			paySetting.setMchId((String) Config.get("wx.fwh.pay.mchId"));
			paySetting.setKey((String) Config.get("wx.fwh.pay.paySecret"));
			paySetting.setCertPath((String) Config.get("wx.fwh.pay.certPath"));
			paySetting.setCertPassword((String) Config.get("wx.fwh.pay.certPassword"));
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
			return JsSigns.with(getSetting()).createSignature(prepayId);
		}

		/**
		 * 统一下单
		 * 
		 * @param params
		 * @return
		 */
		public UnifiedOrderResponse order(Map<String, Object> params) {
			PaySetting paySetting = getSetting();
			UnifiedOrderRequest orderRequest = buildUnifiedOrderRequest(paySetting.getMchId(), params);
			return Payments.with(paySetting).unifiedOrder(orderRequest);
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

			if (map.containsKey("notify")) {
				unifiedOrderRequest.setNotifyUrl((String) map.get("notify"));
			} else {
				unifiedOrderRequest.setNotifyUrl(notificationUrl());
			}

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

		private static String notificationUrl() {
			HttpServletRequest request = RequestContext.getCurrent().getHttpRequest();
			String domain = request.getScheme() + "://" + request.getServerName()
					+ (request.getServerPort() == 80 ? "" : (":" + request.getServerPort())) + request.getContextPath();
			return domain + "/callback/pay";
		}
	}

}
