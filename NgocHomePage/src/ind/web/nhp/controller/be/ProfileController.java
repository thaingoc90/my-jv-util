package ind.web.nhp.controller.be;

import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.LoginBEForm;
import ind.web.nhp.form.UserBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.SimpleAuthenticationAgent;
import ind.web.nhp.us.impl.UserBo;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController extends BaseBackendController {

	private static final String VIEW_NAME_PROFILE = "be_profile";
	private static final String VIEW_NAME_CHANGE_PASS = "be_change_pass";

	@Autowired
	private SimpleAuthenticationAgent aa;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = { "/admin/profile", "/admin/profile/**" }, method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return VIEW_NAME_PROFILE;
	}

	@RequestMapping(value = { "/admin/profile", "/admin/profile/**" }, method = RequestMethod.POST)
	public String post(@ModelAttribute UserBEForm form, HttpServletRequest request, Model model) {
		UserBo user = (UserBo) getCurrentUser();
		user.setBirthday(form.getBirthday());
		user.setDisplayName(form.getDisplayName());
		usManager.updateUser(user);
		model.addAttribute("USER", user);
		model.addAttribute(ErrorConstants.MODEL_SUCCESS,
		        this.lang.getMessage("msg.profile.edit.succ"));
		return VIEW_NAME_PROFILE;
	}

	@RequestMapping(value = "/admin/profile/changePassword", method = RequestMethod.GET)
	public String getHandler(HttpServletRequest request, Model model) {
		return VIEW_NAME_CHANGE_PASS;
	}

	@RequestMapping(value = "/admin/profile/changePassword", method = RequestMethod.POST)
	public String postHandler(@ModelAttribute LoginBEForm form, Model model,
	        RedirectAttributes redirectAttributes) {
		ErrorModel errorObj = new ErrorModel();
		errorObj.setErrorCode(ErrorConstants.ERROR_CODE_DEFAULT);
		errorObj.setMsgSuccess(this.lang.getMessage("error.profile.changePass.succ"));
		validate(form, errorObj);

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
			return VIEW_NAME_CHANGE_PASS;
		}

		IUser user = getCurrentUser();
		String hashPassword = aa.getPasswordEncoder().encodePassword(form.getPassword(),
		        aa.getSaltGenerator().generateSalt(user));
		((UserBo) user).setPassword(hashPassword);
		usManager.updateUser(user);

		model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		return VIEW_NAME_CHANGE_PASS;
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
