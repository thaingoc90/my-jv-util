package ind.web.nhp.cache.impl;

import ind.web.nhp.cache.AbstractCacheManager;
import ind.web.nhp.cache.ICache;
import ind.web.nhp.cache.ICacheManager;

public class DefaultCacheManager extends AbstractCacheManager implements ICacheManager {

	private long countDownTime = 0l;
	private static final long WAIT_TIME = 60 * 5 * 1000;

	/**
	 * Encapsualtion: config cache by configObject.
	 */
	@Override
	protected ICache internalCreateCache(String name, Object cacheConfig) {
		/** Waits 5minutes if have erros when creating cache */
		if (countDownTime != 0 && System.currentTimeMillis() - countDownTime < 0) {
			return null;
		}
		try {
			if (cacheConfig instanceof GuavaCacheConfig) {
				GuavaCacheConfig config = (GuavaCacheConfig) cacheConfig;
				GuavaCache cache = new GuavaCache(name, config);
				cache.init();
				return cache;
			} else if (cacheConfig instanceof MemcacheConfig) {
				MemcacheConfig config = (MemcacheConfig) cacheConfig;
				MemcacheCache cache = new MemcacheCache(name, config);
				cache.init();
				return cache;
			} else if (cacheConfig instanceof RedisCacheConfig) {
				RedisCacheConfig config = (RedisCacheConfig) cacheConfig;
				RedisCache cache = new RedisCache(name, config);
				cache.init();
				return cache;
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			countDownTime = System.currentTimeMillis() + WAIT_TIME;
		}
		return null;
	}
}
