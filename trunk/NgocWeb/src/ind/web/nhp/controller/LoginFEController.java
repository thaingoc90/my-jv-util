package ind.web.nhp.controller;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.UserFEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.SimpleAuthenticationAgent;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginFEController extends BaseFrontendController {

	@Autowired
	private SimpleAuthenticationAgent aa;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> post(@ModelAttribute UserFEForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ErrorModel errorObj = new ErrorModel();
		errorObj.setErrorCode(ErrorConstants.ERROR_CODE_DEFAULT);
		boolean isLogin = doLogin(form, errorObj, request, response);
		if (isLogin) {
			return createAjaxOk("Login successful!");
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, errorObj.getMsgErrors().get(0));
		}
	}

	private boolean doLogin(UserFEForm form, ErrorModel errorObj, HttpServletRequest request,
			HttpServletResponse response) {
		String email = form.getEmail();
		String rawPassword = form.getPassword();

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(rawPassword)) {
			errorObj.addMsgError(this.lang.getMessage("error.field.required"));
			return false;
		}

		IUser user = usManager.getUserByEmail(email);
		if (user == null) {
			errorObj.addMsgError(this.lang.getMessage("error.admin.login"));
			return false;
		}

		if (!aa.authenticate(user, form.getPassword())) {
			errorObj.addMsgError(this.lang.getMessage("error.admin.login"));
			return false;
		} else if (user.isLocked()) {
			errorObj.addMsgError(this.lang.getMessage("error.admin.disabled", user.getEmail()));
			return false;
		}

		if (user.getGroupId() != Constants.GROUP_NORMAL_USER) {
			errorObj.addMsgError(this.lang.getMessage("error.admin.login"));
			return false;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute(Constants.NHP_FE_USER_ID, user.getId());
		if (form.getRemember() == 1) {
			Cookie cookie = new Cookie(Constants.NHP_FE_USER_ID, String.valueOf(user.getId()));
			cookie.setMaxAge(3600 * 24 * 30);
			response.addCookie(cookie);
		}
		return true;
	}
}
