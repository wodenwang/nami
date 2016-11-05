/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.context.VariableContext;

/**
 * @author Borball
 * 
 */
public class BasicScriptExecutionContext implements ScriptExecutionContext {

	private Map<String, Object> variables;

	public BasicScriptExecutionContext(Map<String, Object> extra) {
		variables = new HashMap<String, Object>();
		variables.put(ContextKeys.REQUEST.key, RequestContext.getCurrent());
		variables.put(ContextKeys.SESSION.key, SessionContext.getCurrent());
		variables.put(ContextKeys.VARIABLE.key, VariableContext.getCurrent());
		variables.put(ContextKeys.NOW.key, new Date());
		if (extra != null) {
			variables.putAll(extra);
		}

	}

	public BasicScriptExecutionContext() {
		this(new HashMap<String, Object>());
	}

	@Override
	public Map<String, Object> getVariableContext() {
		return variables;
	}

}
