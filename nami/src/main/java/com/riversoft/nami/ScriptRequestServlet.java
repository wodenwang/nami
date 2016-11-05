/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.context.VariableContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExpressionAndScriptExecutors.ScriptValueObject;
import com.riversoft.core.script.ScriptType;
import com.riversoft.util.JsonMapper;

/**
 * 脚本路由
 * 
 * @author woden
 */
@SuppressWarnings("serial")
@WebServlet(name = "ScriptRequestServlet", urlPatterns = "/request/*")
public class ScriptRequestServlet extends HttpServlet {

	static Logger logger = LoggerFactory.getLogger(ScriptRequestServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 初始化上下文
		initContext(request);

		// 路由执行
		String requestUri = request.getRequestURI();
		logger.debug("获取到请求:{}", requestUri);

		Object result;
		try {
			ScriptValueObject scriptVo = findScriptFromUrl(requestUri);
			result = ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo);// 无上下文
		} catch (SystemRuntimeException e) {
			Map<String, Object> errResult = new HashMap<>();
			errResult.put("msg", e.getExtMessage());
			errResult.put("code", e.getType().getCode());
			result = errResult;
			response.setStatus(500);
		}

		response.setContentType("application/json;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println(JsonMapper.defaultMapper().toJson(result));
		out.flush();
		out.close();
	}

	/**
	 * 初始化request和session的localthread
	 * 
	 * @param request
	 */
	private void initContext(HttpServletRequest request) {
		// 设置threadlocal
		// 设置request
		{
			Enumeration<String> names = request.getParameterNames();
			Map<String, Object> params = new HashMap<>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				// logger.debug("当前表单数据[" + name + "]以设置入threadlocal.");
				params.put(name, request.getParameterValues(name));
			}
			RequestContext.init(request, params);// 设置
		}

		// 设置session
		{
			HttpSession session = request.getSession();
			Enumeration<String> names = session.getAttributeNames();
			Map<String, Object> params = new HashMap<>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				// logger.debug("当前会话数据[" + name + "]以设置入threadlocal.");
				params.put(name, session.getAttribute(name));
			}
			SessionContext.init(session, params);
		}

		// 设置variable
		{
			VariableContext.init();
		}
	}

	/**
	 * 获取脚本文件内容<br>
	 * 开发模式不考虑缓存
	 * 
	 * @param servletPath
	 * @return
	 */
	private ScriptValueObject findScriptFromUrl(String requestUri) {
		ScriptType type;
		String path = StringUtils.substring(requestUri, "/request".length());
		if (StringUtils.endsWith(path.toLowerCase(), ".js")) {
			type = ScriptType.JAVASCRIPT;
		} else if (StringUtils.endsWith(path.toLowerCase(), ".groovy")) {
			type = ScriptType.GROOVY;
		} else if (StringUtils.endsWith(path.toLowerCase(), ".el")) {
			type = ScriptType.EL;
		} else {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "请求协议不合法");
		}

		File file = new File(Platform.getRequestPath(), path);
		if (!file.exists()) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]不存在.");
		}

		try {
			return new ScriptValueObject(type, FileUtils.readFileToString(file));
		} catch (IOException e) {
			logger.error("", e);
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]无法读取.", e);
		}
	}

}
