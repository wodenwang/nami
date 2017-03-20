/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExpressionAndScriptExecutors.ScriptValueObject;
import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * NAMI平台函数
 * 
 * @author woden
 */
@ScriptSupport("nami")
public class NamiFunction {

	static Logger logger = LoggerFactory.getLogger(NamiFunction.class);

	/**
	 * 函数调用
	 * 
	 * @param path
	 * @param args
	 * @return
	 */
	public Object invoke(String path, Object... args) {
		File file = new File(Platform.getFunctionPath(), path);
		ScriptValueObject scriptVo;
		try {
			scriptVo = new ScriptValueObject(file);
		} catch (IOException e) {
			logger.error("找不到函数", e);
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "脚本[" + file.getAbsolutePath() + "]无法读取.", e);
		}
		Map<String, Object> context = new HashMap<>();
		context.put("args", args);

		if (StringUtils.startsWith(file.getName().toLowerCase(), "execute_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "save_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "update_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "delete_")) {// 事务
			return ScriptExecuteService.getInstance().executeScript(scriptVo, context);
		} else if (StringUtils.startsWith(file.getName().toLowerCase(), "get_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "find_")
				|| StringUtils.startsWith(file.getName().toLowerCase(), "query_")) {// 只读
			return ScriptExecuteService.getInstance().getScript(scriptVo, context);
		} else {// 常规
			return ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo, context);
		}
	}

	/**
	 * 抛出异常(直接中断)
	 * 
	 * @param msg
	 */
	public void error(Object msg) {
		if (msg == null) {
			return;
		}

		logger.error(msg.toString());
		throw new SystemRuntimeException(ExceptionType.SCRIPT, msg.toString());
	}

	/**
	 * 获取配置参数
	 * 
	 * @param key
	 * @return
	 */
	public String conf(String key) {
		return Config.get(key);
	}
}
