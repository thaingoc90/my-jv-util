package my.library.main;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;

import my.library.cache.ICache;
import my.library.cache.ICacheManager;
import my.library.cache.config.GuavaCacheConfig;
import my.library.cache.config.MemcacheConfig;
import my.library.cache.config.RedisCacheConfig;
import my.library.cache.impl.DefaultCacheManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	public static void main(String[] args) throws Exception {
		Document doc = Jsoup.connect("http://360game.vn/hinh-anh").get();
		System.out.println(doc.getElementsByTag("img"));
	}

	public static void initCache() {
		ICacheManager cacheManger = new DefaultCacheManager();
		cacheManger.init();
		MemcacheConfig cacheConfig = new MemcacheConfig();
		cacheConfig.setHost("127.0.0.1");
		cacheConfig.setPort(11211);
		ICache memcache = cacheManger.createCache("Test_cache", cacheConfig);
		memcache.set("abc", 3);
		memcache.set("dec", 5);

		ICache memcache2 = cacheManger.createCache("Test_cache2", cacheConfig);
		memcache2.set("ef", 5);
		memcache2.clear();
		System.out.println(memcache.get("abc"));
		System.out.println(memcache2.get("ef"));

		GuavaCacheConfig guavaConfig = new GuavaCacheConfig();
		ICache guavaCache = cacheManger.createCache("Guava_Cache", guavaConfig);
		guavaCache.set("abc", 3);
		guavaCache.set("ebc", 4);
		System.out.println(guavaCache.searchKey("%bc"));

		ICache guavaCache2 = cacheManger.createCache("Guava_Cache2",
				guavaConfig);
		guavaCache2.set("abc", 3);
		guavaCache.clear();
		System.out.println(guavaCache.get("abc"));
		System.out.println(guavaCache2.get("abc"));

		RedisCacheConfig redisConfig = new RedisCacheConfig();
		redisConfig.setHost("127.0.0.1");
		redisConfig.setPort(6379);
		redisConfig.setMaxActive(100);
		redisConfig.setTestOnReturn(true);
		ICache redisCache = cacheManger.createCache("RedisCache", redisConfig);
		redisCache.set("123", 123);
		System.out.println(redisCache.get("123"));

		ICache redisCache2 = cacheManger
				.createCache("RedisCache2", redisConfig);
		redisCache2.set("123", 344);
		redisCache2.set("423", 345);
		System.out.println(redisCache2.searchKey("*23"));

		System.out.println("---end----");
		cacheManger.removeCache("RedisCache");
		cacheManger.removeCache("RedisCache2");
		cacheManger.removeCache("Test_cache");
		cacheManger.removeCache("Guava_Cache");
		cacheManger.destroy();
	}

	public void printUrlDecode() throws Exception {
		String decoded = URLDecoder
				.decode("https://www.facebook.com/sharer/sharer.php?s=100&p%5Burl%5D=http%3A%2F%2Fstaging.tuigamer.com%2Farmory%2Fngoalong%2Fcharacter%2F15262%2Fkimlong&p%5Bimages%5D%5B0%5D=http%3A%2F%2Fstaging.tuigamer.com%2Farmory%2Fngoalong%2Fimages%2Fwives%2F90.png&p%5Btitle%5D=Ng%E1%BB%8Da+long+danh+nh%C3%A2n+%C4%91%C6%B0%E1%BB%9Dng&p%5Bsummary%5D=Server%3A+kimlong%2CAccount%3A15262%2C+Level%3A180",
						"UTF-8");
		System.out.println(decoded);
	}

	public static void getCookieUsingCookieHandler() {
		try {
			// Instantiate CookieManager;
			// make sure to set CookiePolicy
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);

			// get content from URLConnection;
			// cookies are set by web site
			URL url = new URL("http://www.bongdaso.com");
			URLConnection connection = url.openConnection();
			connection.getContent();

			// get cookies from underlying
			// CookieStore
			CookieStore cookieJar = manager.getCookieStore();
			List<HttpCookie> cookies = cookieJar.getCookies();
			for (HttpCookie cookie : cookies) {
				System.out.println("CookieHandler retrieved cookie: " + cookie);
			}
			System.out.println("Finish");
		} catch (Exception e) {
			System.out.println("Unable to get cookie using CookieHandler");
			e.printStackTrace();
		}
	}
}
