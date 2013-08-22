package ind.web.nhp.interceptor;

import ind.web.nhp.base.Constants;
import ind.web.nhp.controller.be.BaseBackendController;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.utils.UrlUtils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PermissionAuthorization extends HandlerInterceptorAdapter {

	@Autowired
	private UrlUtils urlUtils;

	@Autowired
	private IUsManager usManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	        Object handlerObj) throws Exception {

		String[] pathParams = new String[] { "admin" };
		String urlLogin = urlUtils.createUrl(request, response, pathParams, null, null);

		HandlerMethod handler = (HandlerMethod) handlerObj;
		if (handler.getBean() instanceof BaseBackendController) {
			BaseBackendController controller = (BaseBackendController) handler.getBean();
			List<String> listUserPermissions = controller.getPermissionIds();

			String urlMapping = controller.getUrlMapping();
			if (StringUtils.isEmpty(urlMapping)) {
				return super.preHandle(request, response, handler);
			}

			urlMapping = reduceUrl(urlMapping);
			String accessPermission = usManager.getPermissionOfUrl(urlMapping);
			if (StringUtils.isEmpty(accessPermission)) {
				return super.preHandle(request, response, handler);
			}

			if (!listUserPermissions.contains(accessPermission)) {
				response.sendRedirect(urlLogin);
				return false;
			}
		}
		return super.preHandle(request, response, handler);
	}

	public String reduceUrl(String url) {
		String module = "/" + Constants.MODULE_ADMIN + "/";
		return url.substring(module.length());
	}
}
