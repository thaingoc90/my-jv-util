package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.LoginBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.SimpleAuthenticationAgent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController extends BaseBackendController {

	private static final String VIEW_NAME = "be_login";

	@Autowired
	private SimpleAuthenticationAgent aa;

	private IUser user;

	@RequestMapping(value = "/admin/login", method = RequestMethod.GET)
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return VIEW_NAME;
	}

	@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
	public String post(@ModelAttribute LoginBEForm form, Model model, HttpServletRequest request,
	        RedirectAttributes redirectAttributes) {
		ErrorModel errorObj = new ErrorModel();
		errorObj.setErrorCode(ErrorConstants.ERROR_CODE_DEFAULT);
		validate(form, errorObj);
		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
			return VIEW_NAME;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute(Constants.NHP_USER_ID, user.getId());

		return "redirect:/admin/dashboard";
	}

	private boolean validate(LoginBEForm form, ErrorModel errorObj) {
		String email = form.getEmail();
		String rawPassword = form.getPassword();

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(rawPassword)) {
			errorObj.addMsgError(this.lang.getMessage("error.field.required"));
			return false;
		}

		user = usManager.getUserByEmail(email);
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

		return true;
	}
}
