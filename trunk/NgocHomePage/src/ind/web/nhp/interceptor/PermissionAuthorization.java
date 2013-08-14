package ind.web.nhp.interceptor;

import ind.web.nhp.controller.be.BaseBackendController;
import ind.web.nhp.utils.UrlUtils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PermissionAuthorization extends HandlerInterceptorAdapter {

	@Autowired
	private UrlUtils urlUtils;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	        Object handlerObj) throws Exception {

		HandlerMethod handler = (HandlerMethod) handlerObj;
		if (handler.getBean() instanceof BaseBackendController) {
			BaseBackendController controller = (BaseBackendController) handler.getBean();
			String[] accessPermissions = new String[2];
			// String[] accessPermissions = controller.getAccessPermissions();
			if (accessPermissions != null && accessPermissions.length > 0) {
				List<String> listUserPermissions = controller.getPermissionIds();
				for (String permission : accessPermissions) {
					if (!listUserPermissions.contains(permission)) {
						return false;
					}
				}
			}
		}
		return super.preHandle(request, response, handler);
	}
}
