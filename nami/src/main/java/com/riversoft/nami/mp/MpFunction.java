/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.mp;

import java.util.Map;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.weixin.common.jsapi.JsAPISignature;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.jsapi.JsAPIs;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.pay.mp.JsSigns;
import com.riversoft.weixin.pay.mp.Orders;
import com.riversoft.weixin.pay.mp.bean.JSSignature;
import com.riversoft.weixin.pay.mp.bean.UnifiedOrderRequest;
import com.riversoft.weixin.pay.mp.bean.UnifiedOrderResponse;

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
	 * 获取当前用户
	 * 
	 * @return
	 */
	public Object user() {
		return SessionHelper.getUser(RequestContext.getCurrent().getHttpRequest());
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
		public JSSignature signature(String prepayId) {
			return JsSigns.with(getSetting()).createJsSignature(prepayId);
		}

		/**
		 * 统一下单
		 * 
		 * @param params
		 * @return
		 */
		public UnifiedOrderResponse order(Map<String, Object> params) {
			PaySetting paySetting = getSetting();
			UnifiedOrderRequest orderRequest = PayRequestBuilder.buildUnifiedOrderRequest(paySetting.getMchId(),
					params);
			return Orders.with(paySetting).unifiedOrder(orderRequest);
		}
	}

}
