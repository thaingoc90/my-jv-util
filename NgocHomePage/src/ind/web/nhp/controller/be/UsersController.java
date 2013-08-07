package ind.web.nhp.controller.be;

import ind.web.nhp.encrypt.IPasswordEncoder;
import ind.web.nhp.encrypt.ISaltGenerator;
import ind.web.nhp.form.UserBEForm;
import ind.web.nhp.form.UserBEFormValidator;
import ind.web.nhp.us.IGroup;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.impl.UserBo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController extends BaseBackendController {
	private static final String VIEW_NAME = "be_users";

	@Autowired
	private IUsManager usManager;

	@Autowired
	private ISaltGenerator saltGenerator;

	@Autowired
	private IPasswordEncoder passwordEncoder;

	@Autowired
	private UserBEFormValidator userFormValidator;

	@RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
	public String get(Model model) {
		IUser[] listUsers = usManager.getAllUsers();
		IGroup[] listGroups = usManager.getAllGroups();
		Map<String, String> groupMapping = new HashMap<String, String>();
		for (IGroup group : listGroups) {
			groupMapping.put(String.valueOf(group.getId()), group.getName());
		}

		model.addAttribute("USERS", listUsers);
		model.addAttribute("GROUPS", listGroups);
		model.addAttribute("GROUP_MAPPING", groupMapping);
		return VIEW_NAME;
	}

	@RequestMapping(value = "/admin/users/add", method = RequestMethod.POST)
	public String addUser(@ModelAttribute UserBEForm form, BindingResult result) {
		String email = form.getEmail();
		String loginName = form.getLoginName();
		String rawPassword = form.getPassword();
		int errCode = 0;

		userFormValidator.validate(form, result);

		if (!result.hasErrors()) {
			errCode = 1;
		}

		if (errCode == 0) {
			UserBo user = new UserBo();
			user.setEmail(email);
			user.setLoginName(loginName);
			user.setLocked(false);
			user.setDisplayName(form.getDisplayName());
			user.setGroupId(form.getGroupId());
			String password = passwordEncoder.encodePassword(rawPassword,
			        saltGenerator.generateSalt(user));
			user.setPassword(password);
			usManager.createUser(user);
		}
		return "redirect:/admin/users?ecu=" + errCode;
	}
}
