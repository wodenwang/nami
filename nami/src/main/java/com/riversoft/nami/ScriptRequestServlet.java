/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;
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

	// 上传配置
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 10; // 10MB

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
	 * 初始化request和session的localthread
	 * 
	 * @param request
	 */
	private void initContext(HttpServletRequest request) throws ServletException, IOException {
		// 设置threadlocal
		// 设置request
		{
			Map<String, Object> params = new HashMap<>();
			String contentType = request.getContentType(); // 获取Content-Type

			// 判断post请求头部
			if ((contentType != null) && (contentType.toLowerCase().startsWith("multipart/"))) {
				try {
					request.setCharacterEncoding("UTF-8");
					FileItem file = null;
					InputStream in = null;
					ByteArrayOutputStream swapStream = null;

					// 创建一个DiskFileItemFactory工厂
					DiskFileItemFactory factory = new DiskFileItemFactory();
					// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中,默认10M
					factory.setSizeThreshold(MEMORY_THRESHOLD);

					// 构造临时路径来存储上传的文件
					// 这个路径相对当前应用的目录
					String uploadPath = request.getSession().getServletContext().getRealPath("/tempFile");

					// logger.info("临时文件目录{}",uploadPath);

					// 如果目录不存在则创建
					File uploadDir = new File(uploadPath);
					if (!uploadDir.exists()) {
						uploadDir.mkdir();
					}

					// 创建一个文件上传解析器
					ServletFileUpload upload = new ServletFileUpload(factory);

					// 解决上传文件名的中文乱码
					upload.setHeaderEncoding("UTF-8");
					// 得到 FileItem 的集合 items
					List<FileItem> items = upload.parseRequest(request);
					logger.info("items:{}", items.size());

					// 遍历 items:
					for (FileItem item : items) {
						String name = item.getFieldName();
						logger.info("fieldName:{}", name);
						// 若是一个一般的表单域, 无须处理
						if (item.isFormField()) {
							String value = item.getString("utf-8");
							params.put(name, value);
						} else {
							file = item;
							String fileName = file.getName();
							swapStream = new ByteArrayOutputStream();

							in = file.getInputStream();
							byte[] buff = new byte[1024];
							int rc = 0;
							while ((rc = in.read(buff)) > 0) {
								swapStream.write(buff, 0, rc);
							}
							final byte[] bytes = swapStream.toByteArray();
							if (swapStream != null) {
								swapStream.close();
							}
							if (in != null) {
								in.close();
							}
							UploadFile uploadFile = new UploadFile();
							uploadFile.setName(fileName);//文件名
							uploadFile.setValue(bytes);//文件二进制流
							params.put(name, uploadFile);
						}
					}
				} catch (FileUploadException e) {
					logger.warn("上传文件错误", e);
				}
			} else {
				Enumeration<String> names = request.getParameterNames();

				while (names.hasMoreElements()) {
					String name = names.nextElement();
					// logger.debug("当前表单数据[" + name + "]以设置入threadlocal.");
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
