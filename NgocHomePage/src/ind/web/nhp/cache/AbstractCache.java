package ind.web.nhp.cache;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author ngoctdn
 *
 */
public abstract class AbstractCache implements ICache {

	protected String name;
	protected long expireAfterWrite;
	protected long expireAfterAccess;
	private AtomicLong hits = new AtomicLong(), misses = new AtomicLong();

	public AbstractCache() {
	}

	public AbstractCache(String name) {
		this.name = name;
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
	public long getExpireAfterWrite() {
		return expireAfterWrite;
	}

	@Override
	public long getExpireAfterAccess() {
		return expireAfterAccess;
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
