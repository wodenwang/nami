/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.riversoft.core.BeanFactory;

/**
 * 通用数据库服务.<br>
 * 封装通用jdbc操作。
 * 
 * @author Woden
 * 
 */
public class JdbcService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JdbcService.class);

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static JdbcService getInstance() {
		return BeanFactory.getInstance().getSingleBean(JdbcService.class);
	}

	/**
	 * spring jdbc模板<br>
	 */
	protected JdbcTemplate jdbcTemplate;

	/**
	 * spring jdbc模板
	 */
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * spring auto setter
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * spring auto setter
	 * 
	 * @param namedParameterJdbcTemplate
	 */
	public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	/**
	 * 查询列表
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<Map<String, Object>> querySQL(String sql, Object... args) {
		try {
			return jdbcTemplate.query(sql, args, getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	/**
	 * 查询列表
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> querySQL(String sql, Map<String, ?> params) {
		try {
			return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}

	/**
	 * 查询值
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String, Object> findSQL(String sql, Object... args) {
		try {
			return (Map) jdbcTemplate.queryForObject(sql, args, getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * 查询值
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, Object> findSQL(String sql, Map<String, ?> params) {
		try {
			return (Map) namedParameterJdbcTemplate.queryForObject(sql, params, getRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	};

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 */
	public void executeSQL(String sql, Object... args) {
		jdbcTemplate.update(sql, args);
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @param params
	 */
	public void executeSQL(String sql, Map<String, ?> params) {
		namedParameterJdbcTemplate.update(sql, params);
	}

	/**
	 * 执行sql语句并返回自动增长主键
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Long saveSQL(final String sql, Map<String, ?> params, String autoKey) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder, new String[] { autoKey });
		return keyHolder.getKey().longValue();
	}

	/**
	 * 执行sql语句并返回自动增长主键
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Long saveSQL(final String sql, final Object... args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				if (args != null) {
					int i = 1;
					for (Object obj : args) {
						ps.setObject(i, obj);
						i++;
					}
				}
				return ps;
			}
		}, keyHolder);
		logger.debug("自动流水号:" + keyHolder.getKey().intValue());
		return keyHolder.getKey().longValue();
	}

	private RowMapper<Map<String, Object>> getRowMapper() {
		return new ColumnMapRowMapper();
	}

}
