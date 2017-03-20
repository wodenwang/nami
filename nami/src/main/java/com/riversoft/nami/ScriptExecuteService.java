/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami;

import java.util.Map;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExpressionAndScriptExecutors.ScriptValueObject;

/**
 * 脚本事务执行器
 * 
 * @author woden
 *
 */
class ScriptExecuteService {

	public static ScriptExecuteService getInstance() {
		return BeanFactory.getInstance().getBean(ScriptExecuteService.class);
	}

	/**
	 * 执行事务
	 * 
	 * @param scriptVo
	 * @return
	 */
	public Object executeScript(ScriptValueObject scriptVo) {
		return executeScript(scriptVo, null);
	}

	/**
	 * 执行事务
	 * 
	 * @param scriptVo
	 * @param context
	 * @return
	 */
	public Object executeScript(ScriptValueObject scriptVo, Map<String, Object> context) {
		return ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo, context);
	}

	/**
	 * 强制readonly
	 * 
	 * @param scriptVo
	 * @return
	 */
	public Object getScript(ScriptValueObject scriptVo) {
		return getScript(scriptVo, null);
	}

	/**
	 * 强制readonly
	 * 
	 * @param scriptVo
	 * @param context
	 * @return
	 */
	public Object getScript(ScriptValueObject scriptVo, Map<String, Object> context) {
		return ExpressionAndScriptExecutors.getInstance().evaluateScript(scriptVo, context);
	}
}
