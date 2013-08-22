package ind.web.nhp.cache;

import java.util.List;

public interface ICache {

	/**
	 * Initializing method.
	 */
	public void init();

	/**
	 * Destruction method.
	 */
	public void destroy();

	/**
	 * Gets cache's name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Gets cache's size
	 * 
	 * @return
	 */
	public long getSize();

	/**
	 * Gets cache's number of hits
	 * 
	 * @return
	 */
	public long getHits();

	/**
	 * Gets cache's number of misses
	 * 
	 * @return
	 */
	public long getMisses();

	/**
	 * Gets number of seconds before entries to be expired since the last access
	 * 
	 * @return
	 */
	public long getExpireAfterAccess();

	/**
	 * Gets number of seconds before entries to be expired since the last write
	 * 
	 * @return
	 */
	public long getExpireAfterWrite();

	/**
	 * Puts an entry to cache, default expiry
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value);

	/**
	 * Puts an entry to cache, with expireAfterAccess or expireAfterWrite
	 * 
	 * @param key
	 * @param value
	 * @param expireAfterAcess
	 * @param expireAfterWrite
	 */
	// public void set(String key, Object value, long expireAfterAccess, long
	// expireAfterWrite);

	/**
	 * Gets an entry from cache by key
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * Deletes an entry from cache
	 * 
	 * @param key
	 */
	public void delete(String key);

	/**
	 * Clears all entries in cache
	 */
	public void clear();

	/**
	 * Checks if an entry exists in cache
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key);

	/**
	 * Gets all keys from cache which match with pattern
	 * 
	 * @param pattern
	 * @return
	 */
	public List<String> searchKey(String pattern);
}
