package ind.web.nhp.base;

import ind.web.nhp.cache.ICache;
import ind.web.nhp.cache.ICacheManager;

public abstract class CacheDao {

	private ICache cache;
	private ICacheManager cacheManager;
	private String cacheName = this.getClass().getName();

	public void init() {
        cache = getCache();
    }
	
	protected boolean cacheEnabled() {
		return cacheManager != null;
	}

	/**
	 * Deletes all cache entries.
	 */
	protected void flushCache() {
		if (!cacheEnabled()) {
			return;
		}

		ICache cache = getCache();
		if (cache != null) {
			cache.clear();
		}
	}

	/**
	 * Deletes an entry from cache.
	 * 
	 * @param key
	 */
	protected void deleteFromCache(String key) {
		if (!cacheEnabled()) {
			return;
		}

		ICache cache = getCache();
		if (cache != null) {
			cache.delete(key);
		}
	}

	/**
	 * Puts an entry to cache
	 * 
	 * @param key
	 * @param value
	 */
	protected void putToCache(String key, Object value) {
		if (!cacheEnabled()) {
			return;
		}
		if (value != null) {
			ICache cache = getCache();
			if (cache != null) {
				cache.set(key, value);
			}
		}
	}

	/**
	 * Gets an entry from cache.
	 * 
	 * @param key
	 * @return
	 */
	protected Object getFromCache(String key) {
		if (!cacheEnabled()) {
			return null;
		}

		ICache cache = getCache();
		Object value = cache.get(key);
		return value;
	}

	public ICache getCache() {
		if (cache == null && cacheManager != null) {
			cache = cacheManager.createCache(cacheName);
		}
		return cache;
	}

	public ICacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(ICacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

}
