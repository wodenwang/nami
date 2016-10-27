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
public class ExpressionAndScriptExecutors {

    private ExprLangExecutor elExecutor;
    private ScriptExecutor groovyExecutor;
    private ScriptExecutor javaScriptExecutor;

    public Object evaluateEL(String el, ScriptExecutionContext context) {
        return elExecutor.evaluateEL(el, context);
    }

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
}
