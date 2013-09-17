package ind.web.nhp.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.web.nhp.cache.ICache;
import ind.web.nhp.cache.ICacheManager;

public abstract class CacheDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheDao.class);
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
			try {
				cache.clear();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
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
			try {
				cache.delete(key);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
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
				try {
					cache.set(key, value);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
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
		if (cache == null) {
			return null;
		}
		Object value = null;
		try {
			value = cache.get(key);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
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
