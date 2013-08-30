package ind.web.nhp.interceptor;

import ind.web.nhp.base.manager.IConfigManager;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class IpAuthentication extends HandlerInterceptorAdapter {

	@Autowired
	private IConfigManager configManager;

	public static final boolean ENABLE_IP_AUTHEN = true;
	public List<String> listBlockedIp = new LinkedList<String>();
	public List<String> listAllowedIp = new LinkedList<String>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if (ENABLE_IP_AUTHEN) {
			String clientIp = getClientIpAddr(request);
			if (listAllowedIp != null && listAllowedIp.size() > 0
					&& !listAllowedIp.contains(clientIp)) {
				return false;
			}
			if (listBlockedIp != null && listBlockedIp.size() > 0
					&& listBlockedIp.contains(clientIp)) {
				return false;
			}
		}
		return super.preHandle(request, response, handler);
	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
