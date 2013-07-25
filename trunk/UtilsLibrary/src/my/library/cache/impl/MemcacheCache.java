package my.library.cache.impl;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import my.library.cache.AbstractCache;
import my.library.cache.ICache;
import my.library.cache.config.MemcacheConfig;
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

	public MemcacheCache(String name, MemcacheConfig config) {
		super(name);
		if (config != null) {
			this.host = config.getHost();
			this.port = config.getPort();
			this.expireAfterWrite = config.getExpireAfterWrite();
		}
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
		int expireTime = (int) expireAfterWrite;
		if (expireTime <= 0) {
			cache = new CacheMap(memcache, name + ":");
		} else {
			cache = new CacheMap(memcache, expireTime, name + ":");
		}
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
		for (String key : cache.keySet()) {
			cache.remove(key);
		}
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

	public int getPort() {
		return port;
	}

}
