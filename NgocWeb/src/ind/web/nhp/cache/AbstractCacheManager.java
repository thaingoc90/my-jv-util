package ind.web.nhp.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

public abstract class AbstractCacheManager implements ICacheManager {

	private ConcurrentMap<String, ICache> caches;
	private Object cacheConfig;
	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init() {
		int numProcessores = Runtime.getRuntime().availableProcessors();
		MapMaker mm = new MapMaker();
		mm.concurrencyLevel(numProcessores);
		caches = mm.makeMap();
	}

	@Override
	public void destroy() {
		if (caches != null) {
			try {
				for (Entry<String, ICache> entry : caches.entrySet()) {
					try {
						entry.getValue().destroy();
					} catch (Exception e) {
						// EMPTY
					}
				}
			} finally {
				caches.clear();
				caches = null;
			}
		}
	}

	@Override
	public ICache getCache(String name) {
		return caches.get(name);
	}

	@Override
	public ICache[] getAllCaches() {
		List<ICache> result = new ArrayList<ICache>();
		for (Entry<String, ICache> entry : caches.entrySet()) {
			result.add(entry.getValue());
		}
		return result.toArray(new ICache[0]);
	}

	@Override
	public void removeCache(String name) {
		ICache cache = caches.get(name);
		if (cache != null) {
			try {
				cache.destroy();
			} finally {
				cache = null;
			}
		}
	}

	@Override
	public ICache createCache(String name) {
		ICache cache = caches.get(name);
		if (cache != null) {
			return cache;
		}
		Object cacheConfig = getCacheConfig();
		return createCache(name, cacheConfig);
	}

	@Override
	public ICache createCache(String name, Object cacheConfig) {
		ICache cache = internalCreateCache(name, cacheConfig);
		if (cache != null) {
			caches.put(name, cache);
		}
		return cache;
	}

	protected abstract ICache internalCreateCache(String name, Object cacheConfig);

	public Object getCacheConfig() {
		return cacheConfig;
	}

	public void setCacheConfig(Object cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

}
