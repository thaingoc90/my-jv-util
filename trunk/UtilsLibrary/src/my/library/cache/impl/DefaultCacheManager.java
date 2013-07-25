package my.library.cache.impl;

import my.library.cache.AbstractCacheManager;
import my.library.cache.ICache;
import my.library.cache.ICacheManager;
import my.library.cache.config.GuavaCacheConfig;
import my.library.cache.config.MemcacheConfig;
import my.library.cache.config.RedisCacheConfig;

public class DefaultCacheManager extends AbstractCacheManager implements
		ICacheManager {

	/**
	 * Encapsualtion: config cache by configObject.
	 */
	@Override
	protected ICache internalCreateCache(String name, Object cacheConfig) {
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
		return null;
	}

	/**
	 * Not use yet
	 */
	@Override
	public ICache createCache(String name) {
		return null;
	}

}
