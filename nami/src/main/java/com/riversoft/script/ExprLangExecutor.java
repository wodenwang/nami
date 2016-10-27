/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.script;
 
/**
 * @author Borball
 *
 */
public interface ExprLangExecutor {

    Object evaluateEL(String el, ScriptExecutionContext context);

}
