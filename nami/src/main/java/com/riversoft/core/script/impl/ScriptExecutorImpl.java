package com.riversoft.core.script.impl;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ScriptRuntimeException;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExprlangAnnotationScanner;
import com.riversoft.core.script.ScriptExecutionContext;
import com.riversoft.core.script.ScriptExecutor;

/**
 * @borball on 10/27/2016.
 */
public class ScriptExecutorImpl implements ScriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(ScriptExecutorImpl.class);

	private ScriptEngine engine = null;

	public ScriptExecutorImpl(ExprlangAnnotationScanner exprlangAnnotationScanner, String engineName) {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		engine = scriptEngineManager.getEngineByName(engineName);

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
			Throwable rootEx = ExceptionUtils.getRootCause(ex);
			if (rootEx == null) {
				rootEx = ex;
			}
			if (rootEx instanceof SystemRuntimeException) {
				throw (SystemRuntimeException) rootEx;
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
