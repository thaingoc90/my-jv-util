package my.library.cache.impl;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import my.library.cache.AbstractCache;
import my.library.cache.CacheConstant;
import my.library.cache.ICache;
import net.spy.memcached.CacheMap;
import net.spy.memcached.MemcachedClient;

/**
 * lib: spymemcached-2.9.1-sources.jar
 * 
 * @author ngoctdn
 * 
 */
public class MemcacheCache extends AbstractCache implements ICache {

	private String host;
	private int port;
	private CacheMap cache;

	public MemcacheCache() {
		super();
	}

	public MemcacheCache(String name) {
		super(name);
	}

	public MemcacheCache(String name, String host, int port) {
		super(name);
		this.host = host;
		this.port = port;
	}

	@Override
	public void init() {
		super.init();
		MemcachedClient memcache = getMemcacheClient();
		if (memcache == null) {
			return;
		}
		
		String name = getName();
		long expireAfterWrite = getExpireAfterWrite();
		expireAfterWrite = expireAfterWrite > 0 ? expireAfterWrite
				: CacheConstant.DEFAULT_EXPIRE_AFTER_WRITE;
		setExpireAfterWrite(expireAfterWrite);
		int expireTime = (int) expireAfterWrite;
		cache = new CacheMap(memcache, expireTime, name + ":");
	}

	public MemcachedClient getMemcacheClient() {
		MemcachedClient memcache = null;
		try {
			memcache = new MemcachedClient(new InetSocketAddress(host, port));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memcache;
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public long getSize() {
		return cache.size();
	}

	@Override
	public void set(String key, Object value) {
		cache.put(key, value);
	}

	@Override
	public void delete(String key) {
		cache.remove(key);

	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public boolean exists(String key) {
		return cache.containsKey(key);
	}

	@Override
	public List<String> searchKey(String pattern) {
		List<String> result = new LinkedList<String>();
		for (String key : cache.keySet()) {
			if (key.matches(pattern)) {
				result.add(key);
			}
		}
		return result;
	}

	@Override
	protected Object internalGet(String key) {
		return cache.get(key);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
