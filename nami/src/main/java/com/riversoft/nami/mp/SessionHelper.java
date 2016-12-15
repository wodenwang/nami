/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.mp;

import javax.servlet.http.HttpServletRequest;

import com.riversoft.weixin.common.oauth2.OpenUser;

/**
 * @author woden
 *
 */
public class SessionHelper {

	/**
	 * 设置用户
	 * 
	 * @param request
	 * @param user
	 */
	public static void setUser(HttpServletRequest request, OpenUser user) {
		request.getSession().setAttribute("mp_user", user);
	}

	/**
	 * 校验有没登录
	 * 
	 * @param request
	 * @return
	 */
	public static boolean checkLogin(HttpServletRequest request) {
		return request.getSession().getAttribute("mp_user") != null;
	}

	/**
	 * 获取用户
	 * 
	 * @param request
	 * @return
	 */
	public static OpenUser getUser(HttpServletRequest request) {
		return (OpenUser) request.getSession().getAttribute("mp_user");
	}
}
