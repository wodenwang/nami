/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.script;

import java.util.Map;
 
/**
 * @author Borball
 *
 */
public interface ScriptExecutionContext {
    
    Map<String, Object> getVariableContext();

}
