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
import com.riversoft.weixin.common.util.JsonMapper;

import jdk.nashorn.api.scripting.JSObject;

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
		String script = "function a(){return [1,2,3,4];};a();";
		Map<String, Object> scope = new HashMap<>();
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);
		JSObject obj = (JSObject) ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.JAVASCRIPT,
				script, context);
		logger.debug("result:{},class:{},size:{}", obj.toString(), obj.getClass(), obj.values());
		logger.debug("to json:{}", JsonMapper.defaultMapper().toJson(obj.values()));
		int[] result = { 1, 2, 3, 4 };
		logger.debug("to json:{}", JsonMapper.defaultMapper().toJson(result));
		// Assert.assertArrayEquals((int[]) obj, result);
	}
}
