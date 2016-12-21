/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.nami.function;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.Formatter;

/**
 * 流水号辅助类
 * 
 * @author woden
 * 
 */
@ScriptSupport("seq")
public class SequenceHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SequenceHelper.class);

	/**
	 * 获取uuid
	 * 
	 * @return
	 */
	public static String uuid() {
		return IDGenerator.uuid();
	}

	/**
	 * 获取下一个唯一字符
	 * 
	 * @return
	 */
	public static String next() {
		return IDGenerator.next();
	}

	/**
	 * 模式创建序列号.目前支持的模式:<br>
	 * 时间 {now}:yyyyMMdd<br>
	 * 序号{seq:u/l}:tableName,column,size<br>
	 * 表单传值{req}:NAME<br>
	 * 
	 * @param code
	 * @param objs
	 *            命令对应入参
	 * @return
	 */
	public static String pattern(String code, Object... objs) {
		Pattern pattern = Pattern.compile("\\{([a-z\\:]+)\\}");
		Matcher matcher = pattern.matcher(code);
		StringBuffer buff = new StringBuffer();
		int begin = 0;

		Queue<Object> queue = new LinkedList<>(Arrays.asList(objs));

		while (matcher.find()) {
			String str = matcher.group(0);
			String cmd = matcher.group(1);
			int index = code.indexOf(str, begin);
			buff.append(code.subSequence(begin, index));
			begin = index + str.length();
			int caseType = 0;// 1:大写;2:小写
			if (cmd.indexOf(":") > 0) {
				if ("u".equalsIgnoreCase(cmd.substring(cmd.indexOf(":") + 1))) {// 大写
					caseType = 1;
				} else if ("l".equalsIgnoreCase(cmd.substring(cmd.indexOf(":") + 1))) {// 小写
					caseType = 2;
				}
				cmd = cmd.substring(0, cmd.indexOf(":"));
			}

			switch (cmd) {
			case "now":// 时间模式
				String p = (String) queue.poll();// 获取参数
				if (p == null) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "无法为{now}获取参数.");
				}
				buff.append(Formatter.formatDatetime(new Date(), p));
				break;
			case "req":// 表单入参
				String name = (String) queue.poll();// 获取参数
				if (name == null) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "无法为{req}获取参数.");
				}
				RequestContext request = RequestContext.getCurrent();
				String val = request.getString(name);
				if (val == null) {
					val = "";
				}
				if (caseType == 1) {
					val = val.toUpperCase();
				} else if (caseType == 2) {
					val = val.toLowerCase();
				}

				buff.append(val);
				break;
			case "seq":// 自动序列号
				String tableName = (String) queue.poll();
				String columnName = (String) queue.poll();
				Integer size = (Integer) queue.poll();
				if (tableName == null || columnName == null || size == null || size < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "无法为{seq}获取足够数量的参数.");
				}

				Map<String, Object> o = JdbcService.getInstance().findSQL("select max(" + columnName
						+ ") maxColumn from " + tableName + " where " + columnName + " like ?", buff.toString() + "%");
				int seq = 0;
				if (o != null && o.get("maxColumn") != null) {
					logger.debug("找到max的字段值:" + o.get("maxColumn"));
					seq = Integer
							.parseInt(o.get("maxColumn").toString().substring(buff.length(), buff.length() + size));
				}
				logger.debug("自动sequence为:" + seq);
				buff.append(String.format("%0" + size + "d", seq + 1));
				break;
			default:
				buff.append(str);
				break;
			}
		}

		if (code.length() > begin) {
			buff.append(code.substring(begin));
		}
		return buff.toString();
	}

	private static final String WORD_STR = "ABCDEFGHJKLMNPQRSTUVWXYZ";// 随机字母(去掉I,O)
	private static final String INT_STR = "0123456789";// 随机数字

	/**
	 * 生成随机数字
	 * 
	 * @param size
	 * @return
	 */
	public static String randomNumber(int size) {
		Random random = new Random();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buff.append(String.valueOf(INT_STR.charAt(random.nextInt(INT_STR.length()))));
		}
		return buff.toString();
	}

	/**
	 * 生成随机字符
	 * 
	 * @param size
	 * @return
	 */
	public static String randomWord(int size) {
		Random random = new Random();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buff.append(String.valueOf(WORD_STR.charAt(random.nextInt(WORD_STR.length()))));
		}
		return buff.toString();
	}

	/**
	 * 生成随机字符+数字混合
	 * 
	 * @param size
	 * @return
	 */
	public static String random(int size) {
		Random random = new Random();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < size; i++) {
			if (i == 0 || random.nextBoolean()) {// 首位必须字符
				buff.append(String.valueOf(WORD_STR.charAt(random.nextInt(WORD_STR.length()))));
			} else {
				buff.append(String.valueOf(INT_STR.charAt(random.nextInt(INT_STR.length()))));
			}
		}
		return buff.toString();
	}

}
