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
	private MemcachedClient memcacheClient;

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
		if (memcacheClient == null) {
			try {
				memcacheClient = new MemcachedClient(new InetSocketAddress(
						host, port));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return memcacheClient;
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public long getSize() {
		// NOT SUPPORT YET
		return 0;
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
		// NOT SUPPORT YET
	}

	@Override
	public boolean exists(String key) {
		return cache.containsKey(key);
	}

	@Override
	public List<String> searchKey(String pattern) {
		List<String> result = new LinkedList<String>();
		// NOT SUPPORT YET
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
