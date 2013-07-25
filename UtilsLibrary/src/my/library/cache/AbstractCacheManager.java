package my.library.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

public abstract class AbstractCacheManager implements ICacheManager {

	private ConcurrentMap<String, ICache> caches;

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
	public abstract ICache createCache(String name);

	@Override
	public ICache createCache(String name, Object cacheConfig) {
		ICache cache = caches.get(name);
		if (cache == null) {
			cache = internalCreateCache(name, cacheConfig);
			caches.put(name, cache);
		}
		return cache;
	}

	protected abstract ICache internalCreateCache(String name,
			Object cacheConfig);

}
