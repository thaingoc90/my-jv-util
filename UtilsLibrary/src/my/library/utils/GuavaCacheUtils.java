package my.library.utils;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

/**
 * lib: guava-13.0.jar
 * 
 * @author ngoctdn
 *
 */

public class GuavaCacheUtils {
	public static final int TIME_EXPIRE = 15;
	public static final long MAX_SIZE_CACHE = 500l;

	public static LoadingCache<String, Object> loadingCache = CacheBuilder.newBuilder()
			.expireAfterWrite(TIME_EXPIRE, TimeUnit.MINUTES).maximumSize(MAX_SIZE_CACHE)
			.recordStats().build(new CacheLoader<String, Object>() {
				@Override
				public Object load(String key) throws Exception {
					return key.toUpperCase();
				}
			});

	public static Cache<String, Object> cache = CacheBuilder.newBuilder()
			.expireAfterWrite(TIME_EXPIRE, TimeUnit.MINUTES).maximumSize(MAX_SIZE_CACHE)
			.recordStats().build();

	public static CacheStats getLoadingCacheStats() {
		return loadingCache.stats();
	}
}
