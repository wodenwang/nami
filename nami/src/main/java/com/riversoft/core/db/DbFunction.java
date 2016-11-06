/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.util.List;
import java.util.Map;

import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * 数据库操作类
 * 
 * @author woden
 * 
 */
@ScriptSupport("db")
public class DbFunction {

	/**
	 * 根据sql查询唯一值
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String, Object> find(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.findSQL(sql, args);
	}

	/**
	 * 根据sql查询列表
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<?> query(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.querySQL(sql, args);
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @param params
	 */
	public void exec(String sql, Map<String, Object> params) {
		JdbcService service = JdbcService.getInstance();
		service.executeSQL(sql, params);
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @param args
	 */
	public void exec(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		service.executeSQL(sql, args);
	}

	/**
	 * 执行新增语句并返回自动递增ID
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Long save(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.saveSQL(sql, args);
	}
}
