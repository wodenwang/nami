/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.script.impl;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import com.riversoft.exception.ScriptRuntimeException;
import com.riversoft.exception.SystemRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.script.ExprlangAnnotationScanner;
import com.riversoft.script.ScriptExecutionContext;
import com.riversoft.script.ScriptExecutor;

/**
 * @author Borball
 * 
 */
public class GroovyScriptEngine implements ScriptExecutor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(GroovyScriptEngine.class);

	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private ScriptEngine engine = scriptEngineManager.getEngineByName("groovy");

	public GroovyScriptEngine(ExprlangAnnotationScanner exprlangAnnotationScanner) {
		Map<String, Object> context = exprlangAnnotationScanner.getElSupports();

		Bindings bindings = new SimpleBindings();
		for (String funcName : context.keySet()) {
			bindings.put(funcName, context.get(funcName));
		}

		engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
	}

	@Override
	public Object evaluateScript(String script, ScriptExecutionContext context) {
		Bindings bindings = buildBindings(context);

		try {
			if (bindings == null) {
				return engine.eval((new String(script)).intern());
			} else {
				return engine.eval(new String(script).intern(), bindings);
			}
		} catch (Throwable ex) {
			logger.error("执行脚本出错:" + script, ex);
			if (ExceptionUtils.getRootCause(ex) instanceof SystemRuntimeException) {
				throw (SystemRuntimeException) ExceptionUtils.getRootCause(ex);
			}
			throw new ScriptRuntimeException(script, ex);
		}
	}

	private Bindings buildBindings(ScriptExecutionContext context) {
		if ((context == null) || (context.getVariableContext() == null) || (context.getVariableContext().isEmpty())) {
			return null;
		}
		Bindings bindings = new SimpleBindings();
		for (String varName : context.getVariableContext().keySet()) {
			bindings.put(varName, context.getVariableContext().get(varName));
		}
		return bindings;
	}

}
