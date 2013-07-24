package my.library.cache.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import my.library.cache.AbstractCache;
import my.library.cache.CacheConstant;
import my.library.cache.ICache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * List properties: maxActive(def: 200), testOnReturn(def: true),
 * expireAfterWrite(persit if not), host(required), port(required),
 * name(required)
 * 
 * @author ngoctdn
 * 
 */
public class RedisCache extends AbstractCache implements ICache {

	private int maxActive;
	private boolean testOnReturn;
	private String host;
	private int port;
	private Jedis cache;
	private String nameSpace;
	private JedisPool jedisPool;

	public RedisCache() {
		super();
	}

	public RedisCache(String name) {
		super(name);
	}

	public RedisCache(String name, String host, int port) {
		super(name);
		this.host = host;
		this.port = port;
	}

	@Override
	public void init() {
		super.init();
		nameSpace = getName() + ":";
		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		// Maximum active connections to Redis instance
		int maxActive = this.maxActive > 0 ? this.maxActive
				: CacheConstant.DEFAULT_REDIS_MAX_ACTIVE;
		setMaxActive(maxActive);
		jedisConfig.setMaxActive(maxActive);

		// Tests whether connection is dead when returning a connection to the
		// pool
		jedisConfig.setTestOnReturn(testOnReturn);

		jedisPool = new JedisPool(jedisConfig, host, port);
		cache = jedisPool.getResource();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (jedisPool != null) {
			jedisPool.returnResource(cache);
		}
	}

	@Override
	public long getSize() {
		return cache.dbSize();
	}

	@Override
	public void set(String key, Object value) {
		int expireAfterWrite = (int) getExpireAfterWrite();
		cache.set(nameSpace + key, String.valueOf(value));
		if (expireAfterWrite > 0) {
			cache.expire(nameSpace + key, expireAfterWrite);
		}
	}

	@Override
	public void delete(String key) {
		cache.del(nameSpace + key);
	}

	@Override
	public void clear() {
		Set<String> listKey = cache.keys(nameSpace + "*");
		cache.del(listKey.toArray(new String[0]));
	}

	@Override
	public boolean exists(String key) {
		return cache.exists(nameSpace + key);
	}

	@Override
	public List<String> searchKey(String pattern) {
		Set<String> listKeys = cache.keys(pattern);
		return new LinkedList<String>(listKeys);
	}

	@Override
	protected Object internalGet(String key) {
		return cache.get(nameSpace + key);
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
