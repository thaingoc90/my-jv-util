package ind.web.nhp.cache;

public interface ICacheManager {

	/**
	 * Initializing method.
	 */
	public void init();

	/**
	 * Destruction method.
	 */
	public void destroy();

	/**
	 * Get cache
	 */
	public ICache getCache(String name);

	/**
	 * Get All caches
	 */
	public ICache[] getAllCaches();

	/**
	 * Remove a cache
	 */
	public void removeCache(String name);

	/**
	 * Create a cache
	 */
	public ICache createCache(String name);

	/**
	 * Create a cache with cacheConfig
	 */
	public ICache createCache(String name, Object cacheConfig);

}
