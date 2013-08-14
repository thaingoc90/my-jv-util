package ind.web.nhp.controller.be;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.us.IMenu;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.utils.UrlUtils;
import ind.web.nhp.utils.Utils;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseBackendController extends BaseController {

	private boolean loginAuthentication = true;

	@Autowired
	protected IUsManager usManager;
	
	@Autowired
	protected BuildMenu menuBuilder;

	@ModelAttribute("baseUrl")
	public String modelBaseUrl(HttpServletRequest request) {
		String url = UrlUtils.getBaseUrl(request);
		url += "/" + Constants.MODULE_ADMIN + "/";
		return url;
	}

	@ModelAttribute("mainMenu")
	public List<IMenu> modelMenu(HttpServletRequest request) {
		if (usManager == null) {
			return new LinkedList<IMenu>();
		}
		List<String> listPermIds = new LinkedList<String>();
		return menuBuilder.buildMainMenu(listPermIds);
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
