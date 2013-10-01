package ind.web.nhp.controller;

import org.springframework.beans.factory.annotation.Autowired;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.utils.Utils;

public class BaseFrontendController extends BaseController {

	private boolean loginAuthentication = false;

	@Autowired
	protected IUsManager usManager;

	protected IUser getCurrentUser() {
		IUser user = null;
		Object userIdObj = Utils.getSessionAttribute(false, Constants.NHP_FE_USER_ID);
		int userId = Utils.toInt(userIdObj);
		if (userId != 0) {
			user = usManager.getUser(userId);
		}
		return user;
	}

	protected String getCurrentUsername() {
		IUser user = getCurrentUser();
		return (user != null) ? user.getLoginName() : null;
	}

	public boolean isLoginAuthentication() {
		return loginAuthentication;
	}

	public void setLoginAuthentication(boolean loginAuthentication) {
		this.loginAuthentication = loginAuthentication;
	}

}
