package ind.web.nhp.cache;

public class CacheConstant {

	public final static long DEFAULT_CACHE_CAPACITY = 1000l;
	public final static long DEFAULT_EXPIRE_AFTER_WRITE = 1800l;
	public final static long DEFAULT_EXPIRE_AFTER_ACCESS = 1800l;

	public final static int DEFAULT_REDIS_MAX_ACTIVE = 200;
	
	public final static String CACHE_TYPE_GUAVA = "CACHE_GUAVE";
	public final static String CACHE_TYPE_MEMCACHE = "CACHE_MEMCACHE";
	public final static String CACHE_TYPE_REDIS = "CACHE_REDIS";
}
