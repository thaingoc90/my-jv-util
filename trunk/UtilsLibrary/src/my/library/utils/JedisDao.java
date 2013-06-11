package my.library.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis-2.1.0.jar
 * 
 * @author ngoctdn
 *
 */
public class JedisDao {

	private JedisPool pool;

	public JedisDao(String host, int port) {
		initJedis(host, port);
	}

	public static void main(String[] args) {
		JedisDao jedDao = new JedisDao("10.60.47.10", 6379);
		jedDao.jSet("ngoc", "abc");
		jedDao.jGet("ngoc");
		jedDao.destroyJedis();

	}

	public void initJedis(String host, int port) {
		JedisPoolConfig jedisConfig = new JedisPoolConfig();
		jedisConfig.setMaxActive(150);
		jedisConfig.setTestWhileIdle(true);
		// pool = new JedisPool(jedisConfig, "10.60.7.221", 6379);
		pool = new JedisPool(jedisConfig, host, port);
	}

	public void destroyJedis() {
		pool.destroy();
	}

	public void jSet(String key, String value) {
		Jedis jConnection = null;
		try {
			jConnection = pool.getResource();
			String res = jConnection.set(key, value);
			System.out.println(res);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			pool.returnResource(jConnection);
		}
	}

	public void jGet(String key) {
		Jedis jConnection = null;
		try {
			jConnection = pool.getResource();
			String res = jConnection.get(key);
			System.out.println(res);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			pool.returnResource(jConnection);
		}
	}

	public void jIncr(String key) {
		Jedis jConnection = null;
		try {
			jConnection = pool.getResource();
			Long res = jConnection.incr(key);
			System.out.println(res);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			pool.returnResource(jConnection);
		}
	}

	public void jDel(String key) {
		Jedis jConnection = null;
		try {
			jConnection = pool.getResource();
			Long res = jConnection.del(key);
			System.out.println(res);
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			pool.returnResource(jConnection);
		}
	}
}
