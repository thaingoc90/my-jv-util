package my.library.cache.config;

import my.library.cache.CacheConstant;

public class RedisCacheConfig {

	private String type;
	private String host;
	private int port;
	private long expireAfterWrite;
	private int maxActive;
	private boolean testOnReturn;

	public RedisCacheConfig() {
		this.type = CacheConstant.CACHE_TYPE_REDIS;
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

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public String getType() {
		return type;
	}

}
