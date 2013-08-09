package ind.web.nhp.controller.be;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.utils.UrlUtils;
import ind.web.nhp.utils.Utils;

public class BaseBackendController extends BaseController {

	private boolean loginAuthentication = true;

	@Autowired
	protected IUsManager usManager;

	@ModelAttribute("baseUrl")
	public String modelBaseUrl(HttpServletRequest request) {
		String url = UrlUtils.getBaseUrl(request);
		url += "/" + Constants.MODULE_ADMIN + "/";
		return url;
	}

	public IUser getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		IUser user = null;
		if (session != null) {
			Object userIdObj = session.getAttribute(Constants.NHP_USER_ID);
			int userId = Utils.toInt(userIdObj);
			if (userId != 0) {
				user = usManager.getUser(userId);
			}
		}
		return user;
	}

	public boolean isLoginAuthentication() {
		return loginAuthentication;
	}

	public void setLoginAuthentication(boolean loginAuthentication) {
		this.loginAuthentication = loginAuthentication;
	}
}
