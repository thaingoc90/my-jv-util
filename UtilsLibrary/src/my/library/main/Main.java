package my.library.main;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;



public class Main {

	public static void main(String[] args) throws Exception {
		String decoded = URLDecoder.decode("https://www.facebook.com/sharer/sharer.php?s=100&p%5Burl%5D=http%3A%2F%2Fstaging.tuigamer.com%2Farmory%2Fngoalong%2Fcharacter%2F15262%2Fkimlong&p%5Bimages%5D%5B0%5D=http%3A%2F%2Fstaging.tuigamer.com%2Farmory%2Fngoalong%2Fimages%2Fwives%2F90.png&p%5Btitle%5D=Ng%E1%BB%8Da+long+danh+nh%C3%A2n+%C4%91%C6%B0%E1%BB%9Dng&p%5Bsummary%5D=Server%3A+kimlong%2CAccount%3A15262%2C+Level%3A180", "UTF-8");
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
