/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script.impl;

import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.UnifiedJEXL;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ScriptRuntimeException;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExprLangExecutor;
import com.riversoft.core.script.ExprlangAnnotationScanner;
import com.riversoft.core.script.ScriptExecutionContext;
import com.riversoft.core.script.ScriptExecutor;

/**
 * @author Borball
 * 
 */
public class JexlEngine implements ScriptExecutor, ExprLangExecutor {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JexlEngine.class);

	private org.apache.commons.jexl2.JexlEngine engine;
	private UnifiedJEXL unifiedJEXL;
	@SuppressWarnings("unused")
	private ExprlangAnnotationScanner exprlangAnnotationScanner;

	public JexlEngine() {
		engine = new org.apache.commons.jexl2.JexlEngine();
		unifiedJEXL = new UnifiedJEXL(engine);
	}

	/**
	 * @param exprlangAnnotationScanner
	 *            the exprlangAnnotationScanner to set
	 */
	public void setExprlangAnnotationScanner(ExprlangAnnotationScanner exprlangAnnotationScanner) {
		this.exprlangAnnotationScanner = exprlangAnnotationScanner;
		engine.setFunctions(exprlangAnnotationScanner.getElSupports());
	}

	@Override
	public Object evaluateScript(String script, ScriptExecutionContext context) {
		try {
			return engine.createScript(script).execute(new MapContext(context.getVariableContext()));
		} catch (Throwable ex) {
			logger.error("执行脚本出错:" + script, ex);
			if (ExceptionUtils.getRootCause(ex) instanceof SystemRuntimeException) {
				throw (SystemRuntimeException) ExceptionUtils.getRootCause(ex);
			}
			throw new ScriptRuntimeException(script, ex);
		}
	}

	@Override
	public Object evaluateEL(String el, ScriptExecutionContext context) {
		try {
			UnifiedJEXL.Expression expression = unifiedJEXL.parse(el);
			return expression.evaluate(new MapContext(context.getVariableContext()));
		} catch (Exception ex) {
			logger.error("执行脚本出错:" + el, ex);
			if (ExceptionUtils.getRootCause(ex) instanceof SystemRuntimeException) {
				throw (SystemRuntimeException) ExceptionUtils.getRootCause(ex);
			}
			throw new ScriptRuntimeException(el, ex);
		}
	}

	/**
	 * @param cacheSize
	 *            the cacheSize to set
	 */
	public void setCacheSize(int cacheSize) {
		engine.setCache(cacheSize);
	}

	/**
	 * @param silent
	 *            the silent to set
	 */
	public void setSilent(boolean silent) {
		engine.setSilent(silent);
	}

	/**
	 * @param lenient
	 *            the lenient to set
	 */
	public void setLenient(boolean lenient) {
		engine.setLenient(lenient);
	}

	public void setStrict(boolean strict) {
		engine.setStrict(strict);
	}
}
