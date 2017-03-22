/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.util.JsonMapper;

/**
 * @author woden
 *
 */
public class JavascriptScriptTest {

	static Logger logger = LoggerFactory.getLogger(JavascriptScriptTest.class);

	@BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:applicationContext-scripts.xml");
	}

	@Test
	public void testArray() {
		String script = "[{a:1},{b:2},2,['ad',2,{d:1}]]";
		Map<String, Object> scope = new HashMap<>();
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		Object obj = ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.JAVASCRIPT, script, context);
		logger.debug("result:{},class:{}", obj, obj.getClass());
		logger.debug("to json:{}", JsonMapper.defaultMapper().toJson(obj));

		// Assert.assertArrayEquals((int[]) obj, result);
	}

}
