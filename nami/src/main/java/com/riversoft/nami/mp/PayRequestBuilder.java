package com.riversoft.nami.mp;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.Formatter;
import com.riversoft.weixin.pay.mp.bean.UnifiedOrderRequest;

/**
 * @borball on 5/16/2016.
 */
public class PayRequestBuilder {

	public static UnifiedOrderRequest buildUnifiedOrderRequest(String mchId, Map<String, Object> map) {
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
			tradeNumber = mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(10);
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

		unifiedOrderRequest.setOpenId((String) map.get("user"));
		if (map.containsKey("product")) {
			unifiedOrderRequest.setProductId((String) map.get("product"));
		} else {
			unifiedOrderRequest.setProductId(
					mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(10));
		}

		if (map.containsKey("notify")) {
			unifiedOrderRequest.setNotifyUrl((String) map.get("notify"));
		} else {
			unifiedOrderRequest.setNotifyUrl(notificationUrl(unifiedOrderRequest.getTradeType()));
		}

		if (map.containsKey("limit")) {
			unifiedOrderRequest.setLimitPay((String) map.get("limit"));
		}

		// 必要验证
		if ("JSAPI".equalsIgnoreCase(unifiedOrderRequest.getTradeType())
				&& StringUtils.isEmpty(unifiedOrderRequest.getOpenId())) {
			throw new SystemRuntimeException(ExceptionType.WX, "OPEN_ID不允许空.");
		}

		return unifiedOrderRequest;
	}

	private static String notificationUrl(String tradeType) {
		boolean https = Boolean.valueOf(Config.get("wx.net.https", "false"));
		String domain = Config.get("wx.net.domain", "gzriver.com");

		boolean jsAPI = "JSAPI".equalsIgnoreCase(tradeType);
		return (https ? "https://" : "http://") + domain + "/wx/pay/mp/" + (jsAPI ? "notify" : "scan");
	}
}
