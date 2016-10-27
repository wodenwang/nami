/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

/**
 * @author Borball
 * 
 */
public interface ScriptExecutor {

    Object evaluateScript(String script, ScriptExecutionContext context);

}
