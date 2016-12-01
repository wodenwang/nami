/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.Date;

import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * @author Borball
 * 
 */
@ScriptSupport(value = "test")
public class TestExprLang {

    public static long format(Date date) {
        return date.getTime();
    }

}
