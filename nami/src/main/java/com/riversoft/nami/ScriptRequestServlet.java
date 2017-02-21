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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.VariableContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExpressionAndScriptExecutors.ScriptValueObject;
import com.riversoft.util.JsonMapper;

import groovy.lang.GString;

/**
 * 脚本路由
 * 
 * @author woden
 */
@SuppressWarnings("serial")
@WebServlet(name = "ScriptRequestServlet", urlPatterns = "/request/*")
@MultipartConfig
public class ScriptRequestServlet extends HttpServlet {

	static Logger logger = LoggerFactory.getLogger(ScriptRequestServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 初始化上下文
		initContext(request);

		// 路由执行
		String requestUri = request.getRequestURI();
		logger.debug("获取到请求:{},参数:{}", requestUri, request.getQueryString());

		Object result;
		try {
			ScriptValueObject scriptVo = findScriptFromUrl(requestUri);
			result = ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo);// 无上下文
		} catch (SystemRuntimeException e) {
			Map<String, Object> errResult = new HashMap<>();
			String msg = e.getExtMessage();
			if (StringUtils.isEmpty(msg)) {
				msg = e.getType().getMsg();
			}
			errResult.put("code", -1);
			errResult.put("msg", msg);
			result = errResult;
			response.setStatus(299);// 299表示NAMI业务逻辑错误;前端封转对状态码的判断
		}

		response.setContentType("application/json;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		if (result == null || StringUtils.isEmpty(result.toString())) {// 空,则表示成功但无返回
			out.println("{msg:'处理成功',code:0}");
		} else if (result instanceof String || result instanceof GString) {// 字符串,直接转换
			out.println(result.toString());
		} else {
			out.println(JsonMapper.defaultMapper().toJson(result));
		}
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

		// 设置variable
		{
			VariableContext.init();
		}
	}

	/**
	 * 获取脚本文件内容<br>
	 * 开发模式不考虑缓存
	 * 
	 * @param requestUri
	 * @return
	 */
	private ScriptValueObject findScriptFromUrl(String requestUri) {
		String path = StringUtils.substring(requestUri, "/request".length());

		File file = new File(Platform.getRequestPath(), path);
		if (!file.exists()) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]不存在.");
		}

		try {
			return new ScriptValueObject(file);
		} catch (IOException e) {
			logger.error("", e);
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]无法读取.", e);
		}
	}

}
