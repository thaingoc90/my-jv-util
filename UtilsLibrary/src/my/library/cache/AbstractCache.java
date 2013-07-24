package my.library.cache;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractCache implements ICache {

	private String name;
	private long capacity;
	private long expireAfterWrite;
	private long expireAfterAccess;
	private AtomicLong hits = new AtomicLong(), misses = new AtomicLong();

	public AbstractCache() {
	}

	public AbstractCache(String name) {
		this.name = name;
	}

	public AbstractCache(String name, long capacity) {
		this.name = name;
		this.capacity = capacity;
	}

    @Override
    public void init() {
        // EMPTY
    }

    @Override
    public void destroy() {
        // EMPTY
    }
	
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	@Override
	public long getExpireAfterWrite() {
		return expireAfterWrite;
	}

	public void setExpireAfterWrite(long expireAfterWrite) {
		this.expireAfterWrite = expireAfterWrite;
	}

	@Override
	public long getExpireAfterAccess() {
		return expireAfterAccess;
	}

	public void setExpireAfterAccess(long expireAfterAccess) {
		this.expireAfterAccess = expireAfterAccess;
	}

	@Override
	public long getHits() {
		return hits.get();
	}

	public long increaseHits() {
		return hits.incrementAndGet();
	}

	@Override
	public long getMisses() {
		return misses.get();
	}

	public long increaseMisses() {
		return misses.incrementAndGet();
	}

	@Override
	public Object get(String key) {
		Object value = internalGet(key);
		if (value == null) {
			increaseMisses();
		} else {
			increaseHits();
		}
		return value;
	}
	
	protected abstract Object internalGet(String key);
}
