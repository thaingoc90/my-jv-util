package my.library.cache.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import my.library.cache.AbstractCache;
import my.library.cache.CacheConstant;
import my.library.cache.ICache;
import my.library.cache.config.GuavaCacheConfig;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * lib: guava-14.1.jar
 * 
 * @author ngoctdn
 * 
 */
public class GuavaCache extends AbstractCache implements ICache {
	private Cache<String, Object> cache;
	private long capacity;

	public GuavaCache() {
	}

	public GuavaCache(String name) {
		super(name);
	}

	public GuavaCache(String name, GuavaCacheConfig config) {
		super(name);
		if (config != null) {
			this.capacity = config.getCapacity();
			this.expireAfterAccess = config.getExpireAfterAccess();
			this.expireAfterWrite = config.getExipreAfterWrite();
		}
	}

	@Override
	public void init() {
		super.init();
		CacheBuilder<Object, Object> cacheBuider = CacheBuilder.newBuilder();
		int numProcessores = Runtime.getRuntime().availableProcessors();
		cacheBuider.concurrencyLevel(numProcessores);

		long capacity = getCapacity();
		capacity = capacity > 0 ? capacity
				: CacheConstant.DEFAULT_CACHE_CAPACITY;
		this.capacity = capacity;
		cacheBuider.maximumSize(capacity);

		long expireAfterAccess = getExpireAfterAccess();
		long expireAfterWrite = getExpireAfterWrite();

		if (expireAfterWrite > 0) {
			cacheBuider.expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
		} else if (expireAfterAccess > 0) {
			cacheBuider.expireAfterAccess(expireAfterAccess, TimeUnit.SECONDS);
		} else {
			cacheBuider.expireAfterWrite(
					CacheConstant.DEFAULT_EXPIRE_AFTER_WRITE, TimeUnit.SECONDS);
			this.expireAfterWrite = CacheConstant.DEFAULT_EXPIRE_AFTER_WRITE;
		}
		cache = cacheBuider.build();
	}

	@Override
	public void destroy() {
		try {
			cache.cleanUp();
			cache = null;
		} finally {
			super.destroy();
		}

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
		cache.invalidate(key);

	}

	@Override
	public void clear() {
		cache.invalidateAll();
	}

	@Override
	public boolean exists(String key) {
		return cache.getIfPresent(key) != null;
	}

	@Override
	public List<String> searchKey(String pattern) {
		List<String> result = new LinkedList<String>();
		Set<String> keys = cache.asMap().keySet();
		for (String key : keys) {
			if (key.matches(pattern)) {
				result.add(key);
			}
		}
		return result;
	}

	@Override
	protected Object internalGet(String key) {
		return cache.getIfPresent(key);
	}

	public long getCapacity() {
		return capacity;
	}

}
