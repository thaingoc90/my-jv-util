package my.library.cache.config;

import my.library.cache.CacheConstant;

public class MemcacheConfig {

	private String type;
	private String host;
	private int port;
	private long expireAfterWrite;

	public MemcacheConfig() {
		this.type = CacheConstant.CACHE_TYPE_MEMCACHE;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getExpireAfterWrite() {
		return expireAfterWrite;
	}

	public void setExpireAfterWrite(long expireAfterWrite) {
		this.expireAfterWrite = expireAfterWrite;
	}

	public String getType() {
		return type;
	}

}
