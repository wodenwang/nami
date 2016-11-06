/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.Formatter;

/**
 * @author Borball
 * 
 */
public class ScriptExecutorsTest {

	@BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:applicationContext-scripts.xml");
	}

	@Test
	public void testBasicEl() {
		String expression = "${((G1 + G2 + G3) * 0.1) + G4}";

		Map<String, Object> scope = new HashMap<>();
		scope.put("G1", 100f);
		scope.put("G2", 200f);
		scope.put("G3", 300f);
		scope.put("G4", 1000f);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals(1060.0f, Float.valueOf(ExpressionAndScriptExecutors.getInstance().evaluateEL(expression, context).toString()), 0);
	}

	@Test
	public void testFormatter() {
		String expression = "fmt:formatDate(date)";

		Date date = new Date();
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals(Formatter.formatDate(date),
				ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.JSR223, expression, context));

	}

	@Test
	public void testFormatterWithParameters() {
		String expression = "${fmt:formatDatetime(date, 'yyyy-MM-dd HH:mm:ss')}";

		Date date = new Date();
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals(Formatter.formatDatetime(date, "yyyy-MM-dd HH:mm:ss"),
				ExpressionAndScriptExecutors.getInstance().evaluateEL(expression, context));

	}

	@Test
	public void testFormatChinesePrice() {
		String expression = "${fmt:formatChinesePrice(price)}";

		Map<String, Object> scope = new HashMap<>();
		scope.put("price", new BigDecimal(100003.25));

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals("壹拾万零叁元贰角伍分", ExpressionAndScriptExecutors.getInstance().evaluateEL(expression, context));

	}

	@Test
	public void testStringAppend() {
		String expression = "${str1 + str2 + str3}";

		Map<String, Object> scope = new HashMap<>();
		scope.put("str1", "String 1;");
		scope.put("str2", "String 2;");
		scope.put("str3", "String 3;");

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals("String 1;String 2;String 3;", ExpressionAndScriptExecutors.getInstance().evaluateEL(expression, context));

	}

	@Test
	public void testBasicScriptJSR223() {
		String code = "while (x < 10) x = x + 1;";
		Map<String, Object> scope = new HashMap<>();
		scope.put("x", 1);

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertEquals(10, (Integer) ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.JSR223, code, context), 0);
	}

	@Test
	public void testElwithEL() {
		String code = "${true}";
		Map<String, Object> scope = new HashMap<>();

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertTrue((Boolean) ExpressionAndScriptExecutors.getInstance().evaluateEL(code, context));
	}

	@Test
	public void testBasicScriptGroovy() {
		Date date = new Date();

		String script = "now = fmt.formatDate(date)\n" + "now == dateString";
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);
		scope.put("dateString", Formatter.formatDate(date));
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertTrue((Boolean) ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.GROOVY, script, context));
	}

	@Test
	public void testBasicReturnGroovy() {
		String script = "return true;";
		Map<String, Object> scope = new HashMap<>();
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		Assert.assertTrue((Boolean) ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.GROOVY, script, context));
	}

	@Test
	public void testBasicScriptGroovyWithNoMethodExisting() {
		Date date = new Date();

		String script = "now = fmt.formatDate1(date)\n" + "now == dateString";
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		try {
			ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.GROOVY, script, context);
			Assert.fail();
		} catch (SystemRuntimeException ex) {
			Assert.assertTrue(true);
		}
	}

	@Test(expected = SystemRuntimeException.class)
	public void testBasicScriptGroovyWithCompileError() {
		String script = "abc'test[].error";
		Map<String, Object> scope = new HashMap<>();

		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.GROOVY, script, context);
		Assert.fail();
	}

	@Test
	public void testAnnotation() {
		Date date = new Date();
		String script = "now=test.format(date)";
		Map<String, Object> scope = new HashMap<>();
		scope.put("date", date);
		BasicScriptExecutionContext context = new BasicScriptExecutionContext(scope);

		long now = (long) ExpressionAndScriptExecutors.getInstance().evaluateScript(ScriptType.GROOVY, script, context);
		Assert.assertEquals(date.getTime(), now);
	}

}
