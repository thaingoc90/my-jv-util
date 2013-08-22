package ind.web.nhp.cache.impl;

import ind.web.nhp.cache.CacheConstant;

public class GuavaCacheConfig {

	private String type;
	private long capacity;
	private long exipreAfterWrite;
	private long expireAfterAccess;

	public GuavaCacheConfig() {
		this.type = CacheConstant.CACHE_TYPE_GUAVA;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getExipreAfterWrite() {
		return exipreAfterWrite;
	}

	public void setExipreAfterWrite(long exipreAfterWrite) {
		this.exipreAfterWrite = exipreAfterWrite;
	}

	public long getExpireAfterAccess() {
		return expireAfterAccess;
	}

	public void setExpireAfterAccess(long expireAfterAccess) {
		this.expireAfterAccess = expireAfterAccess;
	}

	public String getType() {
		return type;
	}

}
