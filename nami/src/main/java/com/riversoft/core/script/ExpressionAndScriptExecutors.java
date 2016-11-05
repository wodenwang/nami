/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.Map;

import com.riversoft.core.BeanFactory;

/**
 * @author Borball
 * 
 */
public class ExpressionAndScriptExecutors {

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static ExpressionAndScriptExecutors getInstance() {
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance()
				.getBean("expressionAndScriptExecutors");
		return executors;
	}

	private ExprLangExecutor elExecutor;
	private ScriptExecutor groovyExecutor;
	private ScriptExecutor javaScriptExecutor;

	public Object evaluateEL(String el, ScriptExecutionContext context) {
		return elExecutor.evaluateEL(el, context);
	}

	/**
	 * 执行脚本
	 * 
	 * @param scriptValueObject
	 * @return
	 */
	public Object evaluateScript(ScriptValueObject scriptValueObject) {
		return evaluateScript(scriptValueObject, null);
	}

	/**
	 * 执行脚本
	 * 
	 * @param scriptValueObject
	 * @param context
	 * @return
	 */
	public Object evaluateScript(ScriptValueObject scriptValueObject, Map<String, Object> context) {
		return evaluateScript(scriptValueObject.type, scriptValueObject.script,
				new BasicScriptExecutionContext(context));
	}

	/**
	 * 执行脚本
	 * 
	 * @param scriptType
	 * @param script
	 * @param context
	 * @return
	 */
	public Object evaluateScript(ScriptType scriptType, String script, ScriptExecutionContext context) {
		switch (scriptType) {
		case EL:
			return elExecutor.evaluateEL(script, context);
		case GROOVY:
			return groovyExecutor.evaluateScript(script, context);
		case JAVASCRIPT:
			return javaScriptExecutor.evaluateScript(script, context);
		default:
			return groovyExecutor.evaluateScript(script, context);
		}

	}

	public void setElExecutor(ExprLangExecutor elExecutor) {
		this.elExecutor = elExecutor;
	}

	public void setGroovyExecutor(ScriptExecutor groovyExecutor) {
		this.groovyExecutor = groovyExecutor;
	}

	public void setJavaScriptExecutor(ScriptExecutor javaScriptExecutor) {
		this.javaScriptExecutor = javaScriptExecutor;
	}

	/**
	 * 解析脚本内容
	 * 
	 * @author woden
	 *
	 */
	public static class ScriptValueObject {
		ScriptType type;
		String script;

		public ScriptValueObject(ScriptType type, String script) {
			this.type = type;
			this.script = script;
		}
	}
}
