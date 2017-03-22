/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.ScriptRuntimeException;
import com.riversoft.core.exception.SystemRuntimeException;

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
			return convertJsObject(javaScriptExecutor.evaluateScript(script, context));
		default:
			return jsr223Executor.evaluateScript(script, context);
		}
	}

	private static Object convertJsObject(final Object obj) {
		if (obj instanceof Bindings) {
			try {
				final Class<?> cls = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
				if (cls.isAssignableFrom(obj.getClass())) {
					final Method isArray = cls.getMethod("isArray");
					final Object result = isArray.invoke(obj);
					if (result != null && result.equals(true)) {
						final Method values = cls.getMethod("values");
						final Object vals = values.invoke(obj);
						if (vals instanceof Collection<?>) {
							final Collection<?> coll = (Collection<?>) vals;
							List<Object> list = new ArrayList<>();
							coll.forEach(o -> list.add(convertJsObject(o)));
							return list;
						}
					}
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new ScriptRuntimeException("解析javascript出错", e);
			}
		}

		return obj;
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
			// TODO 脚本缓存改造
			script = FileUtils.readFileToString(scriptFile, "UTF-8");
		}
	}
}
