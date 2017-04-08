package com.riversoft.core.cache;


import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis连接池
 * 
 * @author Chris
 *
 */

public class RedisClient {

	/**
	 * 链接池
	 */
	private static JedisPool jedisPool = null;

	private static Logger logger = LoggerFactory.getLogger(RedisClient.class);

	/**
	 * 链接池初始化
	 */
	static {

		try {
			// 池基本配置
			JedisPoolConfig config = new JedisPoolConfig();
			// 预设置参数

			// 最大链接数
			int maxTotal = NumberUtils.toInt(Config.get("redis.pool.maxTotal"), 0);
			if (maxTotal > 0) {
				logger.info("设置最大连接数为{}", Config.get("redis.pool.maxTotal"));
				config.setMaxTotal(maxTotal);
			}

			// 最大空闲资源数
			int maxIdle = NumberUtils.toInt(Config.get("redis.pool.maxIdle"), 0);
			if (maxIdle > 0) {
				logger.info("设置最大空闲资源数为{}", Config.get("redis.pool.maxIdle"));
				config.setMaxIdle(maxIdle);
			}

			// 最小空闲资源数
			int minIdle = NumberUtils.toInt(Config.get("redis.pool.minIdle"), 0);
			if (minIdle > 0) {
				logger.info("设置最小空闲资源数为{}", Config.get("redis.pool.minIdle"));
				config.setMinIdle(minIdle);
			}

			// 最大等待时间
			int maxWaitMillis = NumberUtils.toInt(Config.get("redis.pool.maxWaitMillis"), 0);
			if (maxWaitMillis > 0) {
				logger.info("设置最大等待时间为{}", Config.get("redis.pool.maxWaitMillis"));
				config.setMaxWaitMillis(maxWaitMillis);
			}

			// 是否提前进行validate操作(默认否)
			Boolean testOnBorrow = Boolean.valueOf(Config.get("redis.pool.testOnBorrow", "false"));
			if (testOnBorrow) {
				logger.info("设置是否提前进行validate操作为{}", Config.get("redis.pool.testOnBorrow", "false"));
				config.setTestOnBorrow(testOnBorrow);
			}

			// 当调用return Object方法时，是否进行有效性检查(默认否)
			Boolean testOnReturn = Boolean.valueOf(Config.get("redis.pool.testOnReturn", "false"));
			if (testOnReturn) {
				logger.info("当调用return Object方法时，是否进行有效性检查{}", Config.get("redis.pool.testOnReturn", "false"));
				config.setTestOnReturn(testOnReturn);
			}

			if (Config.get("redis.ip").isEmpty()) {
				logger.warn("没有设置redis服务器IP,无法连接");
			} else {

				String ip = (String) Config.get("redis.ip");
				int port = Integer.parseInt(Config.get("redis.port", "6379"));
				logger.info("设置ip为{},port为{}", ip, port);
				// 构建链接池
				jedisPool = new JedisPool(config, ip, port);
				logger.info("redis连接成功");
			}

		} catch (Exception e) {
			logger.error("初始化redis失败", e);
		}
	}

	public synchronized static Jedis getJedis() {
		try {
			if (jedisPool != null) {
				Jedis resource = jedisPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	public static void releaseResource(final Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
}
