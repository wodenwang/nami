/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.UploadFile;
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
			result = evaluateScript(requestUri);
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
	 * 初始化request和session的threadlocal
	 * 
	 * @param request
	 */
	private void initContext(HttpServletRequest request) throws ServletException, IOException {
		// 设置threadlocal
		// 设置request
		{
			Map<String, Object> params = new HashMap<>();

			// 文件类请求
			if (ServletFileUpload.isMultipartContent(request)) {
				Collection<Part> parts = request.getParts();
				for (Part part : parts) {
					String name = part.getName();
					String fileName = getFileName(part);

					if (StringUtils.isNotEmpty(fileName)) {
						logger.debug("获取文件:{} = {}", name, fileName);
						params.put(name, new UploadFile(fileName, IOUtils.toByteArray(part.getInputStream())));
					} else {
						String value = IOUtils.toString(part.getInputStream(), "utf-8");
						logger.debug("{} = {}", name, value);
						params.put(name, value);
					}
				}
			} else {
				Enumeration<String> names = request.getParameterNames();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					logger.debug("当前表单数据[{}]以设置入threadlocal,值:{}", name, request.getParameterValues(name));
					params.put(name, request.getParameterValues(name));
				}
			}
			RequestContext.init(request, params);// 设置
		}

		// 设置variable
		{
			VariableContext.init();
		}
	}

	/**
	 * 获取文件名
	 * 
	 * @param part
	 * @return
	 */
	private String getFileName(Part part) {

		try {// servlet3.1才支持
			return part.getSubmittedFileName();
		} catch (Throwable e) {

			String header = part.getHeader("Content-Disposition");
			// logger.debug("header:{}", header);
			if (header.indexOf("filename=\"") < 0) {
				return null;
			}

			String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
			return fileName;
		}
	}

	/**
	 * 获取脚本文件内容<br>
	 * 开发模式不考虑缓存
	 * 
	 * @param requestUri
	 * @return
	 */
	private Object evaluateScript(String requestUri) {
		ScriptValueObject scriptVo;
		String path = StringUtils.substring(requestUri, "/request".length());

		File file = new File(Platform.getRequestPath(), path);
		if (!file.exists()) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]不存在.");
		}

		try {
			scriptVo = new ScriptValueObject(file);
		} catch (IOException e) {
			logger.error("", e);
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]无法读取.", e);
		}

		if (StringUtils.startsWith(file.getName().toLowerCase(), "execute_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "create_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "update_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "delete_")) {// 事务
			return ScriptExecuteService.getInstance().executeScript(scriptVo);
		} else if (StringUtils.startsWith(file.getName().toLowerCase(), "get_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "find_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "query_")) {// 只读
			return ScriptExecuteService.getInstance().getScript(scriptVo);
		} else {// 常规
			return ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo);
		}
	}

}
