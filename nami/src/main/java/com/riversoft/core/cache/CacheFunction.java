package com.riversoft.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.script.annotation.ScriptSupport;

import redis.clients.jedis.Jedis;

/**
 * redis 调用库
 * 
 * @author Chris
 *
 */

@ScriptSupport("cache")
public class CacheFunction {

	private static Logger logger = LoggerFactory.getLogger(CacheFunction.class);
	
	// 序列化
	private static byte[] serialize(Object obj) {
		ObjectOutputStream obi = null;
		ByteArrayOutputStream bai = null;
		try {
			bai = new ByteArrayOutputStream();
			obi = new ObjectOutputStream(bai);
			obi.writeObject(obj);
			byte[] byt = bai.toByteArray();
			return byt;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 反序列化
	private static Object unserizlize(byte[] byt) {
		ObjectInputStream oii = null;
		ByteArrayInputStream bis = null;
		bis = new ByteArrayInputStream(byt);
		try {
			oii = new ObjectInputStream(bis);
			Object obj = oii.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// set对象
	public void set(String key, Object value) {
		logger.info("设置key为{}的object",key);
		Jedis jedis = RedisClient.getJedis();
		jedis.set(key.getBytes(), serialize(value));
		RedisClient.releaseResource(jedis);
	}

	// get对象
	public Object get(String key) {
		logger.info("获取key为{}的object",key);
		Jedis jedis = RedisClient.getJedis();
		Object value = unserizlize(jedis.get(key.getBytes()));
		RedisClient.releaseResource(jedis);
		return value;
	}
}
