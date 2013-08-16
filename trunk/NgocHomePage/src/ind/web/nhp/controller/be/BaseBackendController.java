package ind.web.nhp.controller.be;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.us.IMenu;
import ind.web.nhp.us.IPermission;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.utils.UrlUtils;
import ind.web.nhp.utils.Utils;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseBackendController extends BaseController {

	private boolean loginAuthentication = true;
	private String urlMapping;

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
		List<String> listPermIds = getPermissionIds();
		return menuBuilder.buildMainMenu(listPermIds);
	}

	public IUser getCurrentUser() {
		IUser user = null;
		Object userIdObj = Utils.getSessionAttribute(false, Constants.NHP_USER_ID);
		int userId = Utils.toInt(userIdObj);
		if (userId != 0) {
			user = usManager.getUser(userId);
		}
		return user;
	}

	/**
	 * Gets List Permission of currentUser
	 * 
	 * @return
	 */
	public List<String> getPermissionIds() {
		List<String> listPermissionIds = new LinkedList<String>();
		IUser user = getCurrentUser();
		if (user == null) {
			return listPermissionIds;
		}
		IPermission[] listPermisions = usManager.getAllPermisionsOfGroupId(user.getGroupId());
		if (listPermisions != null) {
			for (IPermission perm : listPermisions) {
				String permId = perm.getId().trim();
				listPermissionIds.add(permId);
			}
		}
		return listPermissionIds;
	}

	public boolean isLoginAuthentication() {
		return loginAuthentication;
	}

	public void setLoginAuthentication(boolean loginAuthentication) {
		this.loginAuthentication = loginAuthentication;
	}

	public String getUrlMapping() {
		return urlMapping;
	}

	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

}
