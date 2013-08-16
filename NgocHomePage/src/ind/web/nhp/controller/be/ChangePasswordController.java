package ind.web.nhp.controller.be;

import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.LoginBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.SimpleAuthenticationAgent;
import ind.web.nhp.us.impl.UserBo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChangePasswordController extends BaseBackendController {

	private static final String VIEW_NAME = "be_change_pass";

	@Autowired
	private SimpleAuthenticationAgent aa;

	@RequestMapping(value = "/admin/profile/changePassword", method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return VIEW_NAME;
	}

	@RequestMapping(value = "/admin/profile/changePassword", method = RequestMethod.POST)
	public String post(@ModelAttribute LoginBEForm form, Model model,
	        RedirectAttributes redirectAttributes) {
		ErrorModel errorObj = new ErrorModel();
		errorObj.setErrorCode(ErrorConstants.ERROR_CODE_DEFAULT);
		errorObj.setMsgSuccess(this.lang.getMessage("error.profile.changePass.succ"));
		validate(form, errorObj);

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
			return VIEW_NAME;
		}

		IUser user = getCurrentUser();
		String hashPassword = aa.getPasswordEncoder().encodePassword(form.getPassword(),
		        aa.getSaltGenerator().generateSalt(user));
		((UserBo) user).setPassword(hashPassword);
		usManager.updateUser(user);

		model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		return VIEW_NAME;
	}

	private boolean validate(LoginBEForm form, ErrorModel errorObj) {
		String password = form.getPassword();
		String oldPassword = form.getOldPassword();
		String confirmPassword = form.getConfirmPassword();
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(oldPassword)
		        || StringUtils.isEmpty(confirmPassword)) {
			errorObj.addMsgError(this.lang.getMessage("error.field.required"));
			return false;
		}

		if (!password.equals(confirmPassword)) {
			errorObj.addMsgError(this.lang.getMessage("error.profile.confirmPass.notMatch"));
			return false;
		}

		IUser user = getCurrentUser();
		if (!aa.authenticate(user, form.getOldPassword())) {
			errorObj.addMsgError(this.lang.getMessage("error.profile.oldPassword.notMatch"));
			return false;
		}

		return true;
	}
}
