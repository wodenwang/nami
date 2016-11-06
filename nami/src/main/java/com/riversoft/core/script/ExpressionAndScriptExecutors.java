/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Borball
 *
 */
public class ExpressionAndScriptExecutors {

	private ExprLangExecutor elExecutor;
	private ScriptExecutor groovyExecutor;
	private ScriptExecutor javaScriptExecutor;
	private ScriptExecutor jsr223Executor;

	public static ExpressionAndScriptExecutors getInstance() {
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance()
				.getBean("expressionAndScriptExecutors");
		return executors;
	}

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

	public Object evaluateScript(ScriptType scriptType, String script, ScriptExecutionContext context) {
		switch (scriptType) {
			case JSR223:
				return jsr223Executor.evaluateScript(script, context);
			case GROOVY:
				return groovyExecutor.evaluateScript(script, context);
			case JAVASCRIPT:
				return javaScriptExecutor.evaluateScript(script, context);
			default:
				return jsr223Executor.evaluateScript(script, context);
		}

	}

	public void setElExecutor(ExprLangExecutor elExecutor) {
		this.elExecutor = elExecutor;
	}

	public void setGroovyExecutor(ScriptExecutor groovyExecutor) {
		this.groovyExecutor = groovyExecutor;
	}

	public void setJsr223Executor(ScriptExecutor jsr223Executor) {
		this.jsr223Executor = jsr223Executor;
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

		public ScriptValueObject(File scriptFile) throws IOException {
			if (StringUtils.endsWith(scriptFile.getName().toLowerCase(), ".js")) {
				type = ScriptType.JAVASCRIPT;
			} else if (StringUtils.endsWith(scriptFile.getName().toLowerCase(), ".groovy")) {
				type = ScriptType.GROOVY;
			} else if (StringUtils.endsWith(scriptFile.getName().toLowerCase(), ".el")) {
				type = ScriptType.JSR223;
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "请求协议不合法");
			}
			script = FileUtils.readFileToString(scriptFile);
		}
	}
}
